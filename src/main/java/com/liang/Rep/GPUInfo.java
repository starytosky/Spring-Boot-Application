package com.liang.Rep;

import java.util.List;


public class GPUInfo {
	//名称
	private String name;
	//总内存
	private String totalMemory;
	//已用内存
	private String usedMemory;
	//空闲内存
	private String freeMemory;

	/**
	 * 使用率 整形，最大为100
	 */
	private int usageRate;
	//进程信息
	private List<ProcessInfo> processInfos;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTotalMemory() {
		return totalMemory;
	}

	public void setTotalMemory(String totalMemory) {
		this.totalMemory = totalMemory;
	}

	public String getUsedMemory() {
		return usedMemory;
	}

	public void setUsedMemory(String usedMemory) {
		this.usedMemory = usedMemory;
	}

	public String getFreeMemory() {
		return freeMemory;
	}

	public void setFreeMemory(String freeMemory) {
		this.freeMemory = freeMemory;
	}

	public int getUsageRate() {
		return usageRate;
	}

	public void setUsageRate(int usageRate) {
		this.usageRate = usageRate;
	}

	public List<ProcessInfo> getProcessInfos() {
		return processInfos;
	}

	public void setProcessInfos(List<ProcessInfo> processInfos) {
		this.processInfos = processInfos;
	}
}
