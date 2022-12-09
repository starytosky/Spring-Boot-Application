package com.liang;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author by liang
 * @date : 2022-10-19 14:06
 **/
// 表示这是个springboot应用的入口
// scanBasePackages 表示配置扫描文件的范围，在该目录下的文件会被自动扫描
@SpringBootApplication(scanBasePackages = "com.liang")
@EnableAsync
//@MapperScan("com.liang.Mapper")
public class MainApplication {
    public static void main(String[] args) {
//        配置应用上下文
        SpringApplication.run(MainApplication.class, args);
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> configurer(@Value("${spring.application.name}") String applicationName) {
        return (registry) -> registry.config().commonTags("application",applicationName);
    }
}


