package com.liang.common.util;

import com.alibaba.druid.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

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
