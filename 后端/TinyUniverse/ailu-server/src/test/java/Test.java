import com.ailu.result.Result;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.model.chat.ChatResponse;
import com.mysql.cj.x.protobuf.MysqlxCrud;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.sql.Connection;

/**
 * @Description:
 * @Author: ailu
 * @Date: 7/6/2024 下午3:58
 */

@Slf4j
public class Test{


    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/tiny_universe?characterEncoding=utf8&useSSL=false";
    static final String USER = "root";
    static final String PASS = "123456";
    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();
    @org.junit.jupiter.api.Test
    public void test(){
        //TODO:在linux服务器要重新修改路径
        String path = "'D:/mysql/user.csv '";
        Path filePath = Paths.get("D:/mysql/user.csv");
        String SQL = String.format("SELECT * FROM article " +
                " INTO OUTFILE%sFIELDS TERMINATED BY ',' LINES TERMINATED BY '\\r\\n' ", path);
        try (Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
             PreparedStatement ps = conn.prepareStatement(SQL);) {
            //装载驱动
            Class.forName(JDBC_DRIVER);
            //删除原有文件
            File file = new File(String.valueOf(filePath));
            if(file.exists()){
                file.delete();
            }
            ps.executeQuery();
            //关闭连接
        }catch(Exception e) {
            log.error(e.getMessage());
        }
    }

    @org.junit.jupiter.api.Test
    public void test1() throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"messages\":[{\"role\":\"user\",\"content\":\"分析「今天是个晴朗的日子，阳光洒满了整个城市。我走在街上，突然发现地上有一只小猫咪，它颤巍巍地躲在角落里。我慢慢地靠近它，轻声呼唤。渐渐地，小猫咪走近了我，它的眼神里透着害怕又渴望的神采。我伸出手，小猫咪轻轻地舔了一下，然后躺在我的怀里。我感到无比的温暖和幸福，因为我知道，我刚刚收获了一份无条件的爱和快乐。」中「我」的心情，用4个字表达\"}],\"temperature\":0.95,\"top_p\":0.7,\"penalty_score\":1,\"system\":\"你是一名概要设计师，专门负责为文章生成一篇不超过50字的总结\"}");
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/ernie-tiny-8k?access_token=24.0b96779fc75de09d52ff28681fd7606f.2592000.1727779299.282335-92565648")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        System.out.println(response.body().string());}
}
