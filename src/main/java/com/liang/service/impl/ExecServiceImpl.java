package com.liang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liang.Mapper.*;
import com.liang.Rep.LiveVideoMask;
import com.liang.Rep.LocalMask;
import com.liang.Rep.MaskData;
import com.liang.common.util.ExecUtil;
import com.liang.common.util.IdRandomUtils;
import com.liang.common.util.MpcUtil;
import com.liang.common.util.ObsUtil;
import com.liang.service.IExecService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


@Service
@Slf4j
public class ExecServiceImpl implements IExecService {


    @Autowired
    private LocalMaskMapper localMaskMapper;

    @Autowired
    private LocalMaskDao localMaskDao;

    @Autowired
    private MaskDataMapper maskDataMapper;

    @Autowired
    private LiveVideoMaskDao liveVideoMaskDao;

    @Autowired
    private MaskTaskDao maskTaskDao;

    @Value("${InBucketName}")
    private String InBucketName;

    @Value("${OutBucketName}")
    private String OutBucketName;

    @Value("${HuaWeiLocation}")
    private String HuaWeiLocation;

    @Value("${maskLogPath}")
    private String maskLogPath;

    @Override
    @Async
    public void localVideoMask(String[] cmdStr, LocalMask localvideoMask) {
        // 操作三个表，本身的执行记录表（更新执行状态）、任务表（更新最新任务执行状态）、脱敏数据表（新增脱敏记录）
        try {
            String logName = "local_" + localvideoMask.getExecId();
            boolean isexeclocal = ExecUtil.exec(cmdStr, 100, logName);
            Date endTime = new Date();
            localvideoMask.setEndTime(endTime);
            QueryWrapper<LocalMask> wrapper = new QueryWrapper<>();
            wrapper.eq("exec_id",localvideoMask.getExecId());
            if (!isexeclocal){
                // 向数据库写入信息
                log.info("执行任务"+ localvideoMask.getExecId() +"脚本执行出错");
                localvideoMask.setTaskStatus(2);
                localMaskMapper.update(localvideoMask,wrapper);
                updateTaskStatus(0,localvideoMask.getTaskId(), localvideoMask.getExecId(),2);
                log.info("任务"+localvideoMask.getTaskId()+"状态更新完成");
            }else {
                log.info("任务"+ localvideoMask.getExecId() +"执行成功");
                // 设置执行记录状态
                localvideoMask.setTaskStatus(1);
                localMaskMapper.update(localvideoMask,wrapper);
                // 设置任务集合状态
                updateTaskStatus(0,localvideoMask.getTaskId(), localvideoMask.getExecId(),1);
                // 向脱敏数据表新增一条记录
                localSetMaskData(localvideoMask);
                log.info("任务"+ localvideoMask.getExecId() +"数据脱敏完成");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Async
    public void liveVideoMask(String[] cmdStr, LiveVideoMask liveVideoMask) {
            // 这边执行异步上传文件操作
            try {
                String logName = "live_"+ liveVideoMask.getExecId();
                boolean isexeclive = ExecUtil.exec(cmdStr, 100, logName);
                // 更新日志地址
                liveVideoMaskDao.updateLiveVieoMaskById(liveVideoMask);
                if (!isexeclive) {
                    setLiveTaskStatus(2,liveVideoMask);
                    updateTaskStatus(1, liveVideoMask.getTaskId(), liveVideoMask.getExecId(), 2);
                } else {
                    String filePath = liveVideoMask.getOutFilePath() + liveVideoMask.getOutFilename() + ".avi";
                    log.info("脱敏视频保存路径" + filePath);
                    File file = new File(filePath);
                    if (!file.exists() || !file.isFile()) {
                        log.info("脱敏视频文件生成失败");
                        setLiveTaskStatus(2,liveVideoMask);
                        updateTaskStatus(1, liveVideoMask.getTaskId(), liveVideoMask.getExecId(), 2);
                    }
                    long fileSize = file.length();
                    if (ObsUtil.exitBucket(InBucketName)) {
                        log.info("obs桶存在");
                        // 这里上传视频可能会超时
                        String uploadFileName = liveVideoMask.getObsPath() + liveVideoMask.getOutFilename() + ".avi";
                        // 创建文件夹，将视频存放到指定文件夹

                        // 这边需要获取文件的大小来选择是普通上传还是分块上传
                        boolean isupload = false;
                        if (fileSize > 5 * 1024 * 1024L) {
                            String uploadId = ObsUtil.InitiateMultipartUploadRequestTask(InBucketName, uploadFileName);
                            // 分块上传的同时进行了文件合并操作
                            // 网络好使用异步上传 AsynFileUpload，不好使用分块上传 UploadPartFile
                            isupload = ObsUtil.AsynFileUpload(InBucketName, uploadFileName, uploadId, fileSize, filePath);
                        } else {
                            isupload = ObsUtil.uploadFile(InBucketName, uploadFileName, filePath);
                        }
                        if (isupload) {
                            // 创建输出桶路径文件夹
                            ObsUtil.CreateFolder(OutBucketName,liveVideoMask.getObsPath());
                            log.info("输出桶路径文件夹创建完成");
                            Long trancoding = MpcUtil.createTranscodingTask(InBucketName, OutBucketName, HuaWeiLocation, uploadFileName, liveVideoMask.getObsPath(), liveVideoMask.getOutFilename());
                            if (trancoding != -1) {
                                TimerTask task = new TimerTask() {
                                    public void run() {
                                        String isStaus = MpcUtil.getTaskStatus(trancoding);
                                        if (isStaus.equals("SUCCEEDED") || isStaus.equals("FAILED")) {
                                            log.info("线程停止");
                                            if (isStaus.equals("SUCCEEDED")) {
                                                // 把转码后的视频下载回本地
                                                String objectName = liveVideoMask.getObsPath() + liveVideoMask.getOutFilename()+".mp4";
                                                boolean download = ObsUtil.downloadFile(OutBucketName,objectName,liveVideoMask.getOutFilePath() + liveVideoMask.getOutFilename()+".mp4");
                                                // 向数据库写入一条信息，表明转码完成。
                                                if(download) {
                                                    log.info("文件下载完成");
                                                    setLiveTaskStatus(1,liveVideoMask);
                                                    // 向数据库插入信息
                                                    liveSetMaskData(liveVideoMask);
                                                    updateTaskStatus(1, liveVideoMask.getTaskId(), liveVideoMask.getExecId(), 1);
                                                    log.info("脱敏完成");
                                                }else {
                                                    log.info("文件下载失败");
                                                    setLiveTaskStatus(2,liveVideoMask);
                                                    updateTaskStatus(1, liveVideoMask.getTaskId(), liveVideoMask.getExecId(), 2);
                                                }
                                            }else {
                                                setLiveTaskStatus(2,liveVideoMask);
                                                updateTaskStatus(1, liveVideoMask.getTaskId(), liveVideoMask.getExecId(), 2);
                                            }
                                            // 线程停止执行
                                            this.cancel();
                                        }
                                    }
                                };
                                Timer time = new Timer();
                                time.schedule(task, 3000, 10000);
                            }else {
                                log.info("转码任务创建失败");
                                setLiveTaskStatus(2,liveVideoMask);
                                updateTaskStatus(1, liveVideoMask.getTaskId(), liveVideoMask.getExecId(), 2);
                            }
                        } else {
                            log.info("文件上传失败");
                            setLiveTaskStatus(2,liveVideoMask);
                            updateTaskStatus(1, liveVideoMask.getTaskId(), liveVideoMask.getExecId(), 2);
                        }
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void setLiveTaskStatus(Integer status, LiveVideoMask liveVideoMask) {
            Date endTime = new Date();
            liveVideoMask.setEndTime(endTime);
            liveVideoMask.setTaskStatus(status);
            liveVideoMaskDao.updateLiveVieoMaskById(liveVideoMask);
        }

        public void localSetMaskData(LocalMask localvideoMask) {
            MaskData maskData = new MaskData();
            maskData.setMaskDataId("ma"+IdRandomUtils.getRandomID());
            maskData.setExecId(localvideoMask.getExecId());
            maskData.setTaskId(localvideoMask.getTaskId());
            maskData.setUserId(localvideoMask.getUserId());
            maskData.setRuleId(localvideoMask.getRuleId());
            maskData.setMethodId(localvideoMask.getMethodId());
            maskData.setDataType(0);
            maskData.setDataName(localvideoMask.getDataName());
            maskData.setMaskPath(localvideoMask.getMaskPath());
            maskData.setIsType(0);
            maskData.setMethod(localvideoMask.getMethod());
            Date endTime = new Date();
            maskData.setTime(endTime);
            maskDataMapper.insert(maskData);
        }

        public void liveSetMaskData(LiveVideoMask liveVideoMask ) {
            MaskData maskData = new MaskData();
            maskData.setMaskDataId("ma"+IdRandomUtils.getRandomID());
            maskData.setExecId(liveVideoMask.getExecId());
            maskData.setTaskId(liveVideoMask.getTaskId());
            maskData.setUserId(liveVideoMask.getUserId());
            maskData.setRuleId(liveVideoMask.getRuleId());
            maskData.setMethodId(liveVideoMask.getMethodId());
            maskData.setDataType(0);
            maskData.setMaskPath(liveVideoMask.getOutFilePath() + liveVideoMask.getOutFilename() + ".mp4");
            maskData.setDataName(liveVideoMask.getOutFilename());
            maskData.setIsType(1);
            maskData.setMethod(liveVideoMask.getMethod());
            Date endTime = new Date();
            maskData.setTime(endTime);
            maskDataMapper.insert(maskData);
        }

    public void updateTaskStatus(Integer isType,String taskId, String execId,Integer status) {
        if(isType==0) {
            if (execId.equals(localMaskDao.getRecordCountByTaskId(taskId)))
                maskTaskDao.updateTaskStatus(taskId, status);
        }else {
            if(execId.equals(liveVideoMaskDao.GetUserTaskCountByTaskId(taskId))){
                maskTaskDao.updateTaskStatus(taskId, status);
            }
        }
    }
}
