package com.ailu.controller.common;

import com.ailu.result.Result;
import com.ailu.service.common.FileService;
import com.ailu.vo.article.ImageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/file")
@RestController
@Api(tags = "文件系统")
public class FileController {

    @Autowired
    private FileService fileService;

    @ApiOperation("上传图片")
    @PostMapping("/uploadImg")
    public Result uploadImgFile(@RequestParam("image")MultipartFile image) {

        String url = fileService.uploadImgFile(image);
        return Result.success(url);
    }

    @ApiOperation("文章图片")
    @PostMapping("/articleImg")
    public Result uploadArticleImgFile(@RequestParam("image")MultipartFile image) {
        String url = fileService.uploadImgFile(image);
        ImageVO imageVO = new ImageVO(url);
        return Result.successArticle(imageVO);
    }
    @ApiOperation("上传html")
    @PostMapping("/uploadFile")
    public Result uploadHtmlFile(@RequestParam("html")MultipartFile html) {
        String url = fileService.uploadHtmlFile(html);
        return Result.success(url);
    }

    // @ApiOperation("删除文件")
    // @DeleteMapping("/delete")
    // public void delete(String pathUrl) {
    //     minioService.delete(pathUrl);
    // }
    // @ApiOperation("下载文件")
    // @PostMapping("/downLoadFile")
    // public byte[] downLoadFile(String pathUrl)  {
    //     return minioService.downLoadFile(pathUrl);
    // }
}
