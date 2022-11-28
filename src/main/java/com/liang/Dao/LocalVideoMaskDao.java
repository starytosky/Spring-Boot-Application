package com.liang.Dao;


import com.liang.Bean.LocalvideoMask;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface LocalVideoMaskDao {

    //添加
    @Insert("insert into localvideomask(user_id,video_path,mask_path,model,start_time,end_time,use_method,task_status,isdelete) values(#{userId},#{videoPath},#{maskPath},#{model},#{startTime},#{endTime},#{useMethod},#{taskStatus},#{isdelete})")
    public int insert(LocalvideoMask localvideoMask);

}
