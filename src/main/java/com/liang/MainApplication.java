package com.liang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author by liang
 * @date : 2022-10-19 14:06
 **/
// 表示这是个springboot应用的入口
// scanBasePackages 表示配置扫描文件的范围，在该目录下的文件会被自动扫描
@SpringBootApplication(scanBasePackages = "com.liang")
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class,args);
    }
}


