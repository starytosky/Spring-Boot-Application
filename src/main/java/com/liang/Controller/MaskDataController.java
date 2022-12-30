package com.liang.Controller;

import com.liang.Rep.CheckMaskData;
import com.liang.Rep.CheckMaskTask;
import com.liang.Rep.MaskData;
import com.liang.Res.MaskDataInfo;
import com.liang.Res.MaskDataList;
import com.liang.common.util.Result;
import com.liang.common.util.ResultCodeEnum;
import com.liang.service.MaskDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
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
		List<MaskDataList> maskDataList = maskDataService.selectMaskData(checkMaskData);
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

	/**
	 * @param path     指想要下载的文件的路径
	 * @param response
	 * @功能描述 下载文件:将输入流中的数据循环写入到响应输出流中，而不是一次性读取到内存
	 */
	@PostMapping("downloadLocal")
	public void downloadLocal(String path, HttpServletResponse response) throws IOException {
		// 读到流中
		InputStream inputStream = new FileInputStream(path);// 文件的存放路径
		response.reset();
		response.setContentType("application/octet-stream");
		String filename = new File(path).getName();
		response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
		ServletOutputStream outputStream = response.getOutputStream();
		byte[] b = new byte[1024];
		int len;
		//从输入流中读取一定数量的字节，并将其存储在缓冲区字节数组中，读到末尾返回-1
		while ((len = inputStream.read(b)) > 0) {
			outputStream.write(b, 0, len);
		}
		inputStream.close();
	}


}
