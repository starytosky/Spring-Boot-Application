package com.liang.service.impl;

import com.liang.Bean.LiveVideoMask;
import com.liang.Bean.LocalvideoMask;
import com.liang.Dao.LiveVideoMaskDao;
import com.liang.Dao.LocalVideoMaskDao;
import com.liang.common.util.ExecUtil;
import com.liang.common.util.MpcUtil;
import com.liang.common.util.ObsUtil;
import com.liang.service.DataMaskService;
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
    private LocalVideoMaskDao localVideoMaskDao;

    @Autowired
    private LiveVideoMaskDao liveVideoMaskDao;

    @Value("${InBucketName}")
    private String InBucketName;

    @Value("${OutBucketName}")
    private String OutBucketName;

    @Value("${HuaWeiLocation}")
    private String HuaWeiLocation;

    @Override
    @Async
    public void localVideoMask(String[] cmdStr, LocalvideoMask localvideoMask) {
        try {
            String res = ExecUtil.exec(cmdStr, 200);
            Date endTime = new Date();
            localvideoMask.setEndTime(endTime);
            log.info("脚本执行结果" + res);
            if (res.equals("false") || res.equals("Time out") || res.equals("")){
                // 向数据库写入信息
                log.info("脚本执行出错");
                localvideoMask.setTaskStatus(2);
                localVideoMaskDao.updateLocalVieoMaskById(localvideoMask);
            }else {
                log.info("任务执行成功");
                localvideoMask.setTaskStatus(1);
                localVideoMaskDao.updateLocalVieoMaskById(localvideoMask);
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
                String res = ExecUtil.exec(cmdStr, 1000);
                log.info("脚本执行结果" + res);
                if (res.equals("false") || res.equals("Time out") || res.equals("")) {
                    log.info("执行失败");
                    setTaskStatus(1,liveVideoMask);

                } else {
                    String filePath = liveVideoMask.getOutFilePath() + liveVideoMask.getOutFilename() + ".avi";
                    log.info("脱敏视频保存路径" + filePath);
                    File file = new File(filePath);
                    if (!file.exists() || !file.isFile()) {
                        log.info("脱敏视频文件生成失败");
                        setTaskStatus(1,liveVideoMask);
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
                            setTaskStatus(2,liveVideoMask);
                            Long trancoding = MpcUtil.createTranscodingTask(InBucketName, OutBucketName, HuaWeiLocation, uploadFileName, liveVideoMask.getObsPath(), liveVideoMask.getOutFilename());
                            if (trancoding != -1) {
                                setTaskStatus(8,liveVideoMask);
                                TimerTask task = new TimerTask() {
                                    public void run() {
                                        String isStaus = MpcUtil.getTaskStatus(trancoding);
                                        if (isStaus.equals("SUCCEEDED") || isStaus.equals("FAILED")) {
                                            log.info("线程停止");
                                            if (isStaus.equals("SUCCEEDED")) {
                                                setTaskStatus(4,liveVideoMask);
                                            }else {
                                                setTaskStatus(5,liveVideoMask);
                                            }
                                            // 把转码后的视频下载回本地
                                            String objectName = liveVideoMask.getObsPath() + liveVideoMask.getOutFilename()+".mp4";
                                            boolean download = ObsUtil.downloadFile(OutBucketName,objectName,liveVideoMask.getOutFilePath() + liveVideoMask.getOutFilename()+".mp4");
                                            // 向数据库写入一条信息，表明转码完成。
                                            if(download) {
                                                log.info("文件下载完成");
                                                setTaskStatus(6,liveVideoMask);
                                            }else {
                                                log.info("文件下载失败");
                                                setTaskStatus(7,liveVideoMask);
                                            }
                                            // 这边执行一个函数
                                            log.info("脱敏完成");
                                            this.cancel();
                                        }
                                    }
                                };
                                Timer time = new Timer();
                                time.schedule(task, 3000, 10000);
                            }else {
                                log.info("转码任务创建失败");
                                setTaskStatus(9,liveVideoMask);
                            }
                        } else {
                            log.info("文件上传失败");
                            setTaskStatus(3,liveVideoMask);
                        }
                    }

                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void setTaskStatus(Integer status, LiveVideoMask liveVideoMask) {
            Date endTime = new Date();
            liveVideoMask.setEndTime(endTime);
            liveVideoMask.setTaskStatus(status);
            liveVideoMaskDao.updateLiveVieoMaskById(liveVideoMask);
        }
}
