package com.liang.service.impl;

import com.liang.common.util.ExecUtil;
import com.liang.service.IExecService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j // 注入日志类，可以使用 Log.info("打印内容"); 来打印内容
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
//        boolean isfile = redisTemplate.hasKey(md5Id);
        String fileDirPath = uploadFolder + md5Id;
        File file = new File(fileDirPath);
        if(file.isDirectory() && file.list()!= null) {
            return uploadFolder + md5Id;
        }else {
            return "";
        }
    }

    @Override
    public boolean localVideoMask(String md5Id) {

        // 根据md5Id 和 uploadFolder 去找到对应的离线视频文件
        String fileDirPath = uploadFolder + md5Id + File.separator;
        String outPath = "/home/java/Project/python/image/img_";
        try {
            String res = ExecUtil.exec(new String [] {"python",codePath,fileDirPath,outPath}, 5);
            if (res.equals("Time out")){
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return true;
    }
}
