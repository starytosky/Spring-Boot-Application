package com.liang.Dao.sql;

import com.liang.Bean.CheckRule;
import com.liang.Bean.CheckTask;
import org.apache.ibatis.jdbc.SQL;

public class MaskRuleSql {

    public String selectRule(CheckRule checkRule){
        return new SQL(){
            {
                SELECT("*");
                FROM("maskRule","user");
                if(checkRule.getUserId() != null){
                    WHERE("maskRule.user_id = user.user_id");
                    WHERE("maskRule.user_id = #{userId}");
                    WHERE("isdelete = 0");
                }
                if(checkRule.getRuleName() != null && checkRule.getRuleName().length()>0){
                    WHERE("rule_name = #{ruleName}");
                }
                if(checkRule.getDataType() != null){
                    WHERE("data_type = #{dataType}");
                }
                if(checkRule.getTotalRecord() != null){
                    String str = checkRule.getTotalRecord().toString() + ","+checkRule.getRecordNumber().toString();
                    LIMIT(str);
                }
            }
        }.toString();
    }
}
