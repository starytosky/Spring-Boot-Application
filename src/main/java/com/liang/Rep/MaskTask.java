package com.liang.Rep;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

@Data
@TableName("masktask")
public class MaskTask {
  @TableId(value = "task_id",type = IdType.AUTO)
  private String taskId;
  private String userId;
  private String ruleId;
  private Integer methodId;
  private String taskName;
  private String userName;
  private Integer taskStatus;
  private Integer isType;
  private String taskDesc;
  private String dataId;
  private String dataPath;
  private String dataType;
  private String streamMaskName; // 动态脱敏后保存的文件名
  private String streamUrl;
  private String method;
  private Date time;
  private Integer isdelete;
}
