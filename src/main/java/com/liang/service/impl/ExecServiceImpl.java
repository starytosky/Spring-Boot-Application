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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author liang
 * @Package com.liang.service.impl
 * @date 2022/10/25 16:29
 */
@Service
@Slf4j
public class ExecServiceImpl implements IExecService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${uploadFolder}")
    private String uploadFolder;

    @Value("${codePath}")
    private String codePath;

    // 算法允许传入的参数
    private static final String[] checkmodelList = {"person","plate","sign","qrcode","idcard","nake","all"};
    // 算法在什么设备上运行
    private static final String[] checkuseMethod = {"gpu","cpu"};


    @Override
    public String isFile(String md5Id) {
        // 去redis中找该md5Id是否存在 && 访问文件是否存在
//        boolean isfile = redisTemplate.hasKey(md5Id);
        String fileDirPath = uploadFolder + md5Id;
        log.info("源文件存放目录",fileDirPath);
        File file = new File(fileDirPath);
        File[] array = file.listFiles();
        if(file.isDirectory() && array.length == 1) {
            // 获取这个文件名字
            if(array[0].isFile()) {
                log.info("源视频文件名称" + array[0].getName());
                return fileDirPath + File.separator + array[0].getName();
            }else {
                return "";
            }
        }else {
            return "";
        }
    }

    @Override
    public Boolean checkParameters(String[] modelList, String useMethod) {
        HashSet<String> hset= new HashSet<>();
        // hset stores all the values of checkmodelList
        for(int i = 0; i < checkmodelList.length; i++)
        {
            if(!hset.contains(checkmodelList[i]))
                hset.add(checkmodelList[i]);
        }
        for(int i = 0; i < modelList.length; i++)
        {
            if(!hset.contains(modelList[i]))
                return false;
        }
        if (useMethod.equals("cpu") || useMethod.equals("gpu")) {
            return true;
        }else {
            return false;
        }
    }


    @Override
    public boolean localVideoMask(String videoPath,String[] modelList,String useMethod) {

        // 根据md5Id 和 uploadFolder 去找到对应的离线视频文件
        String fileDirPath = videoPath;
        File file = new File(fileDirPath);
        String outPath = "/home/ysjs3/java/output/" + file.getName();
        log.info("原视频存放地址" + fileDirPath);
        log.info("视频保存地址" + outPath);
        log.info("代码地址" + codePath );
        // String usecmd = "python /home/ysjs3/java/code/test.py -i /home/ysjs3/java/upfile/0198c25790ad81c091d8d0e5c850a0ed/person3.mp4 -o /home/ysjs3/java/output/person3.mp4 --model_list person --device cpu";
        String[] std = new String[] {"python",codePath,"-i",fileDirPath,"-o",outPath,"--model_list"};
        // 计算出命令行需要的参数量
        int cmd_len = modelList.length + std.length + 2;
        String[] cmdStr = new String[cmd_len];
        for (int i = 0; i < std.length; i++) {
            cmdStr[i] = std[i];
        }
        for (int i = std.length; i < (std.length + modelList.length); i++) {
            cmdStr[i] = modelList[i - std.length];
        }
        cmdStr[cmd_len -2] = "--device";
        cmdStr[cmd_len -1] = useMethod;
        try {
            String res = ExecUtil.exec(cmdStr, 200);
//            String res = ExecUtil.exec(str,200);
            log.info("脚本执行结果" + res);
            if (res.equals("false") || res.equals("Time out")){
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
