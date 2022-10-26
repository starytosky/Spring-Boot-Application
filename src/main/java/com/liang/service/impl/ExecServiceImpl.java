package com.liang.service.impl;

import com.liang.common.util.ExecUtil;
import com.liang.service.IExecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * @author liang
 * @Package com.liang.service.impl
 * @date 2022/10/25 16:29
 */
@Service
public class ExecServiceImpl implements IExecService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${uploadFolder}")
    private String uploadFolder;

    @Value("${codePath}")
    private String codePath;


    @Override
    public String isFile(String md5Id) {
        // 去redis中找该md5Id是否存在 && 访问文件是否存在
        boolean isfile = redisTemplate.hasKey(md5Id);
        String fileDirPath = uploadFolder + md5Id;
        File file = new File(fileDirPath);
        if(file.isDirectory() && file.list()!= null && isfile) {
            return uploadFolder + md5Id;
        }else {
            return "";
        }
    }

    @Override
    public boolean localVideoMask(String md5Id) {

        // 根据md5Id 和 uploadFolder 去找到对应的离线视频文件
        String fileDirPath = uploadFolder + md5Id;
        try {
            String res = ExecUtil.exec(new String [] {"python","www.baidu.com"}, 5);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }
}
