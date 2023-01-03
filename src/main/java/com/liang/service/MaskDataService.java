package com.liang.service;

import com.liang.Rep.*;
import com.liang.Res.MaskDataInfo;
import com.liang.Res.MaskDataList;
import com.liang.Res.Resources;

import java.io.IOException;
import java.util.List;

public interface MaskDataService {

	// 根据userid，和任务类型id获取任务情况
//	List<LiveVideoMask> getLiveTaskPosition(CheckMaskTask checkMaskTask);
//
//	List<LocalMask> getLocalTaskPosition(CheckMaskTask checkMaskTask);

	int deleteMaskData(String userId, String maskDataId);

	List<MaskDataList> selectMaskData(CheckMaskData checkMaskData);

	int MaskDataCount(CheckMaskData checkMaskData);

	MaskDataInfo getMaskDataInfo(String maskDataId);

	Resources getResourcesInfo(String resourceId) throws IOException;
}
