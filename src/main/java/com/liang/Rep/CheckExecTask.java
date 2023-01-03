package com.liang.Rep;

import lombok.Data;

@Data
public class CheckExecTask {
	private String userId;
	private String taskId;
	private String execId;
	private Integer isType;
	private Integer taskStatus;
	private Integer pageNumber;
	private Integer recordNumber;
	private Integer totalRecord; // 总的偏移量
}
