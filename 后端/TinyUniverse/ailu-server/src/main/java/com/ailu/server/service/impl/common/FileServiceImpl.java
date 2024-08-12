package com.ailu.server.service.impl.common;

import com.ailu.exception.BaseException;
import com.ailu.server.service.common.FileService;
import com.ailu.server.service.common.MinioService;
import com.ailu.util.UuidUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Description:
 * @Author: ailu
 * @Date: 9/6/2024 上午12:08
 */

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Autowired
    private MinioService minioService;
    @Override
    public String uploadImgFile(MultipartFile image) {
        if (image.isEmpty()) {
            throw new BaseException("没有传来图片");
        }
        if (image.getSize() > 4 * 1024 * 1024) {
            throw new BaseException("文件不能大于4M");
        }

        String fileName = getFileName(image,".png");
        String url = null;
        try {
            //上传图片到minio
            url = minioService.uploadImgFile("",fileName,image.getInputStream());
        }catch(IOException e){
            log.error("上传图片失败");
            throw new BaseException("上传图片失败，服务器繁忙");
        }
        return url;
    }



    @Override
    public String uploadHtmlFile(MultipartFile html) {
        if (html.isEmpty()) {
            throw new BaseException("没有传来html");
        }
        String fileName = getFileName(html,".html");
        String url = null;
        try {
            url = minioService.uploadHtmlFile("",fileName,html.getInputStream());
        } catch (IOException e) {
            log.error("上传图片失败");
            throw new BaseException("上传文章内容失败，服务器繁忙");
        }
        return url;
    }

    @NotNull
    private String getFileName(MultipartFile file,String suffix) {
        String fileName = UuidUtils.getUUID();
        String originalFilename = file.getOriginalFilename();
        log.info("文件名: {}", originalFilename);
        log.info("文件大小: {}", file.getSize());
        int index = originalFilename.lastIndexOf(".");
        // String suffix = ".png";
        if(index >= 0){
            suffix = originalFilename.substring(index);
        }
        fileName = fileName + suffix;
        return fileName;
    }
}
