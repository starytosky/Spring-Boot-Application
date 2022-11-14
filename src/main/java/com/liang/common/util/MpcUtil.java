package com.liang.common.util;

// 用户身份认证
import com.alibaba.fastjson.JSONObject;
import com.huaweicloud.sdk.core.auth.BasicCredentials;
// 请求异常类
import com.huaweicloud.sdk.core.exception.ClientRequestException;
import com.huaweicloud.sdk.core.exception.ConnectionException;
import com.huaweicloud.sdk.core.exception.RequestTimeoutException;
import com.huaweicloud.sdk.core.exception.ServerResponseException;
// Http 配置
import com.huaweicloud.sdk.core.http.HttpConfig;
// 导入mpc的客户端
import com.huaweicloud.sdk.mpc.v1.MpcClient;
// 导入待请求接口的 request 和 response 类
import com.huaweicloud.sdk.mpc.v1.model.*;
// 日志打印
import com.obs.services.internal.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;

@Slf4j
public class MpcUtil {

    public static MpcClient initMpcClient() {
        String endPoint = "https://mpc.cn-east-3.myhuaweicloud.com";
        String ak = "IUZOAPZIE0E3XHAVGDH8";
        String sk = "p2hQTfDLr73RnHTyAAGAnf50tD3P7Z5ad1GRn97F";
        String projectId = "cn-east-3";

        // 使用默认配置
        HttpConfig config = HttpConfig.getDefaultHttpConfig();

        String tokens = getToken("https://iam.cn-east-3.myhuaweicloud.com/v3/auth/tokens","{\n" +
                "    \"auth\": {\n" +
                "        \"identity\": {\n" +
                "            \"methods\": [\n" +
                "                \"password\"\n" +
                "            ],\n" +
                "            \"password\": {\n" +
                "                \"user\": {\n" +
                "                    \"name\": \"liang\",\n" +
                "                    \"password\": \"!!xdGS0$LoOers8qr%%JI\",\n" +
                "                    \"domain\": {\n" +
                "                        \"name\": \"hid_grf0e3bj7h1j-z2\"\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        },\n" +
                "        \"scope\": {\n" +
                "            \"project\": {\n" +
                "                \"name\": \"cn-east-3\"\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}");
        String token = tokens.substring(1,tokens.length()-1);

        BasicCredentials credentials = new BasicCredentials().withAk(ak).withSk(sk).withSecurityToken(token).withProjectId(projectId);

        //初始化MPC的客户端
        MpcClient client =  MpcClient.newBuilder().withHttpConfig(config).withCredential(credentials).withEndpoint(endPoint).build();
        // 初始化请求，以调用查询转码模板接口为例
        ListTranscodingTaskResponse response = client.listTranscodingTask(new ListTranscodingTaskRequest().withTaskId(Collections.singletonList(1900293L)));
        log.info(response.toString());

        return client;
    }

    public static String getToken(String url,String json) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return resultString;
    }

    public static boolean createTranscodingTask(String inBucketName,String outBucketName,String location,String inObj,String outObj) {
        //设置转码输入视频地址
        ObsObjInfo input = new ObsObjInfo().withBucket(inBucketName).withLocation(location).withObject(inObj);
        //设置转码输出视频路径
        ObsObjInfo output = new ObsObjInfo().withBucket(outBucketName).withLocation(location).withObject(outObj);
        //创建转码请求
        CreateTranscodingTaskRequest request
                = new CreateTranscodingTaskRequest().withBody(new CreateTranscodingReq()
                        .withInput(input)
                        .withOutput(output)
                        //设置转码模板，预置模板Id可以在MPC console页面“全局设置” - “预置模板”上查看 自适应 7000784
                        .withTransTemplateId(Collections.singletonList(7000784))
                        //设置输出名称，名称个数需要与模板个数一一对应
                        .withOutputFilenames(Collections.singletonList("output.mp4"))
                //设置截图参数
                //.withThumbnail(new Thumbnail())
                //设置加密参数
                //.withEncryption(new Encryption())
        );
        try {
            CreateTranscodingTaskResponse response = initMpcClient().createTranscodingTask(request);
            System.out.println("CreateTranscodingTaskResponse=" + response);
            return true;
        } catch (ClientRequestException | ConnectionException | RequestTimeoutException | ServiceException e) {
            System.out.println(e);
            return false;
        }
    }



}
