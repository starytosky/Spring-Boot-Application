package com.liang.Rep;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileUpload {

    private String userId;
    // 上传的文件类型
    private Integer file_type;
    /**
     * 文件
     */
    MultipartFile file;
}
