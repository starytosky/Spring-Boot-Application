package com.liang.service;

import com.liang.Bean.LiveVideoMask;
import com.liang.Bean.LocalvideoMask;

/**
 * @author liang
 * @Package com.liang.service
 * @date 2022/10/25 16:18
 */
public interface IExecService {

    public void localVideoMask(String[] cmdStr, LocalvideoMask localvideoMask);

    public void liveVideoMask(String[] cmdStr, LiveVideoMask liveVideoMask);

}
