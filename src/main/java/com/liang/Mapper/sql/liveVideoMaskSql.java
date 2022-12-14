package com.liang.Mapper.sql;

import com.liang.Rep.CheckTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.SQL;

@Slf4j
public class liveVideoMaskSql {

    public String selectByliveRecord(CheckTask checkTask){
        return new SQL(){
            {
                SELECT("*");
                FROM("livevideomask","user");
                if(checkTask.getUserId() != null){
                    WHERE("livevideomask.user_id = #{userId}");
                    WHERE("livevideomask.user_id = user.user_id");
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
