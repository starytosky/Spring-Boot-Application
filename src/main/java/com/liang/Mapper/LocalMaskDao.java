package com.liang.Mapper;


import com.liang.Rep.CheckLocalData;
import com.liang.Rep.CheckMaskTask;
import com.liang.Rep.LocalMask;
import com.liang.Mapper.sql.LocalMaskSql;
import com.liang.Res.LocalData;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface LocalMaskDao {

    //添加
    @Insert("insert into localmask(user_id,task_id,rule_id,task_name,data_name,origin_path,mask_path,model,start_time,end_time,method,task_status,is_type,isdelete) values(#{userId},#{taskId},#{ruleId},#{taskName},#{dataName},#{originPath},#{maskPath},#{model},#{startTime},#{endTime},#{method},#{taskStatus},#{isType},#{isdelete})")
    @Options(useGeneratedKeys=true, keyProperty="execId")
    int insert(LocalMask localvideoMask);


    @Update("update localmask set task_status=#{taskStatus},end_time=#{endTime} where task_id = #{taskId}")
    void updateLocalVieoMaskById(LocalMask localvideoMask);

    @SelectProvider(type = LocalMaskSql.class, method = "selectBylocalRecord")
    List<LocalMask> GetUserTaskByUserId(CheckMaskTask checkMaskTask);

    @SelectProvider(type = LocalMaskSql.class, method = "selectLocalData")
    List<LocalData> selectLocalData(CheckLocalData checkLocalData);

    @SelectProvider(type = LocalMaskSql.class, method = "LocalDataCount")
    int LocalDataCount(CheckLocalData checkLocalData);

    @Update("update localmask set isdelete = 1 where user_id = #{userId} and task_id = #{taskId}")
    int deleteTask(Integer userId,Integer taskId);

    @Select("select count(*) from localmask where user_id = #{userId} and isdelete=0")
    int getRecordCountByUserId(int user_id);

    @Select("select count(*) from localmask where task_id = #{taskId}")
    int getRecordCountByTaskId(int taskId);

}
