package com.liang.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
class OutStream extends Thread{
    InputStream is;
    String type;

    public OutStream(InputStream is, String type) {
        this.is = is;
        this.type = type;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (type.equals("Error")) {
                   log.error(line);
                } else {
                    log.info(line);
                }
            }
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }
}