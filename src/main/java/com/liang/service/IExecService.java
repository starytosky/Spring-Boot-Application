package com.liang.service;

/**
 * @author liang
 * @Package com.liang.service
 * @date 2022/10/25 16:18
 */
public interface IExecService {


    // 判断文件是否存在
    String isFile(String md5Id);

    // 离线视频数据脱敏
    boolean localVideoMask(String md5Id);


}
