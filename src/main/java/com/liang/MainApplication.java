package com.liang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author by liang
 * @date : 2022-10-19 14:06
 **/
// 表示这是个springboot应用的入口
// scanBasePackages 表示配置扫描文件的范围，在该目录下的文件会被自动扫描
@SpringBootApplication(scanBasePackages = "com.liang")
public class MainApplication {
    public static void main(String[] args) {
//        配置应用上下文
        ConfigurableApplicationContext run = SpringApplication.run(MainApplication.class, args);

        boolean tom = run.containsBean("tom");
        System.out.println("容器中tom组件:" + tom);

        boolean user1 = run.containsBean("user1");
        System.out.println("容器中 user1 组件:" + user1);

        boolean tomess = run.containsBean("tomess");
        System.out.println("容器中 tomess 组件:" + tomess);
    }
}


