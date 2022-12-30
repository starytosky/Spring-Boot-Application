package com.liang.Mapper;

import com.liang.Mapper.sql.LocalMaskSql;
import com.liang.Mapper.sql.MaskDataSql;
import com.liang.Rep.*;
import com.liang.Res.MaskDataInfo;
import com.liang.Res.MaskDataList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MaskDataDao {

	@SelectProvider(type = MaskDataSql.class, method = "selectMaskData")
	List<MaskDataList> selectMaskData(CheckMaskData checkMaskData);

	@SelectProvider(type = MaskDataSql.class, method = "MaskDataCount")
	int MaskDataCount(CheckMaskData checkMaskData);

	@Update("update maskdata set isdelete = 1 where user_id = #{userId} and mask_data_id = #{maskDataId}")
	int deleteMaskData(String userId,int maskDataId);

	@Select("SELECT\n" +
			"\tmaskdata.mask_data_id,\n" +
			"\tmaskdata.data_name,\n" +
			"\tmasktask.task_name,\n" +
			"\tmaskdata.TIME,\n" +
			"\tmaskdata.is_type,\n" +
			"\tmaskdata.data_type,\n" +
			"\tuserinfo.user_name,\n" +
			"\tmaskrule.rule_name,\n" +
			"\tmaskrule.limit_content,\n" +
			"\tmaskrule.rule_desc,\n" +
			"\tmaskrule.limit_form,\n" +
			"\tmaskrule.rule_type,\n" +
			"\tmaskrule.rule_resource,\n" +
			"\tmaskmethod.method_name, \n" +
			"\tmasktask.method, \n" +
			"\tmaskrule.isupload, \n" +
			"\tmaskrule.rule_path, \n" +
			"\tmaskmethod.mask_method_id \n" +
			"FROM\n" +
			"\tmaskdata\n" +
			"\tINNER JOIN userinfo ON maskdata.user_id = userinfo.user_id\n" +
			"\tINNER JOIN maskrule ON maskdata.rule_id = maskrule.rule_id\n" +
			"\tINNER JOIN maskmethod ON maskdata.method_id = maskmethod.mask_method_id\n" +
			"\tINNER JOIN masktask ON maskdata.task_id = masktask.task_id\n" +
			"WHERE maskdata.mask_data_id = #{maskDataId} AND\n" +
			"\tmaskdata.isdelete = 0")
	MaskDataInfo getMaskDataInfo(Integer maskDataId);
}
