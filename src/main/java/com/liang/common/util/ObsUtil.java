package com.liang.common.util;

import com.obs.services.Log4j2Configurator;
import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ObsUtil {

    static String endPoint = "https://obs.cn-east-3.myhuaweicloud.com";
    static String ak = "8KMQOSYPO7U4ONN9OHFV";
    static String sk = "KEIfU2Y97wD0S3mXpEAbuebzbf1QsFsQs1bG3PJa";
    // 创建ObsClient实例
    static ObsClient obsClient = new ObsClient(ak, sk, endPoint);

    static Long FilePartSize = 5 * 1024 * 1024L;

    public static boolean createBucket(String bucketName,String location) {
        CreateBucketRequest request = new CreateBucketRequest();
        request.setBucketName(bucketName);
        request.setLocation(location);
        // 创建桶
        try{
            // 创建桶成功
            ObsBucket bucket = obsClient.createBucket(request);
            System.out.println(bucket.getRequestId());
            return true;
        }
        catch (ObsException e)
        {
            // 创建桶失败
            System.out.println("HTTP Code: " + e.getResponseCode());
            System.out.println("Error Code:" + e.getErrorCode());
            System.out.println("Error Message: " + e.getErrorMessage());

            System.out.println("Request ID:" + e.getErrorRequestId());
            System.out.println("Host ID:" + e.getErrorHostId());
            return false;
        }
    }

    public static boolean exitBucket(String bucketName) {
        boolean exists = obsClient.headBucket(bucketName);
        return  exists;
    }

    /**
     * 创建文件夹
     * @param bucketName
     * @return
     */

    public static boolean CreateFolder(String bucketName,String folderPath) {
        obsClient.putObject(bucketName, folderPath, new ByteArrayInputStream(new byte[0]));
        return true;
    }

    /*
        分段上传任务
        1.初始化分段上传任务获取全局id
        2.上传文件每一个段
        3.将文件合并
        并发上传
     */

    public static String InitiateMultipartUploadRequestTask(String bucketName,String objectname) {
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, objectname);
//        设置元数据
//        ObjectMetadata metadata = new ObjectMetadata();
//        metadata.addUserMetadata("property", "property-value");
//        metadata.setContentType("text/plain");
//        request.setMetadata(metadata);
        InitiateMultipartUploadResult result = obsClient.initiateMultipartUpload(request);
        String uploadId = result.getUploadId();
        System.out.println("\t" + uploadId);
        return uploadId;
    }

    public static boolean UploadPartFile(String bucketName,String objectname,String uploadId,Long TotalfileSize,String filePath) {
        // OBS会将服务端收到段数据的ETag值（段数据的MD5值）返回给用户。
        // 这边只需要获取文件大小，计算出有多少个分片，需要上传多少次，计算出每次的偏移量就好了
        List<PartEtag> partEtags = new ArrayList<PartEtag>();
        long chunks = TotalfileSize % FilePartSize == 0 ? TotalfileSize / FilePartSize : TotalfileSize / FilePartSize + 1;
        if (chunks >= 1000) {
            log.info("上传文件超过上限");
            return false;
        }
        int currentChunk = 0;
        while(currentChunk < chunks) {
            UploadPartRequest request = new UploadPartRequest(bucketName, objectname);
            // 设置Upload ID
            request.setUploadId(uploadId);
            // 分段号
            request.setPartNumber(currentChunk+1);
            // 偏移量
            long offset = currentChunk * FilePartSize;
            request.setOffset(offset);
            // 分段大小
            long partSize = offset + FilePartSize > TotalfileSize ? (TotalfileSize - offset) : FilePartSize;
            request.setPartSize(partSize);
            currentChunk = currentChunk + 1;
            request.setFile(new File(filePath));
            UploadPartResult result = obsClient.uploadPart(request);
            if(result.getStatusCode() < 300) {
                partEtags.add(new PartEtag(result.getEtag(), result.getPartNumber()));
            }else return false;
        }
        if (partEtags.size() == chunks) {
            return CompleteMultipartUpload(bucketName, objectname, uploadId, partEtags);
        }
        return true;
    }

    public static boolean CompleteMultipartUpload(String bucketName,String objectname,String uploadId,List<PartEtag> partEtags) {
        // 调用合并段
        CompleteMultipartUploadRequest request = new CompleteMultipartUploadRequest(bucketName, objectname, uploadId, partEtags);
        CompleteMultipartUploadResult result = obsClient.completeMultipartUpload(request);
        if(result.getStatusCode() < 300) {
            log.info("文件合并完成");
            return true;
        }else {
            log.info("文件合并失败");
            return false;
        }
    }


    public static boolean AsynFileUpload(String bucketName,String objectname,String uploadId,Long TotalfileSize,String filePath) {

        // 初始化线程池
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        final List<PartEtag> partEtags = Collections.synchronizedList(new ArrayList<PartEtag>());
        long partCount = TotalfileSize % FilePartSize == 0 ? TotalfileSize / FilePartSize : TotalfileSize / FilePartSize + 1;
        // 执行并发上传段
        for (int i = 0; i < partCount; i++)
        {
            // 分段在文件中的起始位置
            final long offset = i * FilePartSize;
            // 分段大小
            final long currPartSize = (i + 1 == partCount) ? FilePartSize - offset : FilePartSize;
            // 分段号
            final int partNumber = i + 1;
            executorService.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    UploadPartRequest uploadPartRequest = new UploadPartRequest();
                    uploadPartRequest.setBucketName(bucketName);
                    uploadPartRequest.setObjectKey(objectname);
                    uploadPartRequest.setUploadId(uploadId);
                    uploadPartRequest.setFile(new File(filePath));
                    uploadPartRequest.setPartSize(currPartSize);
                    uploadPartRequest.setOffset(offset);
                    uploadPartRequest.setPartNumber(partNumber);

                    UploadPartResult uploadPartResult;
                    try
                    {
                        uploadPartResult = obsClient.uploadPart(uploadPartRequest);
                        System.out.println("Part#" + partNumber + " done\n");
                        partEtags.add(new PartEtag(uploadPartResult.getEtag(), uploadPartResult.getPartNumber()));
                    }
                    catch (ObsException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }

        // 等待上传完成
        executorService.shutdown();
        while (!executorService.isTerminated())
        {
            try
            {
                executorService.awaitTermination(5, TimeUnit.SECONDS);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        // 合并段
        return CompleteMultipartUpload(bucketName, objectname, uploadId, partEtags);
    }

    // 取消分段上传任务
    public static boolean AbortMultipartUpload(String bucketName,String objectname,String uploadId) {
        AbortMultipartUploadRequest request = new AbortMultipartUploadRequest(bucketName, objectname, uploadId);
        if(obsClient.abortMultipartUpload(request).getStatusCode() < 300) {
            log.info("取消成功");
            return true;
        }else {
            log.info("取消失败");
            return false;
        }
    }

    // 列举已上传段列表
    public static List<Multipart> getMultipart(String bucketName,String objectname,String uploadId) {
        //列举已上传的段，其中uploadId来自于initiateMultipartUpload
        ListPartsRequest request = new ListPartsRequest(bucketName, objectname);
        request.setUploadId(uploadId);
        ListPartsResult result = obsClient.listParts(request);

        for(Multipart part : result.getMultipartList()) {
            // 分段号，上传时候指定
            System.out.println("\t" + part.getPartNumber());
            // 段数据大小
            System.out.println("\t" + part.getSize());
            // 分段的ETag值
            System.out.println("\t" + part.getEtag());
            // 段的最后上传时间
            System.out.println("\t" + part.getLastModified());
        }
        return result.getMultipartList();
    }






    public static boolean uploadFile(String bucketName,String objectname,String localFilePath) {
        try {
            log.info("上传文件");
            PutObjectResult putObjectResult = obsClient.putObject(bucketName,objectname,new File(localFilePath));
            if(putObjectResult.getStatusCode() < 300) {
                log.info("上传文件成功");
                return true;
            }else return false;
        }catch (ObsException e){
            System.out.println("Error Code:" + e.getErrorCode());
            return false;
        }
    }

    public static boolean downloadFile(String bucketName,String objectname,String localFilePath) {
        try {
            ObsObject obsObject = obsClient.getObject(bucketName, objectname);
            // 读取对象内容
            System.out.println("Object content:");
            InputStream input = obsObject.getObjectContent();
            byte[] b = new byte[1024];
            FileOutputStream bos = new FileOutputStream(new File(localFilePath));
            int len;
            while ((len=input.read(b)) != -1){
                bos.write(b, 0, len);
            }
            bos.close();
            input.close();
            return true;
        }catch (ObsException e){
            System.out.println("Error Code:" + e.getErrorCode());
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
