package com.liang.Rep;

import lombok.Data;

@Data
public class CheckLocalData {
	private String userId;
	private String resourceName;
	private String resourceType;
	private Integer pageNumber;
	private Integer recordNumber;
	private Integer totalRecord; // 总的偏移量
}
