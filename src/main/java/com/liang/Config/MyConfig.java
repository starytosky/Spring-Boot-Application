package com.liang.Config;

import com.liang.Bean.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author by liang
 * @date : 2022-10-19 21:16
 **/
@Configuration()
public class MyConfig {

    @Bean
    public User user1() {
        return new User("liang",12);
    }
}


