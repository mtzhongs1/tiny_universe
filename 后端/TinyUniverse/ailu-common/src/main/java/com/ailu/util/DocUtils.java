package com.ailu.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/5 下午10:13
 */

public class DocUtils {
    public static String getText(String html){
        Document doc = Jsoup.parse(html);
        return doc.text();
    }
}
