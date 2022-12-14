package com.liang.Controller;

import com.liang.Rep.CheckRule;
import com.liang.Rep.MaskRuleChose;
import com.liang.Rep.Maskrule;
import com.liang.common.util.Result;
import com.liang.common.util.ResultCodeEnum;
import com.liang.service.MaskRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;

@RestController
@RequestMapping("/maskService/maskRule/")
@Slf4j
public class MaskRuleController {

    @Autowired
    private MaskRuleService maskRuleService;

    @PostMapping("addRule")
    public Result liveVideo(@RequestBody Maskrule maskrule) throws IOException {
        Date timer = new Date();
        maskrule.setTime(timer);
        int result = maskRuleService.addMaskRule(maskrule);
        if( result == 1) {
            return Result.ok("添加成功");
        }else if(result == -1){
            return Result.build(500,"限制内容错误");
        }else return Result.build(500,"添加失败");
    }

    @GetMapping("checkRule")
    public Result checkTask(CheckRule checkRule) {
        checkRule.setTotalRecord(checkRule.getRecordNumber()*(checkRule.getPageNumber() - 1));
        int recordCount = maskRuleService.selectRuleCount(checkRule);
        return Result.build(maskRuleService.getMaskRule(checkRule),recordCount, ResultCodeEnum.SUCCESS);
    }

    @GetMapping("getMaskRuleDetail")
    public Result getMaskRuleDetailByRuleId(@RequestParam String ruleId) {
        Maskrule maskrule = maskRuleService.getMaskRuleDetailByRuleId(ruleId);
        if(maskrule != null) {
            return Result.ok(maskrule);
        }else return Result.build(500,"请输入正确的ruleId");
    }

    @GetMapping("deleteRule")
    public Result deleteTask(@RequestParam String userId,@RequestParam  String ruleId) {
        if(maskRuleService.deleteMaskRule(userId,ruleId) == 1) {
            return Result.ok("删除成功");
        }else return Result.build(500,"删除失败");
    }

    @PostMapping("updateMaskRule")
    public Result updateMaskRule(@RequestBody Maskrule maskrule) {
        Date timer = new Date();
        maskrule.setTime(timer);
        if(maskRuleService.updateMaskRule(maskrule) == 1) {
            return Result.ok("更新成功");
        }else return Result.build(500,"更新失败");
    }

    @GetMapping("getCount")
    public Result getCount(@RequestParam String userId) {
        return Result.ok(maskRuleService.getRecordCountByUserId(userId));
    }

    // 读取文件数据
    @GetMapping("getRuleContent")
    public Result getRuleContent(@RequestParam String rulePath) {
        return Result.ok(maskRuleService.getRuleContent(rulePath));
    }

    @PostMapping("updateRuleChose")
    public Result updateRuleChoseById(@RequestBody MaskRuleChose maskRuleChose) {
        if(maskRuleService.updateRuleChoseById(maskRuleChose) == 1) {
            return Result.ok("更新成功");
        }else return Result.build(500,"更新失败");
    }
}
