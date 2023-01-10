package com.liang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liang.Mapper.*;
import com.liang.Rep.*;
import com.liang.Res.ExecRecordInfo;
import com.liang.Res.LocalData;
import com.liang.common.util.IdRandomUtils;
import com.liang.common.util.ObsUtil;
import com.liang.common.util.Tool;
import com.liang.service.MaskTaskService;
import com.liang.service.IExecService;
import com.liang.service.MaskRuleService;
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
import java.util.Locale;

import static com.liang.common.util.Tool.convertXmlToGpuObject;
import static com.liang.common.util.Tool.getGpuXmlInfo;

/**
 * @author liang
 * @Package com.liang.service.impl
 * @date 2022/10/25 16:29
 */
@Service
@Slf4j
public class MaskTaskServiceImpl implements MaskTaskService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private IExecService execService;

    @Autowired
    private MaskRuleService maskRuleService;

    @Autowired
    private LocalMaskDao localMaskDao;

    @Autowired
    private LocalMaskMapper localMaskMapper;

    @Autowired
    private LiveVideoMaskDao liveVideoMaskDao;

    @Autowired
    private LiveVideoMaskMapper liveVideoMaskMapper;

    @Autowired
    private MaskTaskDao maskTaskDao;

    @Autowired
    private MaskTaskMapper maskTaskMapper;

    @Autowired
    private MaskRuleMapper maskRuleMapper;

    @Value("${uploadFolder}")
    private String uploadFolder;

    @Value("${LocalCodePath}")
    private String LocalCodePath;
    
    @Value("${LiveCodePath}")
    private String LiveCodePath;

    @Value("${InBucketName}")
    private String InBucketName;

    @Value("${maskLogPath}")
    private String maskLogPath;

    // 算法允许传入的参数
    private static final String[] checkmodelList = {"person","plate","sign","qrcode","idcard","nake","all"};


    @Override
    public Boolean isFile(String videoPath) {
        File file = new File(videoPath);
        if(file.exists() && file.length() != 0) {
            return true;
        }else {
            return false;
        }
    }

    @Override
    public Boolean checkParameters(MaskTask maskTask) {
        String limitcontent = maskRuleService.getMaskRuleById(maskTask.getRuleId()).getLimitContent();
        String[] modelList = limitcontent.split(",");
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
        if (maskTask.getMethod().toLowerCase().equals("cpu")) {
            return true;
        }else {
            return false;
        }
    }


    @Override
    public boolean localVideoMask(MaskTask maskTask,String ruleDesc) throws IOException {
        // ruleDesc 用于之后构建python命令
        LocalMask localvideoMask = setLocalMask(maskTask);
        localMaskMapper.insert(localvideoMask);
        log.info("数据库返回信息" + localvideoMask.getExecId());
        // 创建文件脱敏后存放地址
        String localMaskFilePath = uploadFolder + File.separator + maskTask.getUserId() + File.separator + "Local" + File.separator + maskTask.getTaskId() + File.separator + localvideoMask.getExecId();
        // 获取文件名
        File originPath = new File(maskTask.getDataPath().trim());
        log.info(originPath.getName());
        // 创建文件夹
        Path path = Paths.get(localMaskFilePath);
        Path pathCreate = Files.createDirectories(path);
        localvideoMask.setMaskPath(pathCreate + File.separator + originPath.getName());
        log.info("脱敏文件存放地址" + pathCreate);
        log.info("原视频存放地址" + localvideoMask.getOriginPath());
        log.info("代码地址" + LocalCodePath );
        // String usecmd = "python /home/ysjs3/java/code/test.py -i /home/ysjs3/java/upfile/0198c25790ad81c091d8d0e5c850a0ed/person3.mp4 -o /home/ysjs3/java/output/person3.mp4 --model_list person --device cpu";
        String[] std = new String[] {"python",LocalCodePath,"-i",localvideoMask.getOriginPath(),"-o",localvideoMask.getMaskPath(),"--model_list"};
        // 计算出命令行需要的参数量
        String[] modelList = localvideoMask.getModel().split(",");
        int cmd_len = modelList.length + std.length + 2;
        String[] cmdStr = setCmd(std,modelList,cmd_len,localvideoMask.getMethod());
        String logName = "local_" + localvideoMask.getExecId();
        localvideoMask.setLogPath(maskLogPath + logName+".log");
        // 更新数据
        QueryWrapper<LocalMask> wrapper = new QueryWrapper<>();
        wrapper.eq("exec_id",localvideoMask.getExecId());
        localMaskMapper.update(localvideoMask,wrapper);
        // 更新任务状态
        maskTaskDao.updateTaskStatus(localvideoMask.getTaskId(), 0);
        // 执行异步操作
        execService.localVideoMask(cmdStr,localvideoMask);
        return true;
    }

    public LocalMask setLocalMask(MaskTask maskTask) {
        LocalMask localvideoMask = new LocalMask();
        // 根据userid local taskid execid 生成对应文件夹
        localvideoMask.setExecId("lo"+IdRandomUtils.getRandomID());
        localvideoMask.setModel( maskRuleService.getMaskRuleById(maskTask.getRuleId()).getLimitContent());
        localvideoMask.setTaskId(maskTask.getTaskId());
        localvideoMask.setUserId(maskTask.getUserId());
        localvideoMask.setRuleId(maskTask.getRuleId());
        localvideoMask.setMethodId(maskTask.getMethodId());
        localvideoMask.setTaskName(maskTask.getTaskName());
        localvideoMask.setOriginPath(maskTask.getDataPath());
        localvideoMask.setDataName(localMaskDao.getResourceNameByResourceId(maskTask.getDataId()));
        localvideoMask.setIsType(0);
        localvideoMask.setMethod(maskTask.getMethod().toLowerCase());
        Date timer = new Date();
        log.info("任务执行开启时间" + timer);
        localvideoMask.setStartTime(timer);
        localvideoMask.setTaskStatus(0);
        // 0: 存在，1：删除
        localvideoMask.setIsdelete(0);
        return localvideoMask;
    }


    @Override
    public boolean liveVideoMask(MaskTask maskTask,String ruleDesc) throws IOException {
        /*
            异步操作，同一个类中调用异步方法不生效
            1.启动python脚本
            2.python脚本返回脱敏完成信息
            3.调用obs接口将本地视频文件上传到华为云obs
            4.上传完成后调用转码接口执行转码任务，执行转码，定时器获取转码任务状态，最后将转码情况写入数据库。
            5.提供一个前端查询转码情况的接口，对接数据库
         */
        LiveVideoMask liveVideoMask = setLiveMask(maskTask);
        // 获取taskid下的执行记录数 在此条件下+1 新建目录
        Integer user_task_count = liveVideoMaskDao.GetUserTaskCountByUserId(liveVideoMask) + 1;

        String obsPath = liveVideoMask.getUserId() + "/" + liveVideoMask.getTaskId() + "/" + user_task_count + "/";
        String live_user_task_path = uploadFolder + obsPath;
        // 创建文件夹
        Path path = Paths.get(live_user_task_path);
        Path pathCreate = Files.createDirectories(path);
        log.info("文件夹");
        liveVideoMask.setOutFilePath(live_user_task_path);
        String[] std = new String[] {"python",LiveCodePath,"-i",liveVideoMask.getStreamUrl(),"-o", liveVideoMask.getOutFilePath(),"--filename", liveVideoMask.getOutFilename(),"--model_list"};
        String[] modelList = liveVideoMask.getModel().split(",");
        // 计算出命令行需要的参数量
        int cmd_len = modelList.length + std.length + 2;
        // 生成算法调用指令
        String[] cmdStr = setCmd(std,modelList,cmd_len,liveVideoMask.getMethod());
        // 向obs创建文件夹
        ObsUtil.CreateFolder(InBucketName,obsPath);
        // 向数据库插入数据
        liveVideoMask.setObsPath(obsPath);
        String logName = "live_"+ liveVideoMask.getExecId();
        liveVideoMask.setLogPath(maskLogPath + logName+".log");
        liveVideoMaskMapper.insert(liveVideoMask);
        log.info("数据库返回信息" + liveVideoMask.getExecId());
        // 执行异步操作
        execService.liveVideoMask(cmdStr,liveVideoMask);
        return true;
    }
    // 构建命令语句
    public String[] setCmd(String[] std,String[] ModelList,Integer cmdLen,String method) {
        String[] cmdStr = new String[cmdLen];
        for (int i = 0; i < std.length; i++) {
            cmdStr[i] = std[i];
        }
        for (int i = std.length; i < (std.length + ModelList.length); i++) {
            cmdStr[i] = ModelList[i - std.length];
        }
        cmdStr[cmdLen -2] = "--device";
        cmdStr[cmdLen -1] = method;
        return cmdStr;
    }

    @Override
    public LiveVideoMask setLiveMask(MaskTask maskTask) {
        LiveVideoMask liveVideoMask = new LiveVideoMask();
        // 根据userid local taskid execid 生成对应文件夹
        liveVideoMask.setExecId("li"+IdRandomUtils.getRandomID().toString());
        liveVideoMask.setModel( maskRuleService.getMaskRuleById(maskTask.getRuleId()).getLimitContent());
        liveVideoMask.setTaskId(maskTask.getTaskId());
        liveVideoMask.setUserId(maskTask.getUserId());
        liveVideoMask.setRuleId(maskTask.getRuleId());
        liveVideoMask.setMethodId(maskTask.getMethodId());
        liveVideoMask.setTaskName(maskTask.getTaskName());
        liveVideoMask.setOutFilename(maskTask.getStreamMaskName());
        liveVideoMask.setDataName(maskTask.getStreamMaskName());
        liveVideoMask.setStreamUrl(maskTask.getStreamUrl());
        liveVideoMask.setTaskStatus(0);
        liveVideoMask.setMethod(maskTask.getMethod().toLowerCase());
        Date timer = new Date();
        log.info("任务执行开启时间" + timer);
        liveVideoMask.setStartTime(timer);
        liveVideoMask.setIsType(1);
        // 0: 存在，1：删除
        liveVideoMask.setIsdelete(0);
        return liveVideoMask;
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



    @Override
    public int createMaskTask(MaskTask maskTask) {
        maskTask.setTaskId("ms"+IdRandomUtils.getRandomID());
        return maskTaskMapper.insert(maskTask);
    }


    @Override
    public int updateMaskTask(MaskTask maskTask) {
        QueryWrapper<MaskTask> wrapper = new QueryWrapper<>();
        wrapper.eq("task_id",maskTask.getTaskId());
        return maskTaskMapper.update(maskTask,wrapper) ;
    }

    @Override
    public String getMaskRuleByRuleId(String ruleId) {
        Maskrule maskrule = maskRuleService.getMaskRuleById(ruleId);
        // 判断是字符串还是文件
        if(maskrule == null) {
            return "";
        }else {
            if(maskrule.getIsupload()==0) { //规则为定义的字符串
                return maskrule.getRuleDesc();
            }else { //规则在上传的文件里
                return maskRuleService.getRuleContent(maskrule.getRulePath());
            }
        }
    }

    @Override
    public MaskTask getMaskTaskById(String taskId) {
        return maskTaskMapper.selectById(taskId);
    }

    @Override
    public String getMaskMethodByMethodId(Integer metnodId) {
        return null;
    }

    @Override
    public List<LocalData> getLocalDataList(CheckLocalData checkLocalData) {
        return localMaskDao.selectLocalData(checkLocalData);
    }

    @Override
    public int getLocalDataCount(CheckLocalData checkLocalData) {
        return localMaskDao.LocalDataCount(checkLocalData);
    }

    @Override
    public List<MaskTask> getTaskRecord(CheckMaskTask checkMaskTask) {
        return maskTaskDao.getTaskRecord(checkMaskTask);
    }

    @Override
    public int getTaskRecordCount(CheckMaskTask checkMaskTask) {
        return maskTaskDao.getTaskRecordCount(checkMaskTask);
    }

    @Override
    public List<LocalMask> getLocalExecRecordList(CheckExecTask checkExecTask) {
        return localMaskDao.getExecRecordList(checkExecTask);
    }

    public int getLocalExecRecordListCount(CheckExecTask checkExecTask) {
        return localMaskDao.getExecRecordListCount(checkExecTask);
    }

    @Override
    public List<LiveVideoMask> getLiveExecRecordList(CheckExecTask checkExecTask) {
        return liveVideoMaskDao.getExecRecordList(checkExecTask);
    }
    public int getLiveExecRecordListCount(CheckExecTask checkExecTask) {
        return liveVideoMaskDao.getExecRecordListCount(checkExecTask);
    }

    @Override
    public ExecRecordInfo getExecRecordInfo(String userId, String execId, Integer isType) {
        ExecRecordInfo execRecordInfo;
        if(isType==0) {
            execRecordInfo = localMaskDao.getExecRecordInfo(execId);
        }else {
            execRecordInfo = liveVideoMaskDao.getExecRecordInfo(execId);
        }
        if(execRecordInfo.getIsupload() == 1) {
            execRecordInfo.setRuleDesc(maskRuleService.getRuleContent(execRecordInfo.getRulePath()));
        }
        return execRecordInfo;
    }

    @Override
    public int deleteTask(String taskID) {
        return maskTaskDao.deleteTask(taskID);
    }

    @Override
    public boolean isExecTask() {
        float x= Tool.SystemUsage();
        if( x > 0.7 ) {
            return false;
        }
        return true;
    }



    @Override
    public int isGpu() {
        try {
            String gpuXmlInfo = getGpuXmlInfo();
            List<GPUInfo> gpuInfos = convertXmlToGpuObject(gpuXmlInfo);
            float freeMaxMemory = -1;
            int x =0;
            for (int i=0;i<gpuInfos.size();i++) {
                float currentFreeMemory = Float.parseFloat(gpuInfos.get(i).getFreeMemory().split(" ")[0]);
                if(currentFreeMemory > freeMaxMemory) {
                    freeMaxMemory = currentFreeMemory;
                    x = i;
                }
            }
            // 这边找到一个最大的 然后判断使用率 如果使用率超过一定的限制那么就将返回-1表示系统资源不足
            if(gpuInfos.get(x).getUsageRate() > 0.85) {
                return -1;
            }
            return x;
        } catch (Exception e) {
            log.error("获取gpu信息error , message : {}", e.getMessage(), e);
            return -2;
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
