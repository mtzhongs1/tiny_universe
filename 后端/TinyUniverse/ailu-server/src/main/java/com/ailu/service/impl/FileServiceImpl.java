package com.ailu.service.impl;

import com.ailu.exception.BaseException;
import com.ailu.service.FileService;
import com.ailu.service.MinioService;
import com.ailu.util.UuidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

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

        String fileName = UuidUtils.getUUID();
        String originalFilename = image.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        fileName = fileName + suffix;
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
}
