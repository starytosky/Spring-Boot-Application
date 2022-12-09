package com.liang.Bean;

import lombok.Data;

import java.util.Date;

@Data
public class LiveVideoMask {
    private Integer taskId;
    private Integer userId;
    private String name;
    private String taskName;
    private String streamUrl;
    private String outFilePath;
    private String outFilename;
    private String obsPath;
    private Integer taskStatus;
    private String useMethod;
    private String model;
    private Date startTime;
    private Date endTime;
    private String[] modelList;
    private Integer isType;
    private Integer isdelete;
    private Long times_sec;
}
