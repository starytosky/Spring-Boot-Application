package com.liang.Controller;

import com.liang.Rep.FileChunkDTO;
import com.liang.Rep.FileChunkResultDTO;
import com.liang.Rep.FileUpload;
import com.liang.common.util.Result;
import com.liang.service.IUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;

/**
 * @ProjectName UploaderController
 * @author Administrator
 * @version 1.0.0
 * @Description 附件分片上传
 * @createTime 2022/4/13 0013 15:58
 */
@RestController
@CrossOrigin
@RequestMapping("/upload")
public class UploaderController {

    @Autowired
    private IUploadService uploadService;

    @Value("${uploadFolder}")
    private String uploadFolder;



    @PostMapping("/upload")
    public Result uploadFile(FileUpload fileUpload) throws IOException {
        String filePath = uploadService.uploadSmallFile(fileUpload);
        if (!filePath.equals("")) {
            // 将上传数据传入数据库
            return Result.ok(filePath);
        }else {
            return Result.build(500,"上传失败");
        }
    }

    /**
     * 检查分片是否存在
     *
     * @return
     */
    @GetMapping("chunk")
    public Result<Object> checkChunkExist(FileChunkDTO chunkDTO) {
        FileChunkResultDTO fileChunkCheckDTO;
        try {
//            // 判断该分片文件类型是否符合条件，符合条件再去检查分片是否存在
            if (uploadService.suffixCheck(chunkDTO.getFilename())) {
                fileChunkCheckDTO = uploadService.checkChunkExist(chunkDTO);
                return Result.ok(fileChunkCheckDTO);
            }else {
                return Result.build(500,"上传文件不符合要求");
            }
        } catch (Exception e) {
            return Result.build(500,e.getMessage());
        }
    }


    /**
     * 上传文件分片
     *
     * @param chunkDTO
     * @return
     */
    @PostMapping("chunk")
    public Result<Object> uploadChunk(FileChunkDTO chunkDTO) {
        try {
            // 每次都去检查一下文件类型
            if (uploadService.suffixCheck(chunkDTO.getFilename())) {
                uploadService.uploadChunk(chunkDTO);
                return Result.ok(chunkDTO.getIdentifier());
            }else {
                return Result.build(500,"上传文件不符合要求");
            }
        } catch (Exception e) {
            return Result.build(500,e.getMessage());
        }
    }

    /**
     * 请求合并文件分片
     *
     * @param chunkDTO
     * @return
     */
    @PostMapping("merge")
    public Result<Object> mergeChunks(@RequestBody FileChunkDTO chunkDTO) {
        try {
            Boolean success = uploadService.mergeChunk(chunkDTO.getIdentifier(), chunkDTO.getFilename(), chunkDTO.getTotalChunks());
            if (success) {
                String mergePath = uploadFolder +  chunkDTO.getIdentifier() + File.separator +  chunkDTO.getFilename();
                return Result.ok(mergePath);
            }else {
                // 分片上传失败好像就自动不会生成对应的文件，因此不用在做删除操作
                return Result.build(500,"文件合并失败");
            }
        } catch (Exception e) {
            return Result.build(500,e.getMessage());
        }
    }

}
