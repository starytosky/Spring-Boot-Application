package com.liang.common.util;

import com.alibaba.druid.util.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;

public class Tool {

	// 读取文件中的数据返回字符串
	public static String getFileContent(String path){
		// 获取文件路径，将文件里的规则读到String里
		BufferedReader reader = null;
		StringBuffer stringBuffer = new StringBuffer();
		try {
			reader = new BufferedReader(new FileReader(path));
			String line = null;
			// 系统对应的换行符
			String ls = System.getProperty("line.separator");
			while ((line = reader.readLine()) != null) {
				stringBuffer.append(line);
//                stringBuffer.append(ls);
			}
			// delete the last ls
			stringBuffer.deleteCharAt(stringBuffer.length() - 1);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return stringBuffer.toString();
	}

	public static void downloadFile(String path, HttpServletResponse response) throws IOException {
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



	public static String getNowIP() throws IOException {
		String ip = null;
		String objWebURL = "https://bajiu.cn/ip/";
		BufferedReader br = null;
		try {
			URL url = new URL(objWebURL);
			br = new BufferedReader(new InputStreamReader(url.openStream()));
			String s = "";
			String webContent = "";
			while ((s = br.readLine()) != null) {
				if (s.indexOf("互联网IP") != -1) {
					ip = s.substring(s.indexOf("'") + 1, s.lastIndexOf("'"));
					break;
				}
			}
		} finally {
			if (br != null)
				br.close();
		}
		if (StringUtils.isEmpty(ip)) {
			throw new RuntimeException();
		}
		return ip;
	}
}
