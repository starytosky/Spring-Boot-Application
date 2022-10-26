package com.liang.Controller;

import com.liang.Bean.Pet;
import com.liang.common.util.Result;
import com.liang.service.IExecService;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author by liang
 * @date : 2022-10-19 14:08
 **/

// @ResponseBody 表示返回的是值而不是跳转到某个页面
//@ResponseBody
//@Controller
// @RestController 包含了上面两个注解的功能
@RestController
@RequestMapping("/dataMask/")
@Slf4j // 注入日志类，可以使用 Log.info("打印内容"); 来打印内容
public class DataMaskController {

    @Autowired
    private IExecService execService;

    @GetMapping("video")
    public Result localVideo(String md5Id) {
        log.info("离线视频脱敏");
        // 判断文件是否存在
        String isFile = execService.isFile(md5Id);
        if(isFile.equals("")) {
            return Result.build(500,"离线文件不存在");
        }else {
            if(execService.localVideoMask(md5Id)) {
                return Result.ok("脱敏成功");
            }else {
                return Result.build(500,"数据脱敏失败");
            }
        }

    }

    @GetMapping("help")
    public String getHelp() {
        return "hello";
    }
}

