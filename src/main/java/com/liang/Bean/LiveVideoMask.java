package com.liang.Bean;

import lombok.Data;

import java.util.Date;

@Data
public class LiveVideoMask {
    private Integer userId;
    private String streamUrl;
    private String outFilePath;
    private String outFilename;
    private Integer taskStatus;
    private String useMethod;
    private String model;
    private Date startTime;
    private Date endTime;
    private String[] modelList;
    private Integer isdelete;
    private Long times_sec;
}
