package com.liang.service;

import com.liang.Bean.LiveVideoMask;
import com.liang.Bean.LocalvideoMask;

public interface DataMaskService {


    // 判断文件是否存在
    Boolean isFile(String md5Id);

    // 判断传入的参数是否正确
    Boolean checkParameters(String[] modelList,String useMethod);

    // 离线视频数据脱敏
    boolean localVideoMask(LocalvideoMask localvideoMask);



    // 实时视频数据脱敏
    boolean liveVideoMask(LiveVideoMask liveVideoMask);

    boolean isRtmpStream(String rtspUrl);
//
//    // readVideoFrame
//    boolean readVideoFrame();
}
