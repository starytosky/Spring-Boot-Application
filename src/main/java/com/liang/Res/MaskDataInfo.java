package com.liang.Res;

import lombok.Data;

import java.util.Date;

@Data
public class MaskDataInfo {
	private Integer maskDataId;
	private String dataName;
	private String taskName;
	private Date time;
	private Integer isType;
	private Integer dataType;
	private String name;
	private String ruleName;
	private String limitContent;
	private String ruleDesc;
	private String limitForm;
	private String ruleType;
	private String ruleResource;
	private String methodName;
	private String method;
}
