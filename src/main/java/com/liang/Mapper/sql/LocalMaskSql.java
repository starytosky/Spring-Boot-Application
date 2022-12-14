package com.liang.Mapper.sql;

import com.liang.Rep.CheckTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.SQL;

@Slf4j
public class LocalMaskSql {
    public String selectBylocalRecord(CheckTask checkTask){
        return new SQL(){
            {
//      select * from localmask,user where localmask.user_id = user.user_id and localmask.user_id = #{userId} and task_status = #{status} and isdelete = 0 LIMIT #{totalRecord},#{recordNumber}
                SELECT("*");
                FROM("localmask","user");
                if(checkTask.getUserId() != null){
                    WHERE("localmask.user_id = user.user_id");
                    WHERE("localmask.user_id = #{userId}");
                    WHERE("isdelete = 0");
                }
                if(checkTask.getTaskName() != null && checkTask.getTaskName().length()>0){
                    WHERE("task_name = #{taskName}");
                }
                if(checkTask.getTaskStatus() != null){
                    WHERE("task_status = #{taskStatus}");
                }
                if(checkTask.getTotalRecord() != null){
                    String str = checkTask.getTotalRecord().toString() + ","+checkTask.getRecordNumber().toString();
                    LIMIT(str);
                }
            }
        }.toString();
    }
}
