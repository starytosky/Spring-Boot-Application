package com.liang.Res;

import lombok.Data;

import java.util.Date;

@Data
public class MaskDataInfo {
	private Integer maskDataId;
	private Integer maskMethodId;
	private String dataName;
	private String taskName;
	private Date time;
	private Integer isType;
	private Integer dataType;
	private String userName;
	private String ruleName;
	private String limitContent;
	private String ruleDesc;
	private String limitForm;
	private String ruleType;
	private String ruleResource;
	private Integer isupload;
	private String rulePath;
	private String methodName;
	private String method;
}
