package com.liang.Mapper.sql;

import com.liang.Rep.CheckExecTask;
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


    public String getExecRecordList(CheckExecTask checkExecTask){
        return new SQL(){
            {
                SELECT("*");
                FROM("livevideomask","masktask");
                if(checkExecTask.getTaskId()!=null){
                    WHERE("livevideomask.task_id = #{taskId}");
                    WHERE("livevideomask.task_id = masktask.task_id");
                }
                if(checkExecTask.getExecId()!=null){
                    WHERE("livevideomask.exec_id = #{execId}");
                }
                if(checkExecTask.getUserId()!=null){
                    WHERE("livevideomask.user_id = #{userId}");
                    WHERE("livevideomask.isdelete = 0");
                }
                if(checkExecTask.getTaskStatus()!=null){
                    WHERE("livevideomask.task_status = #{taskStatus}");
                }
//                if(checkExecTask.getTotalRecord() != null){
//                    String str = checkExecTask.getTotalRecord().toString() + ","+ checkExecTask.getRecordNumber().toString();
//                    LIMIT(str);
//                }
            }
        }.toString();
    }

    // 暂时不用
    public String getExecRecordListCount(CheckExecTask checkExecTask){
        return new SQL(){
            {
                SELECT("count(*)");
                FROM("livevideomask");
                if(checkExecTask.getTaskId()!=null){
                    WHERE("task_id = #{taskId}");
                }
                if(checkExecTask.getExecId()!=null){
                    WHERE("exec_id = #{execId}");
                }
                if(checkExecTask.getUserId()!=null){
                    WHERE("user_id = #{userId}");
                    WHERE("isdelete = 0");
                }
                if(checkExecTask.getTaskStatus()!=null){
                    WHERE("task_status = #{taskStatus}");
                }
            }
        }.toString();
    }
}
