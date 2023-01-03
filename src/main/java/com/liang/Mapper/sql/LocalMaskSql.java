package com.liang.Mapper.sql;

import com.liang.Rep.CheckExecTask;
import com.liang.Rep.CheckLocalData;
import com.liang.Rep.CheckMaskTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.SQL;

@Slf4j
public class LocalMaskSql {
    public String selectBylocalRecord(CheckMaskTask checkMaskTask){
        return new SQL(){
            {
//      select * from localmask,user where localmask.user_id = user.user_id and localmask.user_id = #{userId} and task_status = #{status} and isdelete = 0 LIMIT #{totalRecord},#{recordNumber}
                SELECT("*");
                FROM("localmask","userinfo");
                if(checkMaskTask.getUserId() != null){
                    WHERE("localmask.user_id = userinfo.user_id");
                    WHERE("localmask.user_id = #{userId}");
                    WHERE("localmask.isdelete = 0");
                }
                if(checkMaskTask.getTaskName() != null && checkMaskTask.getTaskName().length()>0){
                    WHERE("localmask.task_name = #{taskName}");
                }
                if(checkMaskTask.getTaskStatus() != null){
                    WHERE("localmask.task_status = #{taskStatus}");
                }
                if(checkMaskTask.getTotalRecord() != null){
                    String str = checkMaskTask.getTotalRecord().toString() + ","+ checkMaskTask.getRecordNumber().toString();
                    LIMIT(str);
                }
            }
        }.toString();
    }

    public String selectLocalData(CheckLocalData checkLocalData){
        return new SQL(){
            {
                SELECT("*");
                FROM("resources");
                WHERE("use_scene LIKE '%数据脱敏%'");
                if(checkLocalData.getResourceName() != null && checkLocalData.getResourceName().length()>0){
                    WHERE("resource_name = #{resourceName}");
                }
                if(checkLocalData.getResourceType() != null && checkLocalData.getResourceType().length()>0){
                    WHERE("resource_type = #{resourceType}");
                }
                if(checkLocalData.getTotalRecord() != null){
                    String str = checkLocalData.getTotalRecord().toString() + ","+ checkLocalData.getRecordNumber().toString();
                    LIMIT(str);
                }
            }
        }.toString();
    }

    public String LocalDataCount(CheckLocalData checkLocalData){
        return new SQL(){
            {
                SELECT("count(*)");
                FROM("resources");
                WHERE("use_scene LIKE '%数据脱敏%'");
                if(checkLocalData.getResourceName() != null && checkLocalData.getResourceName().length()>0){
                    WHERE("resource_name = #{resourceName}");
                }
                if(checkLocalData.getResourceType() != null && checkLocalData.getResourceType().length()>0){
                    WHERE("resource_type = #{resourceType}");
                }
            }
        }.toString();
    }

    public String getExecRecordList(CheckExecTask checkExecTask){
        return new SQL(){
            {
                SELECT("*");
                FROM("localmask","masktask");
                if(checkExecTask.getTaskId()!=null){
                    WHERE("localmask.task_id = #{taskId}");
                    WHERE("localmask.task_id = masktask.task_id");
                }
                if(checkExecTask.getExecId()!=null){
                    WHERE("localmask.exec_id = #{execId}");
                }
                if(checkExecTask.getUserId()!=null){
                    WHERE("localmask.user_id = #{userId}");
                    WHERE("localmask.isdelete = 0");
                }
                if(checkExecTask.getTaskStatus()!=null){
                    WHERE("localmask.task_status = #{taskStatus}");
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
                FROM("localmask","masktask");
                if(checkExecTask.getTaskId()!=null){
                    WHERE("localmask.task_id = #{taskId}");
                }
                if(checkExecTask.getExecId()!=null){
                    WHERE("localmask.exec_id = #{execId}");
                }
                if(checkExecTask.getUserId()!=null){
                    WHERE("localmask.user_id = #{userId}");
                    WHERE("localmask.isdelete = 0");
                }
                if(checkExecTask.getTaskStatus()!=null){
                    WHERE("localmask.task_status = #{taskStatus}");
                }
            }
        }.toString();
    }
}
