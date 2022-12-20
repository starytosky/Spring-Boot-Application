package com.liang.Controller;

import com.liang.Rep.CheckMaskData;
import com.liang.Rep.CheckMaskTask;
import com.liang.Rep.MaskData;
import com.liang.Res.MaskDataInfo;
import com.liang.common.util.Result;
import com.liang.common.util.ResultCodeEnum;
import com.liang.service.MaskDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/maskData/")
@Slf4j
public class MaskDataController {

	@Autowired
	private MaskDataService maskDataService;

	@GetMapping("checkMaskData")
	public Result checkMaskData(CheckMaskData checkMaskData) {
		checkMaskData.setTotalRecord(checkMaskData.getRecordNumber()* (checkMaskData.getPageNumber() -1));
		List<MaskData> maskDataList = maskDataService.selectMaskData(checkMaskData);
		int maskDataCount = maskDataService.MaskDataCount(checkMaskData);
		if (maskDataList!= null) {
			return Result.build(maskDataList,maskDataCount, ResultCodeEnum.SUCCESS);
		}else return Result.build(500,"查询失败");
	}

	@GetMapping("deleteMaskData")
	public Result deleteTask(@RequestParam String userId, @RequestParam  Integer maskDataId) {
		if(maskDataService.deleteMaskData(userId,maskDataId) == 1) {
			return Result.ok("删除成功");
		}else return Result.build(500,"删除失败");
	}

	@GetMapping("getMaskDataInfo")
	public Result getMaskDataInfo(@RequestParam Integer maskDataId) {
		MaskDataInfo maskDataInfo = maskDataService.getMaskDataInfo(maskDataId);
		if(maskDataInfo != null) {
			return Result.ok(maskDataInfo);
		}else return Result.build(500,"查询失败");
	}

	// 还差一个下载视频文件的接口


}
