package com.liang.Rep;

import lombok.Data;

@Data
public class CheckMaskData {
	private String userId;
	private Integer typeId;
	private String taskName;
	private Integer dataType;
	private Integer pageNumber;
	private Integer recordNumber;
	private Integer totalRecord; // 总的偏移量
}
