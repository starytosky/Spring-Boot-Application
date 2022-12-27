package com.liang.Mapper.sql;

import com.liang.Rep.CheckMaskTask;
import com.liang.Rep.MaskTask;
import org.apache.ibatis.jdbc.SQL;

public class MaskTaskSql {

    public String updateMaskTask(MaskTask maskTask){
        return new SQL(){
            {
                UPDATE("masktask");
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

    public String selectByTaskRecord(CheckMaskTask checkMaskTask){
        return new SQL(){
            {
                SELECT("*");
                FROM("masktask");
                if(checkMaskTask.getUserId() != null){
                    WHERE("masktask.user_id = #{userId}");
                    WHERE("isdelete = 0");
                }
                if(checkMaskTask.getTaskName() != null && checkMaskTask.getTaskName().length()>0){
                    WHERE("task_name = #{taskName}");
                }
                if(checkMaskTask.getTypeId() != null){
                    WHERE("is_type = #{typeId}");
                }
                if(checkMaskTask.getTaskStatus() != null){
                    WHERE("task_status = #{taskStatus}");
                }
                if(checkMaskTask.getTotalRecord() != null){
                    String str = checkMaskTask.getTotalRecord().toString() + ","+ checkMaskTask.getRecordNumber().toString();
                    LIMIT(str);
                }
            }
        }.toString();
    }

    public String selectTaskRecordCount(CheckMaskTask checkMaskTask){
        return new SQL(){
            {
                SELECT("count(*)");
                FROM("masktask");
                if(checkMaskTask.getUserId() != null){
                    WHERE("masktask.user_id = #{userId}");
                    WHERE("isdelete = 0");
                }
                if(checkMaskTask.getTypeId() != null){
                    WHERE("is_type = #{typeId}");
                }
                if(checkMaskTask.getTaskName() != null && checkMaskTask.getTaskName().length()>0){
                    WHERE("task_name = #{taskName}");
                }
                if(checkMaskTask.getTaskStatus() != null){
                    WHERE("task_status = #{taskStatus}");
                }
            }
        }.toString();
    }
}
