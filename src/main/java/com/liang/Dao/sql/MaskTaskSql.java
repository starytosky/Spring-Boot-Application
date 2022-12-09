package com.liang.Dao.sql;

import com.liang.Bean.MaskTask;
import org.apache.ibatis.jdbc.SQL;

public class MaskTaskSql {

    public String updateMaskTask(MaskTask maskTask){
        return new SQL(){
            {
                UPDATE("maskTask");
                if(maskTask.getRuleId() != null){
                    SET("rule_id = #{ruleId}");
                }
                if(maskTask.getMethodId() != null){
                    SET("method_id = #{methodId}");
                }
                if(maskTask.getTaskDesc() != null){
                    SET("task_desc = #{taskDesc}");
                }
                if(maskTask.getMethod() != null){
                    SET("method = #{method}");
                }
                if(maskTask.getTime() != null){
                    SET("time = #{time}");
                }
                WHERE("task_id = #{taskId}");
            }
        }.toString();
    }
}
