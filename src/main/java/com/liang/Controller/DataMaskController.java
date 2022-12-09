package com.liang.Controller;

import com.liang.Bean.CheckTask;
import com.liang.Bean.LiveVideoMask;
import com.liang.Bean.LocalMask;
import com.liang.Bean.MaskTask;
import com.liang.common.util.Result;
import com.liang.service.DataMaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
@Slf4j
public class DataMaskController {

    @Autowired
    private DataMaskService dataMaskService;


    @PostMapping("createMaskTask")
    public Result createMaskTask(@RequestBody MaskTask maskTask) {
        if(dataMaskService.createMaskTask(maskTask) != 0) {
            return Result.ok("创建成功");
        }else {
            return Result.build(500,"创建失败");
        }
    }

    @PostMapping("updateMaskTask")
    public Result updateMaskTask(@RequestBody MaskTask maskTask) {
        if(dataMaskService.updateMaskTask(maskTask) != 0) {
            return Result.ok("更新成功");
        }else {
            return Result.build(500,"更新失败");
        }
    }


    @PostMapping("localvideomask")
    public Result localVideo(@RequestBody LocalMask videoMask) {
        // 检查参数是否正确
        boolean checkParameters = dataMaskService.checkParameters(videoMask.getModelList(), videoMask.getUseMethod());
        if(checkParameters) {
            // 判断文件是否存在
            Boolean isFile = dataMaskService.isFile(videoMask.getOriginPath());
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

    @GetMapping("checkTask")
    public Result checkTask(CheckTask checkTask) {
        checkTask.setTotalRecord(checkTask.getRecordNumber()* checkTask.getPageNumber());
        if(checkTask.getTypeId() == 0) {
            return Result.ok(dataMaskService.getLocalTaskPosition(checkTask));
        }else {
            return Result.ok(dataMaskService.getLiveTaskPosition(checkTask));
        }
    }

    @GetMapping("deleteTask")
    public Result deleteTask(@RequestParam Integer userId,@RequestParam  Integer taskId,@RequestParam Integer typeId) {
        if(dataMaskService.deleteTask(userId,taskId,typeId) == 1) {
            return Result.ok("删除成功");
        }else return Result.build(500,"删除失败");
    }

//    @GetMapping("help")
//    public Result help() {
//        System.out.println(("----- selectAll method test ------"));
//        List<User> userList = userMapper.selectList(null);
//        Assert.assertEquals(5, userList.size());
//        userList.forEach(System.out::println);
//        return Result.ok("正确");
//    }
}

