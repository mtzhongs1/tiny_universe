package com.ailu.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Description:
 * @Author: ailu
 * @Date: 9/6/2024 上午12:07
 */

public interface FileService {
    String uploadImgFile(MultipartFile image);
}
