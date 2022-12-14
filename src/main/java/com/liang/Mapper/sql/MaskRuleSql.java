package com.liang.Mapper.sql;

import com.liang.Rep.CheckRule;
import org.apache.ibatis.jdbc.SQL;

public class MaskRuleSql {

    public String selectRule(CheckRule checkRule){
        return new SQL(){
            {
                SELECT("*");
                FROM("maskrule","userinfo");
                if(checkRule.getUserId() != null){
                    WHERE("maskrule.user_id = userinfo.user_id");
                    WHERE("maskrule.user_id = #{userId}");
                    WHERE("maskrule.isdelete = 0");
                }
                if(checkRule.getRuleName() != null && checkRule.getRuleName().length()>0){
                    WHERE("maskrule.rule_name = #{ruleName}");
                }
                if(checkRule.getIsMask()!=null && checkRule.getIsMask() == 1) {
                    WHERE("maskrule.ischose = 1");
                }
                if(checkRule.getDataType() != null){
                    WHERE("maskrule.data_type = #{dataType}");
                }
                if(checkRule.getTotalRecord() != null){
                    String str = checkRule.getTotalRecord().toString() + ","+checkRule.getRecordNumber().toString();
                    LIMIT(str);
                }
            }
        }.toString();
    }

    public String selectRuleCount(CheckRule checkRule){
        return new SQL(){
            {
                SELECT("count(*)");
                FROM("maskrule","userinfo");
                if(checkRule.getUserId() != null){
                    WHERE("maskrule.user_id = userinfo.user_id");
                    WHERE("maskrule.user_id = #{userId}");
                    WHERE("maskrule.isdelete = 0");
                }
                if(checkRule.getIsMask()!=null && checkRule.getIsMask() == 1) {
                    WHERE("maskrule.ischose = 1");
                }
                if(checkRule.getRuleName() != null && checkRule.getRuleName().length()>0){
                    WHERE("maskrule.rule_name = #{ruleName}");
                }
                if(checkRule.getDataType() != null){
                    WHERE("maskrule.data_type = #{dataType}");
                }
            }
        }.toString();
    }

}
