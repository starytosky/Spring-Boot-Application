package com.liang.Res;

import lombok.Data;

import java.util.Date;

@Data
public class LocalData {
	private String resourceId;
	private String resourceName;
	private String keywords;
	private String resourceType;
	private String resourceTrainPath;
	private Date create_time;
	private String resourceDesc;
}
