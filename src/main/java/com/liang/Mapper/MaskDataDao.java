package com.liang.Mapper;

import com.liang.Mapper.sql.LocalMaskSql;
import com.liang.Mapper.sql.MaskDataSql;
import com.liang.Rep.*;
import com.liang.Res.MaskDataInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MaskDataDao {
	@Select("SELECT maskdata.mask_data_id,maskdata.data_name,masktask.task_name,maskdata.time,maskdata.is_type,maskdata.data_type,userinfo.user_name,maskrule.rule_name,maskrule.limit_content,maskrule.rule_desc,maskrule.limit_form,maskrule.rule_type,maskrule.rule_resource,maskmethod.method_name,localmask.method FROM maskdata,userinfo,maskrule,masktask,localmask,maskmethod WHERE maskdata.user_id = userinfo.user_id AND maskdata.task_id = masktask.task_id AND masktask.rule_id = maskrule.rule_id AND localmask.exec_id = maskdata.exec_id AND maskdata.isdelete = 0 AND maskdata.mask_data_id = #{maskDataId}")
	MaskDataInfo getMaskDataInfo(int maskDataId);

	@SelectProvider(type = MaskDataSql.class, method = "selectMaskData")
	List<MaskData> selectMaskData(CheckMaskData checkMaskData);

	@SelectProvider(type = MaskDataSql.class, method = "MaskDataCount")
	int MaskDataCount(CheckMaskData checkMaskData);

	@Update("update maskdata set isdelete = 1 where user_id = #{userId} and mask_data_id = #{maskDataId}")
	int deleteMaskData(String userId,int maskDataId);
}
