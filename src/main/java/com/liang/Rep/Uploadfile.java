package com.liang.Rep;

import java.util.Date;
import lombok.Data;
@Data
public class Uploadfile {
  private Integer uploadFileId;
  private String name;
  private Integer type;
  private String fileDesc;
  private String size;
  private String path;
  private Date time;
}
