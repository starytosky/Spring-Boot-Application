package com.liang.common.util;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
class OutStream extends Thread{
    private static Logger LOG = LoggerFactory.getLogger(OutStream.class);
    InputStream is;
    String type;

    String taskId;

    OutStream(InputStream is, String type,String taskId) {
        this.is = is;
        this.type = type;
        this.taskId = taskId;
    }

    public void run() {
        try {
            // 将脱敏日志写入文件
            MDC.put("taskId", taskId);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (type.equals("Error")) {
//                   LOG.info(line, taskId, Thread.currentThread());
                } else {
                    LOG.info(line, taskId, Thread.currentThread());
                }
            }
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }
}
