package com.liang.common.util;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

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
//    public static String exec(String[] cmd, int timeOut) throws IOException, InterruptedException {
//        Process p = Runtime.getRuntime().exec(cmd);
//        boolean res = p.waitFor(timeOut, TimeUnit.HOURS);
//        if(!res) {
//            return "Time out";
//        }
//        InputStream inputStream = p.getInputStream();
//        byte[] data = new byte[1024];
//        String result = "";
//        while(inputStream.read(data) != -1) {
//            result += new String(data,StandardCharsets.UTF_8);
//        }
//        log.info("python脚本返回结果" + result);
//        String errResult = "";
//        InputStream errorStream = p.getErrorStream();
//        while(errorStream.read(data) != -1) {
//            errResult += (new String(data, StandardCharsets.UTF_8));
//        }
//        log.info("python脚本返回的错误信息" + errResult);
////        if(!errResult.equals("")) {
////            return "false";
////        }
//        return result;
//    }
    public static boolean exec(String[] cmd, int timeOut) throws IOException, InterruptedException {

        Process p = Runtime.getRuntime().exec(cmd);
        new OutStream(p.getInputStream(),"INFO").start();

        new OutStream(p.getErrorStream(),"Error").start();

        // exitValue 为0表示正常终止
        int exitValue = p.waitFor();
        log.info(String.valueOf(exitValue));
        if (exitValue != 0) {
            return false;
        }
        return true;
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

