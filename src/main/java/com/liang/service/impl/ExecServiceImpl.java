package com.liang.service.impl;

import com.liang.Bean.LiveVideoMask;
import com.liang.Bean.LocalvideoMask;
import com.liang.common.util.ExecUtil;
import com.liang.common.util.MpcUtil;
import com.liang.common.util.ObsUtil;
import com.liang.service.DataMaskService;
import com.liang.service.IExecService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


@Service
@Slf4j
public class ExecServiceImpl implements IExecService {


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
            log.info("脚本执行结果" + res);
            if (res.equals("false") || res.equals("Time out") || res.equals("")){
                // 向数据库写入信息
                log.info("脚本执行出错");
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

                } else {
                    String filePath = liveVideoMask.getOutFilePath() + liveVideoMask.getOutFilename() + ".avi";
                    log.info("脱敏视频保存路径" + filePath);
                    File file = new File(filePath);
                    if (!file.exists() || !file.isFile()) {
                        log.info("脱敏视频文件生成失败");

                    }
                    long fileSize = file.length();
                    if (ObsUtil.exitBucket(InBucketName)) {
                        log.info("obs桶存在");
                        // 这里上传视频可能会超时
                        String uploadFileName = liveVideoMask.getOutFilename() + ".avi";
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
                            Long trancoding = MpcUtil.createTranscodingTask(InBucketName, OutBucketName, HuaWeiLocation, uploadFileName, "a/", liveVideoMask.getOutFilename());
                            if (trancoding != -1) {
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
                                time.schedule(task, 3000, 10000);
                                log.info("脱敏完成");
                            }
                        } else {
                            log.info("文件上传失败");
                        }
                    }

                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
}
