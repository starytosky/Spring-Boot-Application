package com.liang.common.util;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liang.Rep.GPUInfo;
import com.liang.Rep.ProcessInfo;
import io.micrometer.core.instrument.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
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

	// 查询系统资源使用情况
	public static float SystemUsage() {
		log.info("开始收集cpu使用率");
		float cpuUsage = 0;
		Process pro1,pro2;
		Runtime r = Runtime.getRuntime();
		try {
			String command = "cat /proc/stat";
			//第一次采集CPU时间
			long startTime = System.currentTimeMillis();
			pro1 = r.exec(command);
			BufferedReader in1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
			String line = null;
			long idleCpuTime1 = 0, totalCpuTime1 = 0;	//分别为系统启动后空闲的CPU时间和总的CPU时间
			while((line=in1.readLine()) != null){
				if(line.startsWith("cpu")){
					line = line.trim();
					log.info(line);
					String[] temp = line.split("\\s+");
					idleCpuTime1 = Long.parseLong(temp[4]);
					for(String s : temp){
						if(!s.equals("cpu")){
							totalCpuTime1 += Long.parseLong(s);
						}
					}
					log.info("IdleCpuTime: " + idleCpuTime1 + ", " + "TotalCpuTime" + totalCpuTime1);
					break;
				}
			}
			in1.close();
			pro1.destroy();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				log.error("CpuUsage休眠时发生InterruptedException. " + e.getMessage());
				log.error(sw.toString());
			}
			//第二次采集CPU时间
			long endTime = System.currentTimeMillis();
			pro2 = r.exec(command);
			BufferedReader in2 = new BufferedReader(new InputStreamReader(pro2.getInputStream()));
			long idleCpuTime2 = 0, totalCpuTime2 = 0;	//分别为系统启动后空闲的CPU时间和总的CPU时间
			while((line=in2.readLine()) != null){
				if(line.startsWith("cpu")){
					line = line.trim();
					log.info(line);
					String[] temp = line.split("\\s+");
					idleCpuTime2 = Long.parseLong(temp[4]);
					for(String s : temp){
						if(!s.equals("cpu")){
							totalCpuTime2 += Long.parseLong(s);
						}
					}
					log.info("IdleCpuTime: " + idleCpuTime2 + ", " + "TotalCpuTime" + totalCpuTime2);
					break;
				}
			}
			if(idleCpuTime1 != 0 && totalCpuTime1 !=0 && idleCpuTime2 != 0 && totalCpuTime2 !=0){
				cpuUsage = 1 - (float)(idleCpuTime2 - idleCpuTime1)/(float)(totalCpuTime2 - totalCpuTime1);
				log.info("本节点CPU使用率为: " + cpuUsage);
			}
			in2.close();
			pro2.destroy();
		} catch (IOException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			log.error("CpuUsage发生InstantiationException. " + e.getMessage());
			log.error(sw.toString());
		}
		return cpuUsage;
	}

	// 获取gpu使用情况 并筛选出使用率最小的卡
	/**
	 * 通过命令xml格式显卡信息
	 *
	 * @return xml字符串
	 * @throws IOException 获取显卡信息错误
	 */
	public static String getGpuXmlInfo() throws IOException {
		Process process;
		String result = "";
		process = Runtime.getRuntime().exec("nvidia-smi -q -x");
		try (InputStream inputStream = process.getInputStream()) {
			result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
		}catch (Exception e) {
			log.error("获取gpu信息失败", e.getMessage(), e);
		}
		if (process.isAlive()) {
			process.destroy();
		}
		return result;
	}

	private static final String REG = "<!DOCTYPE.*.dtd\">";

	/**
	 * 获取gpu信息（暂时只支持nvidia-smi）
	 *
	 * @return gpu信息集合
	 * @throws DocumentException xml解析错误
	 */
	public static List<GPUInfo> convertXmlToGpuObject(String xmlGpu) throws DocumentException {
		//忽略dtd
		xmlGpu = xmlGpu.replaceAll(REG, "");
		Document document = DocumentHelper.parseText(xmlGpu);
		List<Element> gpu = document.getRootElement().elements("gpu");
		List<GPUInfo> gpuInfoList = new ArrayList<>();
		gpu.forEach(element -> {
			GPUInfo gpuInfo = new GPUInfo();
			String uuid = element.element("uuid").getText();
			Element fbMemoryUsage = element.element("fb_memory_usage");
			String total = fbMemoryUsage.element("total").getText();
			String used = fbMemoryUsage.element("used").getText();
			String free = fbMemoryUsage.element("free").getText();
			gpuInfo.setTotalMemory(total);
			gpuInfo.setUsedMemory(used);
			gpuInfo.setFreeMemory(free);
			gpuInfo.setName(uuid);
			Element processes = element.element("processes");
			List<Element> infos = processes.elements("process_info");
			List<ProcessInfo> processInfos = new ArrayList<>();
			infos.forEach(info -> {
				ProcessInfo processInfo = new ProcessInfo();
				String pid = info.element("pid").getText();
				String name = info.element("process_name").getText();
				String usedMemory = info.element("used_memory").getText();
				processInfo.setPid(pid);
				processInfo.setName(name);
				processInfo.setUsedMemory(usedMemory);
				processInfos.add(processInfo);
			});
			gpuInfo.setProcessInfos(processInfos);
			int intTotal = Integer.parseInt(total.split(" ")[0]);
			int intUsed = Integer.parseInt(used.split(" ")[0]);
			gpuInfo.setUsageRate((int) ((float) intUsed / intTotal * 100));
			gpuInfoList.add(gpuInfo);
		});
		return gpuInfoList;
	}

}
