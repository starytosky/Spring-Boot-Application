package com.liang.service;

import com.liang.Bean.CheckTask;
import com.liang.Bean.LiveVideoMask;
import com.liang.Bean.LocalMask;
import com.liang.Bean.MaskTask;

import java.io.IOException;
import java.util.List;

public interface DataMaskService {


    // 判断文件是否存在
    Boolean isFile(String md5Id);

    // 判断传入的参数是否正确
    Boolean checkParameters(String[] modelList,String useMethod);

    // 离线视频数据脱敏
    boolean localVideoMask(LocalMask localvideoMask);



    // 实时视频数据脱敏
    boolean liveVideoMask(LiveVideoMask liveVideoMask) throws IOException;

    boolean isRtmpStream(String rtspUrl);

    // 根据userid，和任务类型id获取任务情况
    List<LiveVideoMask> getLiveTaskPosition(CheckTask checkTask);

    List<LocalMask> getLocalTaskPosition(CheckTask checkTask);

    int deleteTask(Integer userId,Integer taskId, Integer typeId);

    int createMaskTask(MaskTask maskTask);

    int updateMaskTask(MaskTask maskTask);
//
//    // readVideoFrame
//    boolean readVideoFrame();
}
