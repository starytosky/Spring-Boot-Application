package com.liang.Dao;


import com.liang.Bean.CheckRule;
import com.liang.Bean.MaskTask;
import com.liang.Bean.Maskrule;
import com.liang.Dao.sql.MaskRuleSql;
import com.liang.Dao.sql.MaskTaskSql;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MaskTaskDao {
    @Insert("insert into maskTask(user_id,rule_id,method_id,task_name,task_status,mask_type,task_desc,data_id,data_path,data_type,stream_url,method,time) values(#{userId},#{ruleId},#{methodId},#{taskName},#{taskStatus},#{maskType},#{taskDesc},#{dataId},#{dataPath},#{dataType},#{streamUrl},#{method},#{time})")
    @Options(useGeneratedKeys=true, keyProperty="taskId")
    int createMaskTask(MaskTask maskTask);



    @UpdateProvider(type = MaskTaskSql.class, method = "updateMaskTask")
    int updateMaskTask(MaskTask maskTask);

}
