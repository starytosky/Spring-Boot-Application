package com.liang.Controller;

import com.liang.Bean.Pet;
import com.liang.Bean.videoMask;
import com.liang.common.util.Result;
import com.liang.service.IExecService;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;

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

    @PostMapping("video")
    public Result localVideo(@RequestBody videoMask videoMask) {
        log.info("离线视频脱敏");
        // 检查参数是否正确
        boolean checkParameters = execService.checkParameters(videoMask.getModelList(), videoMask.getUseMethod());
        if(checkParameters) {
            // 判断文件是否存在
            String isFile = execService.isFile(videoMask.getMd5Id());
            if(isFile.equals("")) {
                return Result.build(500,"离线文件不存在");
            }else {
                if(execService.localVideoMask(isFile, videoMask.getModelList(), videoMask.getUseMethod())) {
                    return Result.ok("脱敏成功");
                }else {
                    return Result.build(500,"数据脱敏失败");
                }
            }
        }else {
            return Result.build(500,"参数不正确");
        }


    }

    @GetMapping("help")
    public String getHelp() {
        String fileDirPath = "D:\\uploadFiles\\0198c25790ad81c091d8d0e5c850a0ed";
        File file = new File(fileDirPath);
        File[] array = file.listFiles();
        if(file.isDirectory() && array.length == 1) {
            // 获取这个文件名字
            if(array[0].isFile()) {
                log.info("源视频文件地址" + fileDirPath + File.separator + array[0].getName());
                return fileDirPath + File.separator + array[0].getName();
            }else {
                return "";
            }
        }else {
            return "";
        }
    }
}

