package com.liang.Mapper;

import com.liang.Mapper.sql.LocalMaskSql;
import com.liang.Rep.CheckExecTask;
import com.liang.Rep.CheckMaskTask;
import com.liang.Rep.LiveVideoMask;
import com.liang.Mapper.sql.liveVideoMaskSql;
import com.liang.Rep.LocalMask;
import com.liang.Res.ExecRecordInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface LiveVideoMaskDao {

    //添加
    @Insert("insert into livevideomask(exec_id,user_id,stream_url,out_file_path,out_filename,obs_path,task_status,use_method,model,start_time,end_time,isdelete) values(#{execId},#{userId},#{streamUrl},#{outFilePath},#{outFilename},#{obsPath},#{taskStatus},#{useMethod},#{model},#{startTime},#{endTime},#{isdelete})")
    @Options(useGeneratedKeys=true, keyProperty="taskId")
    int insert(LiveVideoMask liveVideoMask);

    @Update("update livevideomask set task_status=#{taskStatus},end_time=#{endTime} where exec_id = #{execId}")
    int updateLiveVieoMaskById(LiveVideoMask liveVideoMask);

    @Select("select count(*) from livevideomask where user_id = #{userId} and task_id = #{taskId}")
    int GetUserTaskCountByUserId(LiveVideoMask liveVideoMask);

    @Select("select t.exec_id from livevideomask t RIGHT JOIN \n" +
            "(select task_id, MAX(start_time) as \"startTime\" from livevideomask GROUP BY task_id) tmp \n" +
            "on t.start_time = tmp.startTime and t.task_id=tmp.task_id WHERE t.task_id = #{taskId}")
    String GetUserTaskCountByTaskId(String taskId);


//    @Select("select * from livevideomask where user_id = #{userId} and task_status = #{status} LIMIT #{totalRecord},#{recordNumber}")
    @SelectProvider(type = liveVideoMaskSql.class, method = "selectByliveRecord")
    List<LiveVideoMask> GetUserTaskByUserId(CheckMaskTask checkMaskTask);

    @Update("update livevideomask set isdelete = 1 where user_id = #{userId} and task_id = #{taskId}")
    int deleteTask(String userId,String taskId);

    @Select("select count(*) from livevideomask where user_id = #{userId} and isdelete=0")
    int getRecordCountByUserId(String user_id);

    @SelectProvider(type = liveVideoMaskSql.class, method = "getExecRecordList")
    List<LiveVideoMask> getExecRecordList(CheckExecTask checkExecTask);

    @SelectProvider(type = liveVideoMaskSql.class, method = "getExecRecordListCount")
    int getExecRecordListCount(CheckExecTask checkExecTask);

    @Select("SELECT\n" +
            "\tlivevideomask.exec_id,\n" +
            "\tlivevideomask.task_id,\n" +
            "\tlivevideomask.task_name,\n" +
            "\tlivevideomask.stream_url,\n" +
            "\tlivevideomask.out_filename,\n" +
            "\tlivevideomask.stream_url,\n" +
            "\tmasktask.method,\n" +
            "\tlivevideomask.start_time,\n" +
            "\tlivevideomask.end_time,\n" +
            "\tlivevideomask.is_type,\n" +
            "\tlivevideomask.log_path,\n" +
            "\tmaskrule.rule_id,\n" +
            "\tmaskrule.rule_name,\n" +
            "\tuserinfo.user_name,\n" +
            "\tmaskrule.rule_desc,\n" +
            "\tmaskrule.limit_content,\n" +
            "\tmaskrule.limit_form,\n" +
            "\tmaskmethod.method_name,\n" +
            "\tmaskrule.isupload, \n" +
            "\tmaskrule.rule_path,\n" +
            "\tlivevideomask.task_status,\n" +
            "\tmasktask.task_desc,\n" +
            "\tmasktask.data_type\n" +
            "FROM\n" +
            "\tlivevideomask\n" +
            "\tINNER JOIN masktask ON livevideomask.task_id = masktask.task_id\n" +
            "\tINNER JOIN userinfo ON livevideomask.user_id = userinfo.user_id\n" +
            "\tINNER JOIN maskrule ON livevideomask.rule_id = maskrule.rule_id\n" +
            "\tINNER JOIN maskmethod ON masktask.method_id = maskmethod.mask_method_id \n" +
            "WHERE\n" +
            "\tlivevideomask.exec_id = #{execId}")
    ExecRecordInfo getExecRecordInfo(String execId);


}
