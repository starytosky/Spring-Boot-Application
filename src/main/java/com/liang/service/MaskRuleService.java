package com.liang.service;


import com.liang.Rep.CheckRule;
import com.liang.Rep.Maskrule;

import java.util.List;

public interface MaskRuleService {

//    添加脱敏规则
    int addMaskRule(Maskrule maskrule);

    List<Maskrule> getMaskRule(CheckRule checkRule);

    int deleteMaskRule(Integer userId,Integer ruleId);

    int getRecordCountByUserId(Integer userId);

    Maskrule getMaskRuleById(Integer ruleId);

    String getRuleContent(String rulePath);

    Maskrule getMaskRuleDetailByRuleId(Integer ruleId);
}
