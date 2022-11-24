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
import com.huaweicloud.sdk.core.utils.JsonUtils;
import com.huaweicloud.sdk.mpc.v1.MpcClient;
// 导入待请求接口的 request 和 response 类
import com.huaweicloud.sdk.mpc.v1.model.*;
// 日志打印
import com.liang.Bean.TemporarKey;
import com.obs.services.internal.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class MpcUtil {

    public static MpcClient initMpcClient() {
        // liang
        String endPoint = "https://mpc.cn-east-3.myhuaweicloud.com";
        String ak = "IUZOAPZIE0E3XHAVGDH8";
        String sk = "p2hQTfDLr73RnHTyAAGAnf50tD3P7Z5ad1GRn97F";
        String projectId = "5b3858cd0aa64edc8d6c9f5db3daaa65";

        // 使用默认配置
        HttpConfig config = HttpConfig.getDefaultHttpConfig();

//        String tokens = getToken("https://iam.cn-east-3.myhuaweicloud.com/v3/auth/tokens","{\n" +
//                "    \"auth\": {\n" +
//                "        \"identity\": {\n" +
//                "            \"methods\": [\n" +
//                "                \"password\"\n" +
//                "            ],\n" +
//                "            \"password\": {\n" +
//                "                \"user\": {\n" +
//                "                    \"name\": \"liang\",\n" +
//                "                    \"password\": \"!!xdGS0$LoOers8qr%%JI\",\n" +
//                "                    \"domain\": {\n" +
//                "                        \"name\": \"hid_grf0e3bj7h1j-z2\"\n" +
//                "                    }\n" +
//                "                }\n" +
//                "            }\n" +
//                "        },\n" +
//                "        \"scope\": {\n" +
//                "            \"project\": {\n" +
//                "                \"name\": \"cn-east-3\"\n" +
//                "            }\n" +
//                "        }\n" +
//                "    }\n" +
//                "}");
//        String tempor = getTemporarykey(tokens,"https://iam.cn-east-3.myhuaweicloud.com/v3.0/OS-CREDENTIAL/securitytokens","{\n" +
//                "  \"auth\": {\n" +
//                "    \"identity\": {\n" +
//                "      \"methods\": [\n" +
//                "        \"token\"\n" +
//                "      ]\n" +
//                "    }\n" +
//                "  }\n" +
//                "}");
//
//
//        TemporarKey temporarKey = JSONObject.parseObject(tempor, TemporarKey.class);

//        BasicCredentials credentials = new BasicCredentials()
//                .withAk(temporarKey.getCredential().getAccess())
//                .withSk(temporarKey.getCredential().getSecret())
//                .withSecurityToken(temporarKey.getCredential().getSecuritytoken())
//                .withProjectId(projectId);
        BasicCredentials credentials = new BasicCredentials().withAk(ak).withSk(sk).withProjectId(projectId);

        //初始化MPC的客户端
        MpcClient client =  MpcClient.newBuilder().withHttpConfig(config).withCredential(credentials).withEndpoint(endPoint).build();

        return client;
    }

//    获取临时ak、sk凭证
    public static String getTemporarykey(String token,String url,String json) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("X-Auth-Token",token);
            httpPost.addHeader("Content-Type","application/json");
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

//    获取iam用户token凭证
    public static String getToken(String url,String json) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String token = null;
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            Header responseHeader = response.getFirstHeader("X-Subject-Token");
            token = responseHeader.getValue();
            System.out.println(token);
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
        return token;
    }

    public static Long createTranscodingTask(String inBucketName,String outBucketName,String location,String inObj,String outObj,String outFilename) {
        List<String> outFilenames = new ArrayList<>();
        outFilenames.add(outFilename);
        //设置转码输入视频地址
        ObsObjInfo input = new ObsObjInfo().withBucket(inBucketName).withLocation(location).withObject(inObj);
        //设置转码输出视频路径
        ObsObjInfo output = new ObsObjInfo().withBucket(outBucketName).withLocation(location).withObject(outObj);
        //创建转码请求
        CreateTranscodingTaskRequest request
                = new CreateTranscodingTaskRequest().withBody(new CreateTranscodingReq()
                        .withInput(input)
                        .withOutput(output)
                        //设置转码模板，预置模板Id可以在MPC console页面“全局设置” - “预置模板”上查看 自定义模板208
                        .withTransTemplateId(Collections.singletonList(208)).withOutputFilenames(outFilenames)
        );
        try {
            log.info("创建转码任务");
            CreateTranscodingTaskResponse response = initMpcClient().createTranscodingTask(request);
            System.out.println("CreateTranscodingTaskResponse=" + response);
            return response.getTaskId().longValue();
        } catch (ClientRequestException | ConnectionException | RequestTimeoutException | ServiceException e) {
            System.out.println(e);
            return -1L;
        }
    }

//    通过API调用MPC创建转码服务
    public static boolean createTranscodingTaskAPI() {

        String json = "{\n" +
                "    \"input\": {\n" +
                "        \"bucket\": \"idata-video\",\n" +
                "        \"location\": \"cn-east-3\",\n" +
                "        \"object\": \"b.avi\"\n" +
                "    },\n" +
                "    \"output\": {\n" +
                "        \"bucket\": \"idata-jia\",\n" +
                "        \"location\": \"cn-east-3\",\n" +
                "        \"object\": \"/a/\"\n" +
                "    },\n" +
                "    \"trans_template_id\": [\n" +
                "        7000784\n" +
                "    ]\n" +
                "}";

        String tokens = getToken("https://iam.cn-east-3.myhuaweicloud.com/v3/auth/tokens",
                "{\n" +
                        "    \"auth\": {\n" +
                        "        \"identity\": {\n" +
                        "            \"methods\": [\n" +
                        "                \"password\"\n" +
                        "            ],\n" +
                        "            \"password\": {\n" +
                        "                \"user\": {\n" +
                        "                    \"name\": \"liang\",\n" +
                        "                    \"password\": \"Tomylovely.\",\n" +
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
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost("https://mpc.cn-east-3.myhuaweicloud.com/v1/5b3858cd0aa64edc8d6c9f5db3daaa65/transcodings");
            httpPost.addHeader("X-Auth-Token",tokens);
            httpPost.addHeader("Content-Type","application/json");
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);

            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println(resultString);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
    }

    // 获取转码任务状态--根据任务id
    public static String getTaskStatus(Long taskId) {
        //按单个TaskId查询任务，TaskId是转码请求响应中返回的任务ID
        ListTranscodingTaskRequest req = new ListTranscodingTaskRequest().withTaskId(Collections.singletonList(taskId));
        //发送请求
        ListTranscodingTaskResponse listTranscodingTaskResponse = initMpcClient().listTranscodingTask(req);
//        System.out.println(JsonUtils.toJSON(listTranscodingTaskResponse));
        String status = String.valueOf(listTranscodingTaskResponse.getTaskArray().get(0).getStatus());
        System.out.println(status);
        return status;
    }

}
