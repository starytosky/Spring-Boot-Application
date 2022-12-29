package com.liang.service;

import com.liang.Rep.*;
import com.liang.Res.ExecRecordInfo;
import com.liang.Res.LocalData;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

public interface MaskTaskService {


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


    int createMaskTask(MaskTask maskTask);

    int updateMaskTask(MaskTask maskTask);

    String getMaskRuleByRuleId(Integer ruleId);

    MaskTask getMaskTaskById(Integer taskId);

    String getMaskMethodByMethodId(Integer metnodId);

    List<LocalData> getLocalDataList(CheckLocalData checkLocalData);

    int getLocalDataCount(CheckLocalData checkLocalData);

    // 获取任务列表
    List<MaskTask> getTaskRecord(CheckMaskTask checkMaskTask);
    // 分页查询获取记录条数
    int getTaskRecordCount(CheckMaskTask checkMaskTask);

    // 获取静态执行记录列表
    List<LocalMask> getLocalExecRecordList(String userId, Integer taskId);
    // 获取动态执行记录
    List<LiveVideoMask> getLiveExecRecordList(String userId, Integer taskId);

    ExecRecordInfo getExecRecordInfo(String userId, Integer execId,Integer isType);
//
//    // readVideoFrame
//    boolean readVideoFrame();
}