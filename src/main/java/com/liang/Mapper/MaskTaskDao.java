package com.liang.Mapper;


import com.liang.Mapper.sql.LocalMaskSql;
import com.liang.Rep.CheckMaskTask;
import com.liang.Rep.MaskTask;
import com.liang.Mapper.sql.MaskTaskSql;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MaskTaskDao {
    @Insert("insert into masktask(task_id,user_id,rule_id,method_id,task_name,task_status,is_type,task_desc,data_id,data_path,data_type,stream_url,method,time) values(#{taskId},#{userId},#{ruleId},#{methodId},#{taskName},#{taskStatus},#{isType},#{taskDesc},#{dataId},#{dataPath},#{dataType},#{streamUrl},#{method},#{time})")
    @Options(useGeneratedKeys=true, keyProperty="taskId")
    int createMaskTask(MaskTask maskTask);



    @UpdateProvider(type = MaskTaskSql.class, method = "updateMaskTask")
    int updateMaskTask(MaskTask maskTask);

    @Update("update masktask set task_status=#{status} where task_id = #{taskId}")
    int updateTaskStatus(String taskId,Integer status);

    @Update("update masktask set isdelete=1 where task_id = #{taskId}")
    int deleteTask(String taskID);

    @SelectProvider(type = MaskTaskSql.class, method = "selectByTaskRecord")
    List<MaskTask> getTaskRecord(CheckMaskTask checkMaskTask);

    @SelectProvider(type = MaskTaskSql.class, method = "selectTaskRecordCount")
    int getTaskRecordCount(CheckMaskTask checkMaskTask);



}
