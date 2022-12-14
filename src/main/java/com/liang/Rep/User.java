package com.liang.Rep;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "User")
@TableName("User")
public class User {
    @ApiModelProperty(value = "用户id")
    @TableId
    private Integer userId;
    private String name;
    private String phone;
    private String company;
}
