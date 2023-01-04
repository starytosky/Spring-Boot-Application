package com.liang.service;


import com.liang.Rep.CheckRule;
import com.liang.Rep.Maskrule;
import com.liang.Rep.MaskRuleChose;

import java.util.List;

public interface MaskRuleService {

//    添加脱敏规则
    int addMaskRule(Maskrule maskrule);

    List<Maskrule> getMaskRule(CheckRule checkRule);

    int selectRuleCount(CheckRule checkRule);

    int deleteMaskRule(String userId,String ruleId);

    int getRecordCountByUserId(String userId);

    Maskrule getMaskRuleById(String ruleId);

    String getRuleContent(String rulePath);

    Maskrule getMaskRuleDetailByRuleId(String ruleId);

    int updateMaskRule(Maskrule maskrule);

    int updateRuleChoseById(List<MaskRuleChose> maskRuleChoseList);
}
