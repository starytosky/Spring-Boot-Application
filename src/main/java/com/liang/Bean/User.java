package com.liang.Bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

/**
 * @author by liang
 * @date : 2022-10-19 19:42
 **/
//@AllArgsConstructor // 全参构造器 --》利用当前的所有参数做一个构造器,
// 如果我们不想要其中的某些参数作为构造器，那么就把这个注解注释掉自己去定义就好
@NoArgsConstructor // 无参构造器
@ToString // toString
@Data // get、set方法
// 表示这是个组件，在容器中被创建和装备，可以注入到别的类中
@Component
public class User {
    private String name;
    private Integer age;
    private Pet pet;

    public User(String name,Integer age) {
        this.name = name;
        this.age = age;
    }
}


