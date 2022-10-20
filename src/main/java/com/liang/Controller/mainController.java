package com.liang.Controller;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author by liang
 * @date : 2022-10-19 14:08
 **/

// @ResponseBody 表示返回的是值而不是跳转到某个页面
//@ResponseBody
//@Controller
// @RestController 包含了上面两个注解的功能
@RestController
@Slf4j // 注入日志类，可以使用 Log.info("打印内容"); 来打印内容
public class mainController {

    @RequestMapping("/hello")
    public String hello() {
        log.info("请求数据");
        return "hello Spring";
    }
}

