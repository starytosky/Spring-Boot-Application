package com.liang.Res;

import lombok.Data;

import java.util.Date;

@Data
public class ExecRecordInfo {
    private Integer execId;
    private Integer taskId;
    private Integer ruleId;
    private String ruleName;
    private String ruleDesc;
    private String limitContent;
    private String limitForm;
    private String taskName;
    private String dataId;
    private String dataName;
    private String resourceInfo;
    private Integer dataType;
    private String resourceDesc;
    private Date startTime;
    private Date endTime;
    private String methodName;
    private Integer taskStatus;
    private Integer isType;
    private Integer isupload;
    private String method;
    private String logPath;
    private String rulePath;
}
