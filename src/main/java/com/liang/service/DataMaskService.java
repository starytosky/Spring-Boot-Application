package com.liang.service;

import com.liang.Rep.CheckTask;
import com.liang.Rep.LiveVideoMask;
import com.liang.Rep.LocalMask;
import com.liang.Rep.MaskTask;

import java.io.IOException;
import java.util.List;

public interface DataMaskService {


    // 判断文件是否存在
    Boolean isFile(String md5Id);

    // 判断传入的参数是否正确
    Boolean checkParameters(String[] modelList,String useMethod);

    // 离线视频数据脱敏
    boolean localVideoMask(MaskTask maskTask,String ruleDesc) throws IOException;

    LocalMask setLocalMask(MaskTask maskTask);


    // 实时视频数据脱敏
    boolean liveVideoMask(MaskTask maskTask,String ruleDesc) throws IOException;

    LiveVideoMask setLiveMask(MaskTask maskTask);

    boolean isRtmpStream(String rtspUrl);

    // 根据userid，和任务类型id获取任务情况
    List<LiveVideoMask> getLiveTaskPosition(CheckTask checkTask);

    List<LocalMask> getLocalTaskPosition(CheckTask checkTask);

    int deleteTask(Integer userId,Integer taskId, Integer typeId);

    int createMaskTask(MaskTask maskTask);

    int updateMaskTask(MaskTask maskTask);

    String getMaskRuleByRuleId(Integer ruleId);

    MaskTask getMaskTaskById(Integer taskId);

    String getMaskMethodByMethodId(Integer metnodId);
//
//    // readVideoFrame
//    boolean readVideoFrame();
}
