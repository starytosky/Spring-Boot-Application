package com.liang.service.impl;


import com.liang.Mapper.MaskRuleMapper;
import com.liang.Rep.CheckRule;
import com.liang.Rep.Maskrule;
import com.liang.Mapper.MaskRuleDao;
import com.liang.common.util.Tool;
import com.liang.service.MaskRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MaskRuleServiceImpl implements MaskRuleService {



    @Autowired
    private MaskRuleDao maskRuleDao;

    @Autowired
    private MaskRuleMapper maskRuleMapper;

    @Override
    public int addMaskRule(Maskrule maskrule) {
        return maskRuleMapper.insert(maskrule);
    }

    @Override
    public List<Maskrule> getMaskRule(CheckRule checkRule) {
        return maskRuleDao.GetRuleList(checkRule);
    }

    @Override
    public int selectRuleCount(CheckRule checkRule) {
        return maskRuleDao.selectRuleCount(checkRule);
    }

    @Override
    public int deleteMaskRule(String userId, Integer ruleId) {
        return maskRuleDao.deleteRule(userId,ruleId);
    }

    @Override
    public int getRecordCountByUserId(String userId) {
        return maskRuleDao.getRecordCountByUserId(userId);
    }

    @Override
    public Maskrule getMaskRuleById(Integer ruleId) {
//        QueryWrapper<Maskrule> wrapper = new QueryWrapper<>();
//        wrapper.eq("rule_id",ruleId);
//        wrapper.eq("isdelete",0);
//        return maskRuleMapper.selectById(wrapper);
        return maskRuleDao.getRecordByRuleId(ruleId);
    }

    public Maskrule getMaskRuleDetailByRuleId(Integer ruleId) {
        Maskrule maskrule = maskRuleDao.getRecordByRuleId(ruleId);
        if(maskrule != null) {
            if(maskrule.getIsupload()==1) {
                maskrule.setRuleDesc(getRuleContent(maskrule.getRulePath()));
            }
        }
        return maskrule;
    }

    // 获取文件内容返回字符串
    @Override
    public String getRuleContent(String rulePath) {
        return Tool.getFileContent(rulePath);
    }
}
