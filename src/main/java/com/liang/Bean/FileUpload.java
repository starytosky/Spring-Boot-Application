package com.liang.Bean;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileUpload {

    private String userId;
    /**
     * 文件
     */
    MultipartFile file;
}
