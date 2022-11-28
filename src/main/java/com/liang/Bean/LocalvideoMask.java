package com.liang.Bean;

import lombok.Data;

import java.util.Date;

@Data
public class LocalvideoMask {
    private Integer localTaskId;
    private Integer userId;
    private String videoPath;
    private String maskPath;
    private String model;
    private Date startTime;
    private Date endTime;
    private Integer taskStatus;
    private Integer isdelete;
    private String useMethod;
    private String[] modelList;

}
