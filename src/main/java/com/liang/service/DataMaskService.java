package com.liang.service;

public interface DataMaskService {


    // 判断文件是否存在
    String isFile(String md5Id);

    // 判断传入的参数是否正确
    Boolean checkParameters(String[] modelList,String useMethod);

    // 离线视频数据脱敏
    boolean localVideoMask(String md5Id,String[] modelList,String useMethod);



    // 实时视频数据脱敏
    boolean liveVideoMask(String rtspUrl, Long times_sec, String out_file_path,String filename,String[] modelList,String useMethod);

    boolean isRtmpStream(String rtspUrl);
//
//    // readVideoFrame
//    boolean readVideoFrame();
}
