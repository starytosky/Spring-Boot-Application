package com.liang.Controller;

import com.liang.Bean.LiveVideoMask;
import com.liang.Bean.LocalvideoMask;
import com.liang.common.util.Result;
import com.liang.service.DataMaskService;
import com.liang.service.IExecService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;

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
    private DataMaskService dataMaskService;

    @PostMapping("localvideomask")
    public Result localVideo(@RequestBody LocalvideoMask videoMask) {
        // 检查参数是否正确
        boolean checkParameters = dataMaskService.checkParameters(videoMask.getModelList(), videoMask.getUseMethod());
        if(checkParameters) {
            // 判断文件是否存在
            Boolean isFile = dataMaskService.isFile(videoMask.getVideoPath());
            if(!isFile) {
                return Result.build(500,"离线文件不存在");
            }else {
                if(dataMaskService.localVideoMask(videoMask)) {
                    return Result.ok("正在脱敏..");
                }else {
                    return Result.build(500,"数据脱敏失败");
                }
            }
        }else {
            return Result.build(500,"参数不正确");
        }
    }

    @PostMapping("livevideomask")
    public Result liveVideo(@RequestBody LiveVideoMask liveVideoMask) throws IOException {
        if(dataMaskService.isRtmpStream(liveVideoMask.getStreamUrl())) {
            boolean isLive = dataMaskService.liveVideoMask(liveVideoMask);
            if (isLive) {
                return Result.ok("正在脱敏..");
            }else return Result.build(500,"脱敏失败");
        }else {
            return Result.build(500,"不正确的stream_url");
        }

    }
}

