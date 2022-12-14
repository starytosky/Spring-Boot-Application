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
    private Integer userId;
    private Integer ruleId;
    private String name;
    private String taskName;
    private String streamUrl;
    private String outFilePath;
    private String outFilename;
    private String dataName;
    private String obsPath;
    private Integer taskStatus;
    private String method;
    private String model;
    private Date startTime;
    private Date endTime;
    private Integer isType;
    private Integer isdelete;
    private Long times_sec;
}
