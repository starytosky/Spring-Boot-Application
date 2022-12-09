package com.liang.Dao;


import com.liang.Bean.CheckTask;
import com.liang.Bean.LocalMask;
import com.liang.Dao.sql.LocalMaskSql;
import com.liang.Dao.sql.liveVideoMaskSql;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface LocalMaskDao {

    //添加
    @Insert("insert into localmask(user_id,video_path,mask_path,model,start_time,end_time,use_method,task_status,isdelete) values(#{userId},#{videoPath},#{maskPath},#{model},#{startTime},#{endTime},#{useMethod},#{taskStatus},#{isdelete})")
    @Options(useGeneratedKeys=true, keyProperty="taskId")
    int insert(LocalMask localvideoMask);


    @Update("update localmask set task_status=#{taskStatus},end_time=#{endTime} where task_id = #{taskId}")
    void updateLocalVieoMaskById(LocalMask localvideoMask);

    @SelectProvider(type = LocalMaskSql.class, method = "selectBylocalRecord")
    List<LocalMask> GetUserTaskByUserId(CheckTask checkTask);

    @Update("update localmask set isdelete = 1 where user_id = #{userId} and task_id = #{taskId}")
    int deleteTask(Integer userId,Integer taskId);

    @Select("select count(*) from localmask where user_id = #{userId} and isdelete=0")
    int getRecordCountByUserId(int user_id);

}
