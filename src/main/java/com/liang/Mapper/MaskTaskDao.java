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
