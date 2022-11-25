package com.liang.Bean;

import lombok.Data;

@Data
public class LiveVideoMask {
    private String stream_url;
    private Long times_sec;
    private String out_file_path;
    private String filename;
    private String useMethod;
    private String[] modelList;
}
