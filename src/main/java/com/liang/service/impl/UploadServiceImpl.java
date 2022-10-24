package com.liang.service.impl;


import com.liang.Bean.FileChunkDTO;
import com.liang.Bean.FileChunkResultDTO;
import com.liang.service.IUploadService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;


/**
 * @ProjectName UploadServiceImpl
 * @author Administrator
 * @version 1.0.0
 * @Description 附件分片上传
 * @createTime 2022/4/13 0013 15:59
 */
@Service
@SuppressWarnings("all")
public class UploadServiceImpl implements IUploadService {

    private Logger logger = LoggerFactory.getLogger(UploadServiceImpl.class);

    // 上传文件的白名单
    private static final String[] suffixWhiteList = {
            "bmp", "gif", "jpg", "jpeg", "png",
            // 常见视频格式
            "mp4", "m3u8", "rmvb", "avi", "swf", "3gp", "mkv", "flv",
            // word excel powerpoint
            "doc", "docx", "xls", "xlsx", "ppt","pptx", "html", "htm", "txt",
            // 压缩文件
            "rar", "zip", "gz", "bz2",
            // pdf
            "pdf"};

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${uploadFolder}")
    private String uploadFolder;


    @Override
    public boolean suffixCheck(String fileName) {
        if(fileName == null || "".equals(fileName)){
            return false;
        }
        //从最后一个点之后截取字符串
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        System.out.println(suffix);
        //白名单匹配
        boolean anyMatch = Arrays.stream(suffixWhiteList).anyMatch(x -> x.equalsIgnoreCase(suffix));
        System.out.println(anyMatch);
        return anyMatch;
    }
    /**
     * 检查文件是否存在，如果存在则跳过该文件的上传，如果不存在，返回需要上传的分片集合
     *
     * @param chunkDTO
     * @return
     */
    @Override
    public FileChunkResultDTO checkChunkExist(FileChunkDTO chunkDTO) {
        //1.检查文件是否已上传过
        //1.1)检查在磁盘中是否存在
        // 拼出分片存放的目录
        String fileFolderPath = getFileFolderPath(chunkDTO.getIdentifier());
        System.out.println(fileFolderPath);
        logger.info("fileFolderPath-->{}", fileFolderPath);
        // 整个文件的目录
        String filePath = getFilePath(chunkDTO.getIdentifier(), chunkDTO.getFilename());
        // 如果整个文件都存在
        File file = new File(filePath);
        boolean exists = file.exists();
        // 获取文件大小，然后和前端传过来的文件大小进行比对
        long filesize = file.length();
        // 如果文件已经存在，并且文件大小和前端传来的相同就将skipUpload设为true完成秒传，否则就在进行分块上传操作
        if(exists && filesize == chunkDTO.getTotalSize()) {
            return new FileChunkResultDTO(true);
        }else {
            //1.2)检查Redis中是否存在,并且所有分片已经上传完成。
            Set<Integer> uploaded = (Set<Integer>) redisTemplate.opsForHash().get(chunkDTO.getIdentifier(), "uploaded");
            // 如果没有没有完整上传，判断现在这个分片的文件夹存不存在，不存在就创建
            File fileFolder = new File(fileFolderPath);
            if (!fileFolder.exists()) {
                boolean mkdirs = fileFolder.mkdirs();
                logger.info("准备工作,创建文件夹,fileFolderPath:{},mkdirs:{}", fileFolderPath, mkdirs);
            }
            // 断点续传，返回已上传的分片
            return new FileChunkResultDTO(false, uploaded);
        }

//        // 满足这三个条件证明文件已经完整上传成功，将skipUpload设为true
//        if (uploaded != null && uploaded.size() == chunkDTO.getTotalChunks() && exists) {
//
//        }

    }


    /**
     * 上传分片
     *
     * @param chunkDTO
     */
    @Override
    public void uploadChunk(FileChunkDTO chunkDTO) {
        //分块的目录
        System.out.println("上传分片");
        String chunkFileFolderPath = getChunkFileFolderPath(chunkDTO.getIdentifier());
        logger.info("分块的目录 -> {}", chunkFileFolderPath);
        File chunkFileFolder = new File(chunkFileFolderPath);
        if (!chunkFileFolder.exists()) {
            boolean mkdirs = chunkFileFolder.mkdirs();
            logger.info("创建分片文件夹:{}", mkdirs);
        }
        //写入分片
        try (
                InputStream inputStream = chunkDTO.getFile().getInputStream();
                FileOutputStream outputStream = new FileOutputStream(new File(chunkFileFolderPath + chunkDTO.getChunkNumber()))
        ) {
            IOUtils.copy(inputStream, outputStream);
            logger.info("文件标识:{},chunkNumber:{}", chunkDTO.getIdentifier(), chunkDTO.getChunkNumber());
            //将该分片写入redis
            long size = saveToRedis(chunkDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean mergeChunk(String identifier, String fileName, Integer totalChunks) throws IOException {
        return mergeChunks(identifier, fileName, totalChunks);
    }

    /**
     * 合并分片
     *
     * @param identifier
     * @param filename
     */
    private Boolean mergeChunks(String identifier, String filename, Integer totalChunks) {
        // 获取分片文件夹路径，这个文件夹下包含的是这个文件的所有分片
        String chunkFileFolderPath = getChunkFileFolderPath(identifier);
        // 完整文件的位置，这边根据后面的业务去确定文件路径，如果没有该路径，就新创建一个文件
//        String filePath = getFilePath(identifier, filename);
        // 检查分片是否都存在
        if (checkChunks(chunkFileFolderPath, totalChunks)) {
            File chunkFileFolder = new File(chunkFileFolderPath);
            File mergeFile = new File(uploadFolder+identifier,filename);
            // 判断路径是否存在，不存在则新创建一个
            if (!mergeFile.getParentFile().exists()) {
                mergeFile.getParentFile().mkdirs();
            }
            File[] chunks = chunkFileFolder.listFiles();
            //对分片进行排序，保证拼接完成的文件的完整性
            List fileList = Arrays.asList(chunks);
            Collections.sort(fileList, (Comparator<File>) (o1, o2) -> {
                return Integer.parseInt(o1.getName()) - (Integer.parseInt(o2.getName()));
            });
            try {
                // 创建RandomAccessFile实例，该实例可以读写。在这里将该实例与合并文件绑定了
                RandomAccessFile randomAccessFileWriter = new RandomAccessFile(mergeFile, "rw");
                byte[] bytes = new byte[1024];
                for (File chunk : chunks) {
                    // 这个实例用来读分片数据
                    RandomAccessFile randomAccessFileReader = new RandomAccessFile(chunk, "r");
                    int len;
                    while ((len = randomAccessFileReader.read(bytes)) != -1) {
                        randomAccessFileWriter.write(bytes, 0, len);
                    }
                    randomAccessFileReader.close();
                }
                randomAccessFileWriter.close();
                // 合并完成后将分片删除
            } catch (Exception e) {
                return false;
            }
            File ChunkFile = new File(getChunkFileFolderPath(identifier));
            deleteAll(ChunkFile);
            redisTemplate.delete(identifier);
            return true;
        }
        return false;
    }

    /**
     * 检查分片是否都存在
     * @param chunkFileFolderPath
     * @param totalChunks
     * @return
     */
    private boolean checkChunks(String chunkFileFolderPath, Integer totalChunks) {
        try {
            // 判断所有的分片是否都存在
            for (int i = 1; i <= totalChunks + 1; i++) {
                File file = new File(chunkFileFolderPath + File.separator + i);
                if (file.exists()) {
                    continue;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 分片写入Redis
     *
     * @param chunkDTO
     */
    private synchronized long saveToRedis(FileChunkDTO chunkDTO) {
        Set<Integer> uploaded = (Set<Integer>) redisTemplate.opsForHash().get(chunkDTO.getIdentifier(), "uploaded");
        if (uploaded == null) {
            uploaded = new HashSet<>(Arrays.asList(chunkDTO.getChunkNumber()));
            HashMap<String, Object> objectObjectHashMap = new HashMap<>();
            objectObjectHashMap.put("uploaded", uploaded);
            objectObjectHashMap.put("totalChunks", chunkDTO.getTotalChunks());
            objectObjectHashMap.put("totalSize", chunkDTO.getTotalSize());
//            objectObjectHashMap.put("path", getFileRelativelyPath(chunkDTO.getIdentifier(), chunkDTO.getFilename()));
            objectObjectHashMap.put("path", chunkDTO.getFilename());
            // 这边将hashmap对象加入到redis中，根据md5计算出来的唯一值
            redisTemplate.opsForHash().putAll(chunkDTO.getIdentifier(), objectObjectHashMap);
        } else {
            uploaded.add(chunkDTO.getChunkNumber());
            redisTemplate.opsForHash().put(chunkDTO.getIdentifier(), "uploaded", uploaded);
        }
        return uploaded.size();
    }

    /**
     * 得到文件的绝对路径
     *
     * @param identifier
     * @param filename
     * @return
     */
    private String getFilePath(String identifier, String filename) {
        // 这写的啥呀，做了处理也不用
        String ext = filename.substring(filename.lastIndexOf("."));
//        return getFileFolderPath(identifier) + identifier + ext;
        return uploadFolder + identifier + File.separator +  filename;
    }

    /**
     * 得到文件的相对路径
     *
     * @param identifier
     * @param filename
     * @return
     */
    private String getFileRelativelyPath(String identifier, String filename) {
        String ext = filename.substring(filename.lastIndexOf("."));
        return "/" + identifier.substring(0, 1) + "/" +
                identifier.substring(1, 2) + "/" +
                identifier + "/" + identifier
                + ext;
    }


    /**
     * 得到分块文件所属的目录
     *
     * @param identifier
     * @return
     */
    private String getChunkFileFolderPath(String identifier) {
        return getFileFolderPath(identifier) + "chunks" + File.separator;
    }

    /**
     * 得到文件所属的目录
     *
     * @param identifier
     * @return
     */
    private String getFileFolderPath(String identifier) {
        // 这里就是为了拼接出路径  因为window和linux的路径分隔符不同因此使用 File.separator 表示的是路径分隔符
        return uploadFolder + identifier + File.separator;
//        return uploadFolder;
    }

    // 删除分片目录及目录下的分片文件
    public static void deleteAll(File file) {

        if (file.isFile() || file.list().length == 0) {
            file.delete();
        } else {
            for (File f : file.listFiles()) {
                deleteAll(f); // 递归删除每一个文件

            }
            file.delete(); // 删除文件夹
        }
    }
}
