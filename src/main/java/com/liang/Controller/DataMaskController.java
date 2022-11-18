package com.liang.Controller;

import com.liang.Bean.LiveVideoMask;
import com.liang.Bean.videoMask;
import com.liang.common.util.Result;
import com.liang.service.IExecService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("LivevideoStreaming")
    public Result liveVideo(@RequestBody LiveVideoMask liveVideoMask) {
        String stream_url = "rtmp://media3.scctv.net/live/scctv_800";// 流地址
        Long times_sec = Long.valueOf(30);// 停止录制时长 0为不限制时长
        String out_file_path = "D:\\uploadFiles\\outvideo\\";//输出路径
        String useMethod = "cpu";
        // 保存的文件名
        String filename = "test.mp4";
        if(execService.isRtmpStream(liveVideoMask.getStream_url())) {
            boolean isLive = execService.liveVideoMask(liveVideoMask.getStream_url(), liveVideoMask.getTimes_sec(), liveVideoMask.getOut_file_path(), liveVideoMask.getFilename(), liveVideoMask.getModelList(), liveVideoMask.getUseMethod());
            if (isLive) {
                return Result.ok("脱敏成功");
            }else return Result.build(500,"脱敏失败");
        }else {
            return Result.build(500,"不正确的stream_url");
        }

    }
}

