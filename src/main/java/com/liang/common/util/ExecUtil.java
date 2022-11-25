package com.liang.common.util;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
/**
 * @author liang
 * @Package com.liang.common.util
 * @date 2022/10/25 15:56
 */

@Slf4j
public class ExecUtil {

    /**
     * Execute system command
     *
     * @param cmd
     * @param timeOut time out
     * @return the result
     * @throws IOException
     * @throws InterruptedException
     */
    public static String exec(String[] cmd, int timeOut) throws IOException, InterruptedException {
        log.info("延迟三十秒");
        Process p = Runtime.getRuntime().exec(cmd);
        boolean res = p.waitFor(timeOut, TimeUnit.HOURS);
        if(!res) {
            return "Time out";
        }
        InputStream inputStream = p.getInputStream();
        byte[] data = new byte[1024];
        String result = "";
        while(inputStream.read(data) != -1) {
            result += new String(data,StandardCharsets.UTF_8);
        }
        log.info("python脚本返回结果" + result);
        String errResult = "";
        InputStream errorStream = p.getErrorStream();
        while(errorStream.read(data) != -1) {
            errResult += (new String(data, StandardCharsets.UTF_8));
        }
        log.info("python脚本返回的错误信息" + errResult);
//        if(!errResult.equals("")) {
//            return "false";
//        }
        return result;
    }
//    public static void main(String [] args) {
//        // test 1: ping
//        try {
//            String res = ExecUtil.exec("ping www.baidu.com", 5);
//            System.out.println(res);
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//        // test 2: ping
//        try {
//            String res = ExecUtil.exec(new String [] {"ping","www.baidu.com"}, 5);
//            System.out.println(res);
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//        // test 3: ipconfig
//        try {
//            String res = ExecUtil.exec("ipconfig", 5);
//            System.out.println(res);
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//        // test 4: ipconfig /all
//        try {
//            String res = ExecUtil.exec(new String [] {"ipconfig","/all"}, 5);
//            System.out.println(res);
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//        // test 5: open exe file
//        try {
//            String res = ExecUtil.exec("D:/software/data/Notepad++/notepad++", 5);
//            System.out.println(res);
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
}

