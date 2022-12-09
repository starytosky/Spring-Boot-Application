package com.liang.service.impl;


import com.liang.Bean.CheckRule;
import com.liang.Bean.Maskrule;
import com.liang.Dao.MaskRuleDao;
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


    @Override
    public int addMaskRule(Maskrule maskrule) {
        return maskRuleDao.insert(maskrule);
    }

    @Override
    public List<Maskrule> getMaskRule(CheckRule checkRule) {
        return maskRuleDao.GetRuleList(checkRule);
    }

    @Override
    public int deleteMaskRule(Integer userId, Integer ruleId) {
        return maskRuleDao.deleteRule(userId,ruleId);
    }

    @Override
    public int getRecordCountByUserId(Integer userId) {
        return maskRuleDao.getRecordCountByUserId(userId);
    }


}
