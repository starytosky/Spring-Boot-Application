package com.liang.Rep;

import lombok.Data;

@Data
public class CheckRule {
    private Integer userId;
    private String ruleName;
    private Integer dataType;
    private Integer pageNumber;
    private Integer recordNumber;
    private Integer totalRecord; // 总的偏移量
}
