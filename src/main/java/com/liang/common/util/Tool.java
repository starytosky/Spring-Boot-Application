package com.liang.common.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
}
