package com.liang.service;

import com.liang.Rep.LiveVideoMask;
import com.liang.Rep.LocalMask;

/**
 * @author liang
 * @Package com.liang.service
 * @date 2022/10/25 16:18
 */
public interface IExecService {

    public void localVideoMask(String[] cmdStr, LocalMask localvideoMask);

    public void liveVideoMask(String[] cmdStr, LiveVideoMask liveVideoMask);

}
