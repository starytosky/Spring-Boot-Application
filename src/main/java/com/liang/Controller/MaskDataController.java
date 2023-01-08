package com.liang.Controller;

import com.liang.Rep.CheckMaskData;
import com.liang.Rep.CheckMaskTask;
import com.liang.Rep.MaskData;
import com.liang.Res.MaskDataInfo;
import com.liang.Res.MaskDataList;
//import com.liang.Res.Resources;
import com.liang.common.util.Result;
import com.liang.common.util.ResultCodeEnum;
import com.liang.common.util.Tool;
import com.liang.service.MaskDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/maskService/maskData/")
@Slf4j
@CrossOrigin
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
	public Result deleteTask(@RequestParam String userId, @RequestParam  String maskDataId) {
		if(maskDataService.deleteMaskData(userId,maskDataId) == 1) {
			return Result.ok("删除成功");
		}else return Result.build(500,"删除失败");
	}

	@GetMapping("getMaskDataInfo")
	public Result getMaskDataInfo(@RequestParam String maskDataId) {
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
	@GetMapping("downloadLocal")
	public void downloadLocal(String path, HttpServletResponse response) throws IOException {
		log.info(path);
		File file = new File(path);
		String filename = file.getName();
		byte[] buffer = new byte[1024];
		BufferedInputStream bis = null;
		OutputStream os = null;
		try {
			//文件是否存在
			if (file.exists()) {
				//设置响应
				response.setContentType("application/octet-stream;charset=UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
				os = response.getOutputStream();
				bis = new BufferedInputStream(new FileInputStream(file));
				while(bis.read(buffer) != -1){
					os.write(buffer);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
				if (os != null) {
					os.flush();
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
//		if(path!=null) {
//			Tool.downloadFile(path,response);
//		}
	}

//	// 获取原始静态数据详情getResourcesInfo
//	@GetMapping("getResourceInfo")
//	public Result getResourceInfo(@RequestParam String resourceId) throws IOException {
//		Resources resources = maskDataService.getResourcesInfo(resourceId);
//		if(resources != null) {
//			return Result.ok(resources);
//		}else return Result.build(500,"查询失败");
//	}

}
