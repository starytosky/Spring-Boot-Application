package com.liang.Rep;

import lombok.Data;

@Data
public class CheckMaskTask {
    private String userId;
    private Integer typeId;
    private String taskName;
    private Integer taskStatus;
    private Integer pageNumber;
    private Integer recordNumber;
    private Integer totalRecord; // 总的偏移量
}
