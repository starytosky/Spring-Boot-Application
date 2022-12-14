package com.liang.Mapper;


import com.liang.Rep.MaskTask;
import com.liang.Mapper.sql.MaskTaskSql;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MaskTaskDao {
    @Insert("insert into masktask(user_id,rule_id,method_id,task_name,task_status,is_type,task_desc,data_id,data_path,data_type,stream_url,method,time) values(#{userId},#{ruleId},#{methodId},#{taskName},#{taskStatus},#{isType},#{taskDesc},#{dataId},#{dataPath},#{dataType},#{streamUrl},#{method},#{time})")
    @Options(useGeneratedKeys=true, keyProperty="taskId")
    int createMaskTask(MaskTask maskTask);



    @UpdateProvider(type = MaskTaskSql.class, method = "updateMaskTask")
    int updateMaskTask(MaskTask maskTask);

    @Update("update masktask set task_status=#{status} where task_id = #{taskId}")
    int updateTaskStatus(Integer taskId,Integer status);

}
