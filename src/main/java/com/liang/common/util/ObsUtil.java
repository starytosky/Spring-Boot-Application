package com.liang.common.util;

import com.obs.services.Log4j2Configurator;
import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.*;
import lombok.extern.slf4j.Slf4j;


import java.io.*;

@Slf4j
public class ObsUtil {

    static String endPoint = "https://obs.cn-east-3.myhuaweicloud.com";
    static String ak = "8KMQOSYPO7U4ONN9OHFV";
    static String sk = "KEIfU2Y97wD0S3mXpEAbuebzbf1QsFsQs1bG3PJa";
    // 创建ObsClient实例
    static ObsClient obsClient = new ObsClient(ak, sk, endPoint);

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
