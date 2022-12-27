package com.liang.Controller;

import com.liang.Rep.*;
import com.liang.Res.LocalData;
import com.liang.Res.MaskDataInfo;
import com.liang.common.util.Result;
import com.liang.common.util.ResultCodeEnum;
import com.liang.service.MaskTaskService;
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
public class MaskTaskController {

    @Autowired
    private MaskTaskService maskTaskService;


    @PostMapping("createMaskTask")
    public Result createMaskTask(@RequestBody MaskTask maskTask) {
        Date timer = new Date();
        maskTask.setTime(timer);
        if( maskTaskService.createMaskTask(maskTask)!= 0) {
            return Result.ok(maskTask.getTaskId());
        }else {
            return Result.build(500,"创建失败");
        }
    }

    @PostMapping("updateMaskTask")
    public Result updateMaskTask(@RequestBody MaskTask maskTask) {
        Date timer = new Date();
        maskTask.setTime(timer);
        if(maskTaskService.updateMaskTask(maskTask) != 0) {
            return Result.ok("更新成功");
        }else {
            return Result.build(500,"更新失败");
        }
    }

    @GetMapping("getMaskTaskList")
    public Result getTaskRecord(CheckMaskTask checkMaskTask) {
        checkMaskTask.setTotalRecord(checkMaskTask.getRecordNumber()*(checkMaskTask.getPageNumber() - 1));
        int recordCount = maskTaskService.getTaskRecordCount(checkMaskTask);
        return Result.build(maskTaskService.getTaskRecord(checkMaskTask),recordCount, ResultCodeEnum.SUCCESS);
    }




    @PostMapping("localvideomask")
    public Result localVideo(@RequestBody MaskTask maskTask) throws IOException {
        Date timer = new Date();
        maskTask.setTime(timer);
        // 查看
        if (maskTaskService.updateMaskTask(maskTask) != 0) {
            // 根据 规则id去拿规则数据，判断是字符串数据还是文件数据,并进行相应的数据处理
            // 规则描述就是规则本身
            String ruleDesc = maskTaskService.getMaskRuleByRuleId(maskTask.getRuleId());
            if (ruleDesc.equals("")) {
                log.info("任务" + maskTask.getTaskId() + "脱敏规则不正确");
                return Result.build(500, "脱敏规则不正确");
            } else {
                // 检测原始文件是否存在
                Boolean isFile = maskTaskService.isFile(maskTask.getDataPath());
                if (!isFile) {
                    return Result.build(500, "离线文件不存在");
                } else {
                    if (maskTaskService.localVideoMask(maskTask,ruleDesc)) {
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
        String ruleDesc = maskTaskService.getMaskRuleByRuleId(maskTask.getRuleId());
        if (ruleDesc.equals("")) {
            log.info("任务" + maskTask.getTaskId() + "脱敏规则不正确");
            return Result.build(500, "脱敏规则不正确");
        } else {
            if (maskTaskService.isRtmpStream(maskTask.getStreamUrl())) {
                boolean isLive = maskTaskService.liveVideoMask(maskTask,ruleDesc);
                if (isLive) {
                    return Result.ok("正在脱敏..");
                } else return Result.build(500, "脱敏失败");
            } else {
                return Result.build(500, "请输入正确的脱敏地址！");
            }
        }
    }

    // 获取执行记录
    @GetMapping("getExecRecord")
    public Result getExecRecord(@RequestParam String userId,@RequestParam  Integer taskId,@RequestParam  Integer isType) {
        if(isType == 0) {
            return Result.ok(maskTaskService.getLocalExecRecordList(userId,taskId));
        }else return Result.ok(maskTaskService.getLiveExecRecordList(userId,taskId));
    }

    // 执行记录的详细信息
    @GetMapping("getExecRecordInfo")
    public Result getExecRecordInfo(@RequestParam String userId,@RequestParam  Integer execId,@RequestParam  Integer isType) {
        return Result.ok(maskTaskService.getExecRecordInfo(userId,execId,isType));
    }


    // 查询脱敏数据集 getLocalDataList
    @GetMapping("getlocalData")
    public Result getlocalData(CheckLocalData checkLocalData) {
        checkLocalData.setTotalRecord(checkLocalData.getRecordNumber()* (checkLocalData.getPageNumber()-1));
        List<LocalData> localDataList = maskTaskService.getLocalDataList(checkLocalData);
        int localDataCount = maskTaskService.getLocalDataCount(checkLocalData);
        if(localDataList != null) {
            return Result.build(localDataList,localDataCount, ResultCodeEnum.SUCCESS);
        }else return Result.build(500,"查询失败");
    }

}

