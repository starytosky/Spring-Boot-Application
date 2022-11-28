package com.liang.Dao;

import com.liang.Bean.LiveVideoMask;
import com.liang.Bean.LocalvideoMask;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface LiveVideoMaskDao {

    //添加
    @Insert("insert into livevideomask(user_id,stream_url,out_file_path,out_filename,obs_path,task_status,use_method,model,start_time,end_time,isdelete) values(#{userId},#{streamUrl},#{outFilePath},#{outFilename},#{obsPath},#{taskStatus},#{useMethod},#{model},#{startTime},#{endTime},#{isdelete})")
    @Options(useGeneratedKeys=true, keyProperty="liveTaskId")
    int insert(LiveVideoMask liveVideoMask);

    @Update("update livevideomask set task_status=#{taskStatus},end_time=#{endTime} where live_task_id = #{liveTaskId}")
    int updateLiveVieoMaskById(LiveVideoMask liveVideoMask);

    @Select("select count(*) from livevideomask where user_id = #{userId}")
    int GetUserTaskCountByUserId(LiveVideoMask liveVideoMask);
}
