package com.liang.Rep;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("maskrule")
public class Maskrule {
  @TableId(value = "rule_id",type = IdType.AUTO)
  private String ruleId;
  private String userId;
  private String ruleName;
  private String userName;
  private Integer dataType;
  private Integer isupload; // 是否是上传的
  private String rulePath;  // 上传的自定义规则保存路径
  private String ruleDesc;
  private String limitContent;
  private String limitForm;
  private String ruleType;
  private String ruleResource;
  private Date time;
  private Integer isdelete;
}
