package com.liang.service;

import com.liang.Rep.*;
import com.liang.Res.MaskDataInfo;

import java.util.List;

public interface MaskDataService {

	// 根据userid，和任务类型id获取任务情况
//	List<LiveVideoMask> getLiveTaskPosition(CheckMaskTask checkMaskTask);
//
//	List<LocalMask> getLocalTaskPosition(CheckMaskTask checkMaskTask);

	int deleteMaskData(String userId, Integer maskDataId);


	MaskDataInfo getMaskDataInfo(int maskDataId);

	List<MaskData> selectMaskData(CheckMaskData checkMaskData);

	int MaskDataCount(CheckMaskData checkMaskData);
}
