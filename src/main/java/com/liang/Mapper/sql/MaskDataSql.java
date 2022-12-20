package com.liang.Mapper.sql;

import com.liang.Rep.CheckMaskData;
import org.apache.ibatis.jdbc.SQL;

public class MaskDataSql {
	public String selectMaskData(CheckMaskData checkMaskData){
		return new SQL(){
			{
				SELECT("*");
				FROM("maskdata","userinfo","masktask");
				if(checkMaskData.getUserId() != null){
					WHERE("maskdata.user_id = userinfo.user_id");
					WHERE("maskdata.user_id = #{userId}");
					WHERE("maskdata.task_id = masktask.task_id");
					WHERE("maskdata.isdelete = 0");
				}
				if(checkMaskData.getTaskName() != null && checkMaskData.getTaskName().length()>0){
					WHERE("masktask.task_name = #{taskName}");
				}
				if(checkMaskData.getTypeId() != null){
					WHERE("maskdata.is_type = #{isType}");
				}
				if(checkMaskData.getDataType() != null){
					WHERE("maskdata.data_type = #{dataType}");
				}
				if(checkMaskData.getTotalRecord() != null){
					String str = checkMaskData.getTotalRecord().toString() + ","+ checkMaskData.getRecordNumber().toString();
					LIMIT(str);
				}
			}
		}.toString();
	}

	public String MaskDataCount(CheckMaskData checkMaskData){
		return new SQL(){
			{
				SELECT("count(*)");
				FROM("maskdata","userinfo","masktask");
				if(checkMaskData.getUserId() != null){
					WHERE("maskdata.user_id = userinfo.user_id");
					WHERE("maskdata.user_id = #{userId}");
					WHERE("maskdata.task_id = masktask.task_id");
					WHERE("maskdata.isdelete = 0");
				}
				if(checkMaskData.getTaskName() != null && checkMaskData.getTaskName().length()>0){
					WHERE("masktask.task_name = #{taskName}");
				}
				if(checkMaskData.getTypeId() != null){
					WHERE("maskdata.is_type = #{isType}");
				}
				if(checkMaskData.getDataType() != null){
					WHERE("maskdata.data_type = #{dataType}");
				}
			}
		}.toString();
	}
}
