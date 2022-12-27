package com.liang.Mapper;


import com.liang.Rep.CheckRule;
import com.liang.Rep.Maskrule;
import com.liang.Mapper.sql.MaskRuleSql;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MaskRuleDao {

//    添加规则
    @Insert("insert into maskrule(user_id,rule_name,data_type,isupload,rule_path,rule_desc,limit_content,limit_form,rule_type,rule_resource,time) values(#{userId},#{ruleName},#{dataType},#{isupload},#{rulePath},#{ruleDesc},#{limitContent},#{limitForm},#{ruleType},#{ruleResource},#{time})")
    @Options(useGeneratedKeys=true, keyProperty="ruleId")
    int insert(Maskrule maskrule);

    @SelectProvider(type = MaskRuleSql.class, method = "selectRule")
    List<Maskrule> GetRuleList(CheckRule checkRule);

    @SelectProvider(type = MaskRuleSql.class, method = "selectRuleCount")
    int selectRuleCount(CheckRule checkRule);

    @Select("select * from maskrule,userinfo where maskrule.user_id = userinfo.user_id AND maskrule.rule_id = #{ruleId} AND  isdelete=0")
    Maskrule getRecordByRuleId(int ruleId);



//    删除
    @Update("update maskrule set isdelete = 1 where user_id = #{userId} and rule_id = #{ruleId}")
    int deleteRule(String userId,Integer ruleId);

    @Select("select count(*) from maskrule where user_id = #{userId} and isdelete=0")
    int getRecordCountByUserId(String user_id);


}
