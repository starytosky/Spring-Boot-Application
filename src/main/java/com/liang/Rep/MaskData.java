package com.liang.Rep;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
@Data
@TableName("maskdata")
public class MaskData {
  @TableId(value = "mask_data_id",type = IdType.AUTO)
  private String maskDataId;
  private String userId;
  private String taskId;
  private String execId;
  private String ruleId;
  private Integer methodId;
  private String maskPath;
  private Integer dataType;
  private Integer isType;
  private String dataName;
  private Date time;
  private Integer isDelete;
  private String method;
}
