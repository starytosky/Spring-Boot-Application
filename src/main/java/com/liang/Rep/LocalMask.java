package com.liang.Rep;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("localmask")
public class LocalMask {
    @TableId(value = "exec_id",type = IdType.AUTO)
    private Integer execId;
    private Integer taskId;
    private String userId;
    private Integer ruleId;
    private Integer methodId;
    private String userName; // 用户名
    private String taskName;
    private String dataName;
    private Integer dataType;
    private String originPath;
    private String maskPath;
    private String model;
    private Date startTime;
    private Date endTime;
    private Integer taskStatus;
    private String logPath;
    private Integer isType;
    private Integer isdelete;
    private String method;
//    private String[] modelList;
}
