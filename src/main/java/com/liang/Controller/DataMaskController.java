package com.liang.Controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.liang.Rep.*;
import com.liang.Mapper.MaskTaskMapper;
import com.liang.Mapper.UserMapper;
import com.liang.common.util.Result;
import com.liang.service.DataMaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

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


    @Autowired
    private UserMapper userMapper;




    @PostMapping("createMaskTask")
    public Result createMaskTask(@RequestBody MaskTask maskTask) {
        Date timer = new Date();
        maskTask.setTime(timer);
        if( dataMaskService.createMaskTask(maskTask)!= 0) {
            return Result.ok(maskTask.getTaskId());
        }else {
            return Result.build(500,"创建失败");
        }
    }

    @PostMapping("updateMaskTask")
    public Result updateMaskTask(@RequestBody MaskTask maskTask) {
        Date timer = new Date();
        maskTask.setTime(timer);
        if(dataMaskService.updateMaskTask(maskTask) != 0) {
            return Result.ok("更新成功");
        }else {
            return Result.build(500,"更新失败");
        }
    }




    @PostMapping("localvideomask")
    public Result localVideo(@RequestBody MaskTask maskTask) throws IOException {
        Date timer = new Date();
        maskTask.setTime(timer);
        if (dataMaskService.updateMaskTask(maskTask) != 0) {
            // 根据 规则id去拿规则数据，判断是字符串数据还是文件数据,并进行相应的数据处理
            // 规则描述就是规则本身
            String ruleDesc = dataMaskService.getMaskRuleByRuleId(maskTask.getRuleId());
            if (ruleDesc.equals("")) {
                log.info("任务" + maskTask.getTaskId() + "脱敏规则不正确");
                return Result.build(500, "脱敏规则不正确");
            } else {
                // 检测原始文件是否存在
                Boolean isFile = dataMaskService.isFile(maskTask.getDataPath());
                if (!isFile) {
                    return Result.build(500, "离线文件不存在");
                } else {
                    if (dataMaskService.localVideoMask(maskTask,ruleDesc)) {
                        return Result.ok("正在脱敏..");
                    } else {
                        return Result.build(500, "数据脱敏失败");
                    }
                }

            }
        } else return Result.build(500, "保存失败");
    }


    @PostMapping("livevideomask")
    public Result liveVideo(@RequestBody MaskTask maskTask) throws IOException {
        String ruleDesc = dataMaskService.getMaskRuleByRuleId(maskTask.getRuleId());
        if (ruleDesc.equals("")) {
            log.info("任务" + maskTask.getTaskId() + "脱敏规则不正确");
            return Result.build(500, "脱敏规则不正确");
        } else {
            if (dataMaskService.isRtmpStream(maskTask.getStreamUrl())) {
                boolean isLive = dataMaskService.liveVideoMask(maskTask,ruleDesc);
                if (isLive) {
                    return Result.ok("正在脱敏..");
                } else return Result.build(500, "脱敏失败");
            } else {
                return Result.build(500, "不正确的stream_url");
            }
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

    @GetMapping("getTask")
    public Result getTask(@RequestParam  Integer taskId) {
        return Result.ok(dataMaskService.getMaskTaskById(taskId));
    }



    @GetMapping("deleteTask")
    public Result deleteTask(@RequestParam Integer userId,@RequestParam  Integer taskId,@RequestParam Integer typeId) {
        if(dataMaskService.deleteTask(userId,taskId,typeId) == 1) {
            return Result.ok("删除成功");
        }else return Result.build(500,"删除失败");
    }

    @GetMapping("getuser")
    public Result help() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userMapper.selectList(null);
        return Result.ok(userList);
    }

    @GetMapping("insertUser")
    public Result insertUser(User user) {
        if(userMapper.insert(user) == 1) {
            return Result.ok("插入成功");
        }else return Result.build(500,"插入失败");
    }

    @PostMapping("updateUser")
    public Result updateUser(@RequestBody User user) {
        log.info(String.valueOf(user));
        int x = userMapper.updateById(user);
        if( x== 1) {
            return Result.ok("更新成功");
        }else return Result.build(500,"更新失败");
    }


}

