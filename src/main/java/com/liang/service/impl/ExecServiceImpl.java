package com.liang.service.impl;

import com.liang.common.util.ExecUtil;
import com.liang.common.util.MpcUtil;
import com.liang.common.util.ObsUtil;
import com.liang.service.IExecService;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author liang
 * @Package com.liang.service.impl
 * @date 2022/10/25 16:29
 */
@Service
@Slf4j
public class ExecServiceImpl implements IExecService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${uploadFolder}")
    private String uploadFolder;

    @Value("${LocalCodePath}")
    private String LocalCodePath;
    
    @Value("${LiveCodePath}")
    private String LiveCodePath;

    // 算法允许传入的参数
    private static final String[] checkmodelList = {"person","plate","sign","qrcode","idcard","nake","all"};
    // 算法在什么设备上运行
    private static final String[] checkuseMethod = {"gpu","cpu"};


    @Override
    public String isFile(String md5Id) {
        // 去redis中找该md5Id是否存在 && 访问文件是否存在
//        boolean isfile = redisTemplate.hasKey(md5Id);
        String fileDirPath = uploadFolder + md5Id;
        log.info("源文件存放目录",fileDirPath);
        File file = new File(fileDirPath);
        File[] array = file.listFiles();
        if(file.isDirectory() && array.length == 1) {
            // 获取这个文件名字
            if(array[0].isFile()) {
                log.info("源视频文件名称" + array[0].getName());
                return fileDirPath + File.separator + array[0].getName();
            }else {
                return "";
            }
        }else {
            return "";
        }
    }

    @Override
    public Boolean checkParameters(String[] modelList, String useMethod) {
        HashSet<String> hset= new HashSet<>();
        // hset stores all the values of checkmodelList
        for(int i = 0; i < checkmodelList.length; i++)
        {
            if(!hset.contains(checkmodelList[i]))
                hset.add(checkmodelList[i]);
        }
        for(int i = 0; i < modelList.length; i++)
        {
            if(!hset.contains(modelList[i]))
                return false;
        }
        if (useMethod.equals("cpu") || useMethod.equals("gpu")) {
            return true;
        }else {
            return false;
        }
    }


    @Override
    public boolean localVideoMask(String videoPath,String[] modelList,String useMethod) {

        // 根据md5Id 和 uploadFolder 去找到对应的离线视频文件
        File file = new File(videoPath);
        String outPath = "/home/ysjs3/java/output/" + file.getName();
        log.info("原视频存放地址" + videoPath);
        log.info("视频保存地址" + outPath);
        log.info("代码地址" + LocalCodePath );
        // String usecmd = "python /home/ysjs3/java/code/test.py -i /home/ysjs3/java/upfile/0198c25790ad81c091d8d0e5c850a0ed/person3.mp4 -o /home/ysjs3/java/output/person3.mp4 --model_list person --device cpu";
        String[] std = new String[] {"python",LocalCodePath,"-i",videoPath,"-o",outPath,"--model_list"};
        // 计算出命令行需要的参数量
        int cmd_len = modelList.length + std.length + 2;
        String[] cmdStr = new String[cmd_len];
        for (int i = 0; i < std.length; i++) {
            cmdStr[i] = std[i];
        }
        for (int i = std.length; i < (std.length + modelList.length); i++) {
            cmdStr[i] = modelList[i - std.length];
        }
        cmdStr[cmd_len -2] = "--device";
        cmdStr[cmd_len -1] = useMethod;
        try {
            String res = ExecUtil.exec(cmdStr, 200);
            log.info("脚本执行结果" + res);
            if (res.equals("false") || res.equals("Time out") || res.equals("")){
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }


    @Override
    public boolean liveVideoMask(String stream_url, Long times_sec, String out_file_path,String filename,String[] modelList,String useMethod) {
        /*
            异步操作，同一个类中调用异步方法不生效
            1.启动python脚本
            2.python脚本返回脱敏完成信息
            3.调用obs接口将本地视频文件上传到华为云obs
            4.上传完成后调用转码接口执行转码任务，执行转码，定时器获取转码任务状态，最后将转码情况写入数据库。
            5.提供一个前端查询转码情况的接口，对接数据库
         */
        log.info(String.valueOf(times_sec));
        String[] std = new String[] {"python",LiveCodePath,"-i",stream_url,"-o", out_file_path,"--time",String.valueOf(times_sec),"--filename",filename,"--model_list"};
        // 计算出命令行需要的参数量
        int cmd_len = modelList.length + std.length + 2;
        String[] cmdStr = new String[cmd_len];
        for (int i = 0; i < std.length; i++) {
            cmdStr[i] = std[i];
        }
        for (int i = std.length; i < (std.length + modelList.length); i++) {
            cmdStr[i] = modelList[i - std.length];
        }
        cmdStr[cmd_len -2] = "--device";
        cmdStr[cmd_len -1] = useMethod;
        try {
            String res = ExecUtil.exec(cmdStr, 1000);
            log.info("脚本执行结果" + res);
            if (res.equals("false") || res.equals("Time out") || res.equals("")){
                log.info("执行失败");
                return false;
            }else {
                String filePath = out_file_path + filename + ".avi";
                log.info("脱敏视频保存路径" + filePath);
                File file = new File(filePath);
                if (!file.exists() || !file.isFile()) {
                    log.info("脱敏视频文件生成失败");
                    return false;
                }
                Long fileSize = file.length();
                if(ObsUtil.exitBucket("idata-video")){
                    log.info("obs桶存在");
                    // 这里上传视频可能会超时
                    String uploadFileName = filename + ".avi";
                    // 这边需要获取文件的大小来选择是普通上传还是分块上传
                    boolean isupload = false;
                    if(fileSize > 5 * 1024 * 1024L) {
                        String uploadId = ObsUtil.InitiateMultipartUploadRequestTask("idata-video",uploadFileName);
                        // 分块上传的同时进行了文件合并操作
                        // 网络好使用异步上传 AsynFileUpload，不好使用分块上传 UploadPartFile
                        isupload = ObsUtil.AsynFileUpload("idata-video",uploadFileName,uploadId,fileSize,filePath);
                    }else {
                        isupload = ObsUtil.uploadFile("idata-video",uploadFileName,filePath);
                    }
                    if(isupload) {
                        Long trancoding = MpcUtil.createTranscodingTask("idata-video","idata-jia","cn-east-3",uploadFileName,"a/",filename);
                        if(trancoding != -1) {
                            TimerTask task = new TimerTask() {
                                public void run() {
                                    String isStaus = MpcUtil.getTaskStatus(trancoding);
                                    if (isStaus.equals("SUCCEEDED") || isStaus.equals("FAILED")) {
                                        log.info("线程停止");
                                        // 把转码后的视频下载回本地
//                                        boolean download = ObsUtil.downloadFile("idata-jia",filename,out_file_path + filename);
                                        // 向数据库写入一条信息，表明转码完成。
                                        // 这边执行一个函数
                                        this.cancel();
                                    }
                                }
                            };
                            Timer time = new Timer();
                            time.schedule(task,3000,10000);
                            log.info("脱敏完成");
                            return true;
                        }else return false;
                    }else {
                        log.info("文件上传失败");
                        return false;
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean isRtmpStream(String rtspUrl) {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(rtspUrl);
        try {
            // start中调用了一系列的解析操作
            grabber.start();
            Frame frame = grabber.grabImage();
            if (frame != null) {
                return true;
            }else {
                return false;
            }
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
            return false;
        }
    }


//    @Override
//    public boolean liveVideoMask(String stream_url, Long times_sec, String out_file_path, String file_format, boolean is_audio) {
//        // 获取视频源
//        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(stream_url);
//        FFmpegFrameRecorder recorder = null;
//        try {
//            // start中调用了一系列的解析操作
//            grabber.start();
//            Frame frame = grabber.grabImage();
//            if (frame != null) {
//                //保存到本地的文件
//                File outFile = new File(out_file_path);
//                // 如果文件不存在或者不是一个文件 则根据文件的路径创建一个文件
//                if (out_file_path.isEmpty() || !outFile.exists() || outFile.isFile()) {
//                    log.info("创建对应文件");
//                    outFile.createNewFile();
//                } else {
//                    System.out.println("输出文件无法创建");
//                    return false;
//                }
//                // 流媒体输出地址，分辨率（长，高），是否录制音频（0:不录制/1:录制）
//                recorder = new FFmpegFrameRecorder(out_file_path, frame.imageWidth, frame.imageHeight, is_audio ? 1 : 0);
//                recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);//直播流格式
//                recorder.setFormat(file_format);//录制的视频格式
//                recorder.setFrameRate(25);//帧数
//
//                recorder.start();//开始录制
//                // 计算结束时间
//                long endTime = System.currentTimeMillis() + times_sec * 1000;
//                // 如果没有到录制结束时间并且获取到了下一帧则继续录制
//                while ((System.currentTimeMillis() < endTime) && (frame != null)) {
//                    recorder.record(frame);//录制
//                    frame = grabber.grabFrame();//获取下一帧
//                }
//                recorder.record(frame);
//            }
//        } catch (FrameGrabber.Exception e) {
//            e.printStackTrace();
//        } catch (FrameRecorder.Exception e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            //停止录制
//            if (grabber != null ) {
//                try {
//                    grabber.stop();
//                } catch (FrameGrabber.Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            if (recorder != null) {
//                try {
//                    recorder.stop();
//                } catch (FrameRecorder.Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            System.out.println("录制完成，录制时长：" + times_sec + "秒(0为没有限制录制时长)");
//        }
//        return true;
//    }
//
//    @Override
//    public boolean readVideoFrame() {
//        File file = new File("D:\\vodeiPredit\\20210528135144\\1001.mp4");
//        OpenCVFrameConverter.ToMat cvCoreMat = new OpenCVFrameConverter.ToMat();
//        try (FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(file);){
//            fFmpegFrameGrabber.start();
//            Frame frame = null;
//            int frameNum = 0;
//            //读取每帧视频
//            while ((frame = fFmpegFrameGrabber.grabImage()) != null) {
//                opencv_core.Mat mat = cvCoreMat.convertToMat(frame);
//                //保存图片
//                opencv_imgcodecs.imwrite("Frame"+frameNum+".jpg",mat);
//                frameNum++;
//            }
//        } catch (FrameGrabber.Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

}
