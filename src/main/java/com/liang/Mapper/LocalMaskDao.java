package com.liang.Mapper;


import com.liang.Rep.CheckExecTask;
import com.liang.Rep.CheckLocalData;
import com.liang.Rep.CheckMaskTask;
import com.liang.Rep.LocalMask;
import com.liang.Mapper.sql.LocalMaskSql;
import com.liang.Res.ExecRecordInfo;
import com.liang.Res.LocalData;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface LocalMaskDao {

    //添加
    @Insert("insert into localmask(exec_id,user_id,task_id,rule_id,task_name,data_name,origin_path,mask_path,model,start_time,end_time,method,task_status,is_type,isdelete) values(#{#{execId},userId},#{taskId},#{ruleId},#{taskName},#{dataName},#{originPath},#{maskPath},#{model},#{startTime},#{endTime},#{method},#{taskStatus},#{isType},#{isdelete})")
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
    int deleteTask(String userId,String taskId);

    @Select("select count(*) from localmask where user_id = #{userId} and isdelete=0")
    int getRecordCountByUserId(int user_id);

    @Select("select t.exec_id from localmask t RIGHT JOIN \n" +
            "(select task_id, MAX(start_time) as \"startTime\" from localmask GROUP BY task_id) tmp \n" +
            "on t.start_time = tmp.startTime and t.task_id=tmp.task_id WHERE t.task_id = #{taskId}")
    String getRecordCountByTaskId(String taskId);

    @Select("select resource_name from resources where resource_id = #{resourceId}")
    String getResourceNameByResourceId(String resourceId);

    @SelectProvider(type = LocalMaskSql.class, method = "getExecRecordList")
    List<LocalMask> getExecRecordList(CheckExecTask checkExecTask);

    @SelectProvider(type = LocalMaskSql.class, method = "getExecRecordListCount")
    int getExecRecordListCount(CheckExecTask checkExecTask);

    @Select("SELECT\n" +
            "\tlocalmask.task_id,\n" +
            "\tlocalmask.exec_id,\n" +
            "\tmaskrule.rule_id,\n" +
            "\tlocalmask.start_time,\n" +
            "\tlocalmask.end_time,\n" +
            "\tlocalmask.method,\n" +
            "\tlocalmask.task_status,\n" +
            "\tlocalmask.data_name,\n" +
            "\tlocalmask.task_name,\n" +
            "\tlocalmask.is_type,\n" +
            "\tlocalmask.log_path,\n" +
            "\tmaskrule.rule_name,\n" +
            "\tmaskrule.rule_desc,\n" +
            "\tmaskrule.limit_content,\n" +
            "\tmaskrule.limit_form,\n" +
            "\tmasktask.data_type,\n" +
            "\tmasktask.task_desc,\n" +
            "\tresources.resource_desc,\n" +
            "\tresources.resource_info,\n" +
            "\tmaskmethod.method_name,\n" +
            "\tmaskrule.isupload,\n" +
            "\tmaskrule.rule_path\n" +
            "FROM\n" +
            "\tlocalmask\n" +
            "\tINNER JOIN masktask ON localmask.task_id = masktask.task_id\n" +
            "\tINNER JOIN maskrule ON localmask.rule_id = maskrule.rule_id\n" +
            "\tINNER JOIN resources ON masktask.data_id = resources.resource_id\n" +
            "\tINNER JOIN maskmethod ON masktask.method_id = maskmethod.mask_method_id \n" +
            "WHERE\n" +
            "\tlocalmask.exec_id = #{execId}")
    ExecRecordInfo getExecRecordInfo(String execId);
}
