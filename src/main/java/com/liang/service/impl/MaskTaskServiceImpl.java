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

    @Value("${OutBucketName}")
    private String OutBucketName;

    // ???????????????????????????
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
        if (maskTask.getMethod().toLowerCase().equals("cpu") || maskTask.getMethod().toLowerCase().equals("gpu")) {
            return true;
        }else {
            return false;
        }
    }


    @Override
    public boolean localVideoMask(MaskTask maskTask,String ruleDesc) throws IOException {
        // ruleDesc ??????????????????python??????
        LocalMask localvideoMask = setLocalMask(maskTask);
        localMaskMapper.insert(localvideoMask);
        log.info("?????????????????????" + localvideoMask.getExecId());
        // ?????????????????????????????????
        String localMaskFilePath = uploadFolder + File.separator + maskTask.getUserId() + File.separator + "Local" + File.separator + maskTask.getTaskId() + File.separator + localvideoMask.getExecId();
        // ???????????????
        File originPath = new File(maskTask.getDataPath().trim());
        log.info(originPath.getName());
        // ???????????????
        Path path = Paths.get(localMaskFilePath);
        Path pathCreate = Files.createDirectories(path);
        localvideoMask.setMaskPath(pathCreate + File.separator + originPath.getName());
        log.info("????????????????????????" + pathCreate);
        log.info("?????????????????????" + localvideoMask.getOriginPath());
        log.info("????????????" + LocalCodePath );
        // String usecmd = "python /home/ysjs3/java/code/test.py -i /home/ysjs3/java/upfile/0198c25790ad81c091d8d0e5c850a0ed/person3.mp4 -o /home/ysjs3/java/output/person3.mp4 --model_list person --device cpu";
        String[] std = new String[] {"python",LocalCodePath,"-i",localvideoMask.getOriginPath(),"-o",localvideoMask.getMaskPath(),"--model_list"};
        // ????????????????????????????????????
        String[] modelList = localvideoMask.getModel().split(",");
        int cmd_len = modelList.length + std.length + 2;
        String[] cmdStr = setCmd(std,modelList,cmd_len,localvideoMask.getMethod());
        String logName = "local_" + localvideoMask.getExecId();
        localvideoMask.setLogPath(maskLogPath + logName+".log");
        // ????????????
        QueryWrapper<LocalMask> wrapper = new QueryWrapper<>();
        wrapper.eq("exec_id",localvideoMask.getExecId());
        localMaskMapper.update(localvideoMask,wrapper);
        // ??????????????????
        maskTaskDao.updateTaskStatus(localvideoMask.getTaskId(), 0);
        // ??????????????????
        execService.localVideoMask(cmdStr,localvideoMask);
        return true;
    }

    public LocalMask setLocalMask(MaskTask maskTask) {
        LocalMask localvideoMask = new LocalMask();
        // ??????userid local taskid execid ?????????????????????
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
        log.info("????????????????????????" + timer);
        localvideoMask.setStartTime(timer);
        localvideoMask.setTaskStatus(0);
        // 0: ?????????1?????????
        localvideoMask.setIsdelete(0);
        return localvideoMask;
    }


    @Override
    public boolean liveVideoMask(MaskTask maskTask,String ruleDesc) throws IOException {
        /*
            ?????????????????????????????????????????????????????????
            1.??????python??????
            2.python??????????????????????????????
            3.??????obs?????????????????????????????????????????????obs
            4.????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            5.???????????????????????????????????????????????????????????????
         */
        LiveVideoMask liveVideoMask = setLiveMask(maskTask);
        // ??????taskid????????????????????? ???????????????+1 ????????????
        Integer user_task_count = liveVideoMaskDao.GetUserTaskCountByUserId(liveVideoMask) + 1;

        String obsPath = liveVideoMask.getUserId() + "/" + liveVideoMask.getTaskId() + "/" + user_task_count + "/";
        String live_user_task_path = uploadFolder + obsPath;
        // ???????????????
        Path path = Paths.get(live_user_task_path);
        Path pathCreate = Files.createDirectories(path);
        log.info("?????????");
        liveVideoMask.setOutFilePath(live_user_task_path);
        String[] std = new String[] {"python",LiveCodePath,"-i",liveVideoMask.getStreamUrl(),"-o", liveVideoMask.getOutFilePath(),"--filename", liveVideoMask.getOutFilename(),"--model_list"};
        String[] modelList = liveVideoMask.getModel().split(",");
        // ????????????????????????????????????
        int cmd_len = modelList.length + std.length + 2;
        // ????????????????????????
        String[] cmdStr = setCmd(std,modelList,cmd_len,liveVideoMask.getMethod());
        liveVideoMask.setObsPath(obsPath);
        // ???obs???????????????
        if(ObsUtil.CreateFolder(InBucketName,obsPath) && ObsUtil.CreateFolder(OutBucketName,liveVideoMask.getObsPath())) {
            // ????????????????????????
            String logName = "live_"+ liveVideoMask.getExecId();
            liveVideoMask.setLogPath(maskLogPath + logName+".log");
            liveVideoMaskMapper.insert(liveVideoMask);
            log.info("?????????????????????" + liveVideoMask.getExecId());
            // ??????????????????
            execService.liveVideoMask(cmdStr,liveVideoMask);
            return true;
        }else return false;
    }
    // ??????????????????
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
        // ??????userid local taskid execid ?????????????????????
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
        log.info("????????????????????????" + timer);
        liveVideoMask.setStartTime(timer);
        liveVideoMask.setIsType(1);
        // 0: ?????????1?????????
        liveVideoMask.setIsdelete(0);
        return liveVideoMask;
    }

    @Override
    public boolean isRtmpStream(String rtspUrl) {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(rtspUrl);
        try {
            // start????????????????????????????????????
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
        // ??????????????????????????????
        if(maskrule == null) {
            return "";
        }else {
            if(maskrule.getIsupload()==0) { //???????????????????????????
                return maskrule.getRuleDesc();
            }else { //???????????????????????????
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
            // ??????????????????????????? ????????????????????? ??????????????????????????????????????????????????????-1????????????????????????
            if(gpuInfos.get(x).getUsageRate() > 0.85) {
                return -1;
            }
            return x;
        } catch (Exception e) {
            log.error("??????gpu??????error , message : {}", e.getMessage(), e);
            return -2;
        }
    }


//    @Override
//    public boolean liveVideoMask(String stream_url, Long times_sec, String out_file_path, String file_format, boolean is_audio) {
//        // ???????????????
//        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(stream_url);
//        FFmpegFrameRecorder recorder = null;
//        try {
//            // start????????????????????????????????????
//            grabber.start();
//            Frame frame = grabber.grabImage();
//            if (frame != null) {
//                //????????????????????????
//                File outFile = new File(out_file_path);
//                // ????????????????????????????????????????????? ??????????????????????????????????????????
//                if (out_file_path.isEmpty() || !outFile.exists() || outFile.isFile()) {
//                    log.info("??????????????????");
//                    outFile.createNewFile();
//                } else {
//                    System.out.println("????????????????????????");
//                    return false;
//                }
//                // ????????????????????????????????????????????????????????????????????????0:?????????/1:?????????
//                recorder = new FFmpegFrameRecorder(out_file_path, frame.imageWidth, frame.imageHeight, is_audio ? 1 : 0);
//                recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);//???????????????
//                recorder.setFormat(file_format);//?????????????????????
//                recorder.setFrameRate(25);//??????
//
//                recorder.start();//????????????
//                // ??????????????????
//                long endTime = System.currentTimeMillis() + times_sec * 1000;
//                // ???????????????????????????????????????????????????????????????????????????
//                while ((System.currentTimeMillis() < endTime) && (frame != null)) {
//                    recorder.record(frame);//??????
//                    frame = grabber.grabFrame();//???????????????
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
//            //????????????
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
//            System.out.println("??????????????????????????????" + times_sec + "???(0???????????????????????????)");
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
//            //??????????????????
//            while ((frame = fFmpegFrameGrabber.grabImage()) != null) {
//                opencv_core.Mat mat = cvCoreMat.convertToMat(frame);
//                //????????????
//                opencv_imgcodecs.imwrite("Frame"+frameNum+".jpg",mat);
//                frameNum++;
//            }
//        } catch (FrameGrabber.Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

}
