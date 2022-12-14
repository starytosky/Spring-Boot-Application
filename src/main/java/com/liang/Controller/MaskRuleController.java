package com.liang.Controller;

import com.liang.Rep.CheckRule;
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
@RequestMapping("/maskRule/")
@Slf4j
public class MaskRuleController {

    @Autowired
    private MaskRuleService maskRuleService;

    @PostMapping("addRule")
    public Result liveVideo(@RequestBody Maskrule maskrule) throws IOException {
        Date timer = new Date();
        maskrule.setTime(timer);
        if(maskRuleService.addMaskRule(maskrule) == 1) {
            return Result.ok("添加成功");
        }else {
            return Result.build(500,"添加失败");
        }
    }

    @GetMapping("checkRule")
    public Result checkTask(CheckRule checkRule) {
        checkRule.setTotalRecord(checkRule.getRecordNumber()*checkRule.getPageNumber());
        int recordCount = maskRuleService.getRecordCountByUserId(checkRule.getUserId());
        return Result.build(maskRuleService.getMaskRule(checkRule),recordCount, ResultCodeEnum.SUCCESS);
    }

    @GetMapping("getMaskRuleDetail")
    public Result getMaskRuleDetailByRuleId(@RequestParam Integer ruleId) {
        return Result.ok(maskRuleService.getMaskRuleDetailByRuleId(ruleId));
    }

    @GetMapping("deleteRule")
    public Result deleteTask(@RequestParam Integer userId,@RequestParam  Integer ruleId) {
        if(maskRuleService.deleteMaskRule(userId,ruleId) == 1) {
            return Result.ok("删除成功");
        }else return Result.build(500,"删除失败");
    }

    @GetMapping("getCount")
    public Result getCount(@RequestParam Integer userId) {
        return Result.ok(maskRuleService.getRecordCountByUserId(userId));
    }

    // 读取文件数据
    @GetMapping("getRuleContent")
    public Result getRuleContent(@RequestParam String rulePath) {
        return Result.ok(maskRuleService.getRuleContent(rulePath));
    }


}
