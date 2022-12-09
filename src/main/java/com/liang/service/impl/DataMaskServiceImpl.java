package com.liang.service.impl;

import com.liang.Bean.CheckTask;
import com.liang.Bean.LiveVideoMask;
import com.liang.Bean.LocalMask;
import com.liang.Bean.MaskTask;
import com.liang.Dao.LiveVideoMaskDao;
import com.liang.Dao.LocalMaskDao;
import com.liang.Dao.MaskTaskDao;
import com.liang.common.util.ObsUtil;
import com.liang.service.DataMaskService;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * @author liang
 * @Package com.liang.service.impl
 * @date 2022/10/25 16:29
 */
@Service
@Slf4j
public class DataMaskServiceImpl implements DataMaskService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private IExecService execService;

    @Autowired
    private LocalMaskDao localMaskDao;

    @Autowired
    private LiveVideoMaskDao liveVideoMaskDao;

    @Autowired
    private MaskTaskDao maskTaskDao;

    @Value("${uploadFolder}")
    private String uploadFolder;

    @Value("${LocalCodePath}")
    private String LocalCodePath;
    
    @Value("${LiveCodePath}")
    private String LiveCodePath;

    @Value("${InBucketName}")
    private String InBucketName;

    // 算法允许传入的参数
    private static final String[] checkmodelList = {"person","plate","sign","qrcode","idcard","nake","all"};
    // 算法在什么设备上运行
    private static final String[] checkuseMethod = {"gpu","cpu"};


    @Override
    public Boolean isFile(String videoPath) {
        // 去redis中找该md5Id是否存在 && 访问文件是否存在
//        boolean isfile = redisTemplate.hasKey(md5Id);
//        String fileDirPath = uploadFolder + md5Id;
//        log.info("源文件存放目录",fileDirPath);
//        File file = new File(fileDirPath);
//        File[] array = file.listFiles();
//        if(file.isDirectory() && array.length == 1) {
//            // 获取这个文件名字
//            if(array[0].isFile()) {
//                log.info("源视频文件名称" + array[0].getName());
//                return fileDirPath + File.separator + array[0].getName();
//            }else {
//                return "";
//            }
//        }else {
//            return "";
//        }
        File file = new File(videoPath);
        if(file.exists() && file.length() != 0) {
            return true;
        }else {
            return false;
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
        if (useMethod.equals("cpu") || useMethod.equals("0")) {
            return true;
        }else {
            return false;
        }
    }


    @Override
    public boolean localVideoMask(LocalMask localvideoMask) {
        log.info("原视频存放地址" + localvideoMask.getOriginPath());
        log.info("视频保存地址" + localvideoMask.getMaskPath());
        log.info("代码地址" + LocalCodePath );
        // String usecmd = "python /home/ysjs3/java/code/test.py -i /home/ysjs3/java/upfile/0198c25790ad81c091d8d0e5c850a0ed/person3.mp4 -o /home/ysjs3/java/output/person3.mp4 --model_list person --device cpu";
        String[] std = new String[] {"python",LocalCodePath,"-i",localvideoMask.getOriginPath(),"-o",localvideoMask.getMaskPath(),"--model_list"};
        // 计算出命令行需要的参数量
        int cmd_len = localvideoMask.getModelList().length + std.length + 2;
        String model = "";
        String[] cmdStr = new String[cmd_len];
        for (int i = 0; i < std.length; i++) {
            cmdStr[i] = std[i];
        }
        for (int i = std.length; i < (std.length + localvideoMask.getModelList().length); i++) {
            cmdStr[i] = localvideoMask.getModelList()[i - std.length];
            model = model + localvideoMask.getModelList()[i - std.length] + ",";
        }
        cmdStr[cmd_len -2] = "--device";
        cmdStr[cmd_len -1] = localvideoMask.getUseMethod();
        localvideoMask.setModel(model);
        Date timer = new Date();
        log.info("任务执行开启时间" + timer);
        localvideoMask.setStartTime(timer);
        localvideoMask.setTaskStatus(0);
        // 0: 存在，1：删除
        localvideoMask.setIsdelete(0);
        // 向数据库新增数据
        int x = localMaskDao.insert(localvideoMask);
        log.info("数据库返回信息" + localvideoMask.getTaskId());
        // 执行异步操作
        execService.localVideoMask(cmdStr,localvideoMask);
        return true;
    }


    @Override
    public boolean liveVideoMask(LiveVideoMask liveVideoMask) throws IOException {
        /*
            异步操作，同一个类中调用异步方法不生效
            1.启动python脚本
            2.python脚本返回脱敏完成信息
            3.调用obs接口将本地视频文件上传到华为云obs
            4.上传完成后调用转码接口执行转码任务，执行转码，定时器获取转码任务状态，最后将转码情况写入数据库。
            5.提供一个前端查询转码情况的接口，对接数据库
         */
        // 在当前的数量上+1，代表新任务的文件夹
        Integer user_task_count = liveVideoMaskDao.GetUserTaskCountByUserId(liveVideoMask) + 1;

        String obsPath = liveVideoMask.getUserId() + "/" + user_task_count + "/";
        String live_user_task_path = uploadFolder + obsPath;
        // 创建文件夹
        Path path = Paths.get(live_user_task_path);
        Path pathCreate = Files.createDirectories(path);
        log.info("文件夹");
        liveVideoMask.setOutFilePath(live_user_task_path);
        String[] std = new String[] {"python",LiveCodePath,"-i",liveVideoMask.getStreamUrl(),"-o", liveVideoMask.getOutFilePath(),"--time",String.valueOf(liveVideoMask.getTimes_sec()),"--filename", liveVideoMask.getOutFilename(),"--model_list"};
        // 计算出命令行需要的参数量
        int cmd_len = liveVideoMask.getModelList().length + std.length + 2;
        String model = "";
        String[] cmdStr = new String[cmd_len];
        for (int i = 0; i < std.length; i++) {
            cmdStr[i] = std[i];
        }
        for (int i = std.length; i < (std.length + liveVideoMask.getModelList().length); i++) {
            cmdStr[i] = liveVideoMask.getModelList()[i - std.length];
            model = model + liveVideoMask.getModelList()[i - std.length] + ",";
        }
        cmdStr[cmd_len -2] = "--device";
        cmdStr[cmd_len -1] = liveVideoMask.getUseMethod();

        // 向obs创建文件夹
        ObsUtil.CreateFolder(InBucketName,obsPath);
        // 向数据库插入数据
        liveVideoMask.setObsPath(obsPath);
        liveVideoMask.setModel(model);
        Date timer = new Date();
        liveVideoMask.setStartTime(timer);
        liveVideoMask.setTaskStatus(0);
        liveVideoMask.setIsdelete(0);
        liveVideoMaskDao.insert(liveVideoMask);
        log.info("数据库返回信息" + liveVideoMask.getTaskId());
        // 执行异步操作
        execService.liveVideoMask(cmdStr,liveVideoMask);
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

    // 根据userid，和任务类型id获取任务情况
    @Override
    public List<LiveVideoMask> getLiveTaskPosition(CheckTask checkTask) {
        List<LiveVideoMask> liveVideoMaskList =  liveVideoMaskDao.GetUserTaskByUserId(checkTask);
        return liveVideoMaskList;
    }

    @Override
    public List<LocalMask> getLocalTaskPosition(CheckTask checkTask) {
        List<LocalMask> localvideoMaskList =  localMaskDao.GetUserTaskByUserId(checkTask);
        return localvideoMaskList;
    }

    @Override
    public int deleteTask(Integer userId, Integer taskId, Integer typeId) {
        if(typeId == 0) {
            return localMaskDao.deleteTask(userId,taskId);
        }else {
            return liveVideoMaskDao.deleteTask(userId,taskId);
        }
    }

    @Override
    public int createMaskTask(MaskTask maskTask) {
        return maskTaskDao.createMaskTask(maskTask);
    }


    @Override
    public int updateMaskTask(MaskTask maskTask) {
        return maskTaskDao.updateMaskTask(maskTask) ;
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
