package com.liang.service.impl;

import com.liang.common.util.ExecUtil;
import com.liang.service.IExecService;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.bytedeco.javacv.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

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

    @Value("${codePath}")
    private String codePath;

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
        String fileDirPath = videoPath;
        File file = new File(fileDirPath);
        String outPath = "/home/ysjs3/java/output/" + file.getName();
        log.info("原视频存放地址" + fileDirPath);
        log.info("视频保存地址" + outPath);
        log.info("代码地址" + codePath );
        // String usecmd = "python /home/ysjs3/java/code/test.py -i /home/ysjs3/java/upfile/0198c25790ad81c091d8d0e5c850a0ed/person3.mp4 -o /home/ysjs3/java/output/person3.mp4 --model_list person --device cpu";
        String[] std = new String[] {"python",codePath,"-i",fileDirPath,"-o",outPath,"--model_list"};
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
//            String res = ExecUtil.exec(str,200);
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
    public boolean liveVideoMask(String stream_url, Long times_sec, String out_file_path, String file_format, boolean is_audio) {

        return true;
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
