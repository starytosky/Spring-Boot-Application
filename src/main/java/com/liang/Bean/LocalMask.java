package com.liang.Bean;

import lombok.Data;

import java.util.Date;

@Data
public class LocalMask {
    private Integer taskId;
    private Integer userId;
    private String name; // 用户名
    private String taskName;
    private String originPath;
    private String maskPath;
    private String model;
    private Date startTime;
    private Date endTime;
    private Integer taskStatus;
    private Integer isType;
    private Integer isdelete;
    private String useMethod;
    private String[] modelList;
}
