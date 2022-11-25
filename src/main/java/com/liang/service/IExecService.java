package com.liang.service;

/**
 * @author liang
 * @Package com.liang.service
 * @date 2022/10/25 16:18
 */
public interface IExecService {

    public void localVideoMask(String[] cmdStr);

    public void liveVideoMask(String[] cmdStr,String out_file_path,String filename);

}
