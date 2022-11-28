package com.liang.Dao;

import com.liang.Bean.LiveVideoMask;
import com.liang.Bean.LocalvideoMask;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface LiveVideoMaskDao {

    //添加
    @Insert("insert into livevideomask(user_id,stream_url,out_file_path,out_filename,task_status,use_method,model,start_time,end_time,isdelete) values(#{userId},#{streamUrl},#{outFilePath},#{outFilename},#{taskStatus},#{useMethod},#{model},#{startTime},#{endTime},#{isdelete})")
    public int insert(LiveVideoMask liveVideoMask);
}
