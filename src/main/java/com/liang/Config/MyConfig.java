package com.liang.Config;

import com.liang.Bean.Pet;
import com.liang.Bean.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author by liang
 * @date : 2022-10-19 21:16
 **/
@Configuration(proxyBeanMethods = true) //告诉SpringBoot这是一个配置类 == 配置文件
@ConditionalOnMissingBean(name = "tom")
public class MyConfig {

    @Bean
    @Primary
    public User user1() {
        User zhang = new User("liang",12);
        //user组件依赖了Pet组件
        zhang.setPet(new Pet());
        return zhang;
    }

    @Bean("tomess")
    public Pet cat() {
        return new Pet("cat");
    }


}


