package com.liang.Rep;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("livevideomask")
public class LiveVideoMask {
    @TableId(value = "exec_id",type = IdType.AUTO)
    private Integer execId;
    private Integer taskId;
    private String userId;
    private Integer ruleId;
    private Integer methodId;
    private String userName;
    private String taskName;
    private String streamUrl;
    private String streamMaskName;
    private String outFilePath;
    private String outFilename;
    private String dataName;
    private Integer dataType;
    private String obsPath;
    private Integer taskStatus;
    private String method;
    private String model;
    private Date startTime;
    private Date endTime;
    private String logPath;
    private Integer isType;
    private Integer isdelete;
}
