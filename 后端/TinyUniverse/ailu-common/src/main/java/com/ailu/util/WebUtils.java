package com.ailu.util;
import com.ailu.result.Result;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WebUtils {
    /**
     * 将字符串渲染到客户端
     *
     * @param response 渲染对象
     * @param string   待渲染的字符串
     * @return null
     */
    public static void renderString(HttpServletResponse response, Result string) {
        try {
            // 设置CORS头信息
            response.setHeader("Access-Control-Allow-Origin", "*"); // 或者指定的域名
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "x-requested-with,Authorization");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            String res = JSONObject.toJSONString(string);
            response.getWriter().print(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void setDownLoadHeader(String filename, ServletContext context, HttpServletResponse response) throws UnsupportedEncodingException {
        String mimeType = context.getMimeType(filename);//获取文件的mime类型
        response.setHeader("content-type", mimeType);
        String fname = URLEncoder.encode(filename, "UTF-8");
        response.setHeader("Content-disposition", "attachment; filename=" + fname);

//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        response.setCharacterEncoding("utf-8");
    }
}
