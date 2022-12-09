package com.liang.Dao;


import com.liang.Bean.CheckRule;
import com.liang.Bean.LocalMask;
import com.liang.Bean.Maskrule;
import com.liang.Dao.sql.LocalMaskSql;
import com.liang.Dao.sql.MaskRuleSql;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MaskRuleDao {

//    添加规则
    @Insert("insert into maskRule(user_id,rule_name,data_type,isupload,rule_path,rule_desc,limit_content,limit_form,rule_type,rule_resource,time) values(#{userId},#{ruleName},#{dataType},#{isupload},#{rulePath},#{ruleDesc},#{limitContent},#{limitForm},#{ruleType},#{ruleResource},#{time})")
    @Options(useGeneratedKeys=true, keyProperty="ruleId")
    int insert(Maskrule maskrule);

    @SelectProvider(type = MaskRuleSql.class, method = "selectRule")
    List<Maskrule> GetRuleList(CheckRule checkRule);



//    删除
    @Update("update maskRule set isdelete = 1 where user_id = #{userId} and rule_id = #{ruleId}")
    int deleteRule(Integer userId,Integer ruleId);

    @Select("select count(*) from maskRule where user_id = #{userId} and isdelete=0")
    int getRecordCountByUserId(int user_id);

}
