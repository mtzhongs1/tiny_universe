package com.ailu.server.service.common;

import java.io.InputStream;

/**
 * @author: ailu
 * @description: TODO
 * @date: 5/6/2024 下午10:23
 */

public interface MinioService {
    String uploadImgFile(String prefix, String filename, InputStream inputStream);


    String uploadHtmlFile(String prefix, String filename,InputStream inputStream);


    void delete(String pathUrl);

    byte[] downLoadFile(String pathUrl);
}
