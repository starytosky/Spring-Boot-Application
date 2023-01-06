package com.liang.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liang.Mapper.MaskRuleMapper;
import com.liang.Rep.CheckRule;
import com.liang.Rep.MaskTask;
import com.liang.Rep.Maskrule;
import com.liang.Mapper.MaskRuleDao;
import com.liang.Rep.MaskRuleChose;
import com.liang.common.util.IdRandomUtils;
import com.liang.common.util.Tool;
import com.liang.service.MaskRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
public class MaskRuleServiceImpl implements MaskRuleService {



    @Autowired
    private MaskRuleDao maskRuleDao;

    @Autowired
    private MaskRuleMapper maskRuleMapper;

    // 算法允许传入的参数
    private static final String[] checkmodelList = {"person","plate","sign","qrcode","idcard","nake","all"};

    @Override
    public int addMaskRule(Maskrule maskrule) {
        maskrule.setRuleId("ru"+IdRandomUtils.getRandomID().toString());
        maskrule.setIschose(1);
        //判断传入的限制内容是否符合规范
        if(checkParameters(maskrule.getLimitContent().trim().split(","))){
            return maskRuleMapper.insert(maskrule);
        }else return -1;
    }

    @Override
    public Boolean checkParameters(String[] limitContent) {
        HashSet<String> hset= new HashSet<>();
        // hset stores all the values of checkmodelList
        for(int i = 0; i < checkmodelList.length; i++)
        {
            if(!hset.contains(checkmodelList[i]))
                hset.add(checkmodelList[i]);
        }
        for(int i = 0; i < limitContent.length; i++)
        {
            if(!hset.contains(limitContent[i]))
                return false;
        }
       return true;
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
    public int deleteMaskRule(String userId, String ruleId) {
        return maskRuleDao.deleteRule(userId,ruleId);
    }

    @Override
    public int getRecordCountByUserId(String userId) {
        return maskRuleDao.getRecordCountByUserId(userId);
    }

    @Override
    public Maskrule getMaskRuleById(String ruleId) {
//        QueryWrapper<Maskrule> wrapper = new QueryWrapper<>();
//        wrapper.eq("rule_id",ruleId);
//        wrapper.eq("isdelete",0);
//        return maskRuleMapper.selectById(wrapper);
        return maskRuleDao.getRecordByRuleId(ruleId);
    }

    public Maskrule getMaskRuleDetailByRuleId(String ruleId) {
        Maskrule maskrule = maskRuleDao.getRecordByRuleId(ruleId);
        if(maskrule != null) {
            if(maskrule.getIsupload()==1) {
                maskrule.setRuleDesc(getRuleContent(maskrule.getRulePath()));
            }
        }
        return maskrule;
    }

    @Override
    public int updateMaskRule(Maskrule maskrule) {
        QueryWrapper<Maskrule> wrapper = new QueryWrapper<>();
        wrapper.eq("rule_id",maskrule.getRuleId());
        return maskRuleMapper.update(maskrule,wrapper);
    }

    @Override
    public int updateRuleChoseById(MaskRuleChose maskRuleChose) {
        if(maskRuleDao.selectRuleById(maskRuleChose.getRuleId()) == 1) {
            maskRuleDao.updateRuleChoseById(maskRuleChose);
            return 1;
        }else return 0;
    }

    // 获取文件内容返回字符串
    @Override
    public String getRuleContent(String rulePath) {
        return Tool.getFileContent(rulePath);
    }
}
