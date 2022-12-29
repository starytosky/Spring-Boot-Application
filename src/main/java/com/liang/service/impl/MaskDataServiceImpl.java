package com.liang.service.impl;

import com.liang.Mapper.*;
import com.liang.Rep.*;
import com.liang.Res.MaskDataInfo;
import com.liang.service.MaskDataService;
import com.liang.service.MaskRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Slf4j
public class MaskDataServiceImpl implements MaskDataService {

	@Autowired
	private MaskDataDao maskDataDao;

	@Autowired
	private MaskRuleService maskRuleService;


//	// 根据userid，和任务类型id获取任务情况
//	@Override
//	public List<LiveVideoMask> getLiveTaskPosition(CheckMaskTask checkMaskTask) {
//		List<LiveVideoMask> liveVideoMaskList =  liveVideoMaskDao.GetUserTaskByUserId(checkMaskTask);
//		return liveVideoMaskList;
//	}
//
//	@Override
//	public List<LocalMask> getLocalTaskPosition(CheckMaskTask checkMaskTask) {
//		List<LocalMask> localvideoMaskList =  localMaskDao.GetUserTaskByUserId(checkMaskTask);
//		return localvideoMaskList;
//	}



	@Override
	public int deleteMaskData(String userId, Integer maskDataId) {
		return maskDataDao.deleteMaskData(userId,maskDataId);
	}

	@Override
	public List<MaskData> selectMaskData(CheckMaskData checkMaskData) {
		return maskDataDao.selectMaskData(checkMaskData);
	}

	@Override
	public int MaskDataCount(CheckMaskData checkMaskData) {
		return maskDataDao.MaskDataCount(checkMaskData);
	}

	@Override
	public MaskDataInfo getMaskDataInfo(Integer maskDataId) {
		MaskDataInfo maskDataInfo = maskDataDao.getMaskDataInfo(maskDataId);
		if(maskDataInfo.getIsupload() == 1) {
			maskDataInfo.setRuleDesc(maskRuleService.getRuleContent(maskDataInfo.getRulePath()));
		}
		return maskDataInfo;
	}


}
