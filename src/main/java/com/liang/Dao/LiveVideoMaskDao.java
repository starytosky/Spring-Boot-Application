package com.liang.Dao;

import com.liang.Bean.CheckTask;
import com.liang.Bean.LiveVideoMask;
import com.liang.Dao.sql.liveVideoMaskSql;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface LiveVideoMaskDao {

    //添加
    @Insert("insert into livevideomask(user_id,stream_url,out_file_path,out_filename,obs_path,task_status,use_method,model,start_time,end_time,isdelete) values(#{userId},#{streamUrl},#{outFilePath},#{outFilename},#{obsPath},#{taskStatus},#{useMethod},#{model},#{startTime},#{endTime},#{isdelete})")
    @Options(useGeneratedKeys=true, keyProperty="taskId")
    int insert(LiveVideoMask liveVideoMask);

    @Update("update livevideomask set task_status=#{taskStatus},end_time=#{endTime} where task_id = #{taskId}")
    int updateLiveVieoMaskById(LiveVideoMask liveVideoMask);

    @Select("select count(*) from livevideomask where user_id = #{userId}")
    int GetUserTaskCountByUserId(LiveVideoMask liveVideoMask);


//    @Select("select * from livevideomask where user_id = #{userId} and task_status = #{status} LIMIT #{totalRecord},#{recordNumber}")
    @SelectProvider(type = liveVideoMaskSql.class, method = "selectByliveRecord")
    List<LiveVideoMask> GetUserTaskByUserId(CheckTask checkTask);

    @Update("update livevideomask set isdelete = 1 where user_id = #{userId} and task_id = #{taskId}")
    int deleteTask(Integer userId,Integer taskId);

    @Select("select count(*) from livevideomask where user_id = #{userId} and isdelete=0")
    int getRecordCountByUserId(int user_id);


}
