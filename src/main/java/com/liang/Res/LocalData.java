package com.liang.Res;

import lombok.Data;

import java.util.Date;

@Data
public class LocalData {
	private String resourceId;
	private String resourceName;
	private String keywords;
	private String resourceType;
	private String resourcePath;
	private Date createTime;
	private String resourceDesc;
}
