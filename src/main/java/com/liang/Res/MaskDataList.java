package com.liang.Res;

import lombok.Data;

import java.util.Date;

@Data
public class MaskDataList {
	private String maskDataId;
	private String userId;
	private String taskId;
	private String taskName;
	private String userName;
	private String maskPath;
	private Integer dataType;
	private Integer isType;
	private String dataName;
	private Date time;
}
