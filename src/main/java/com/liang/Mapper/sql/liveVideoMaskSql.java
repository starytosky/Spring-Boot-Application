package com.liang.Mapper.sql;

import com.liang.Rep.CheckMaskTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.SQL;

@Slf4j
public class liveVideoMaskSql {

    public String selectByliveRecord(CheckMaskTask checkMaskTask){
        return new SQL(){
            {
                SELECT("*");
                FROM("livevideomask","userinfo");
                if(checkMaskTask.getUserId() != null){
                    WHERE("livevideomask.user_id = #{userId}");
                    WHERE("livevideomask.user_id = userinfo.user_id");
                    WHERE("livevideomask.isdelete = 0");
                }
                if(checkMaskTask.getTaskName() != null && checkMaskTask.getTaskName().length()>0){

                    WHERE("livevideomask.task_name = #{taskName}");
                }
                if(checkMaskTask.getTaskStatus() != null){
                    WHERE("livevideomask.task_status = #{taskStatus}");
                }
                if(checkMaskTask.getTotalRecord() != null){
                    String str = checkMaskTask.getTotalRecord().toString() + ","+ checkMaskTask.getRecordNumber().toString();
                    LIMIT(str);
                }
            }
        }.toString();
    }
}
