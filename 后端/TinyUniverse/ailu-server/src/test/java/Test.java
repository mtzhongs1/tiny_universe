import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.model.chat.ChatResponse;
import com.mysql.cj.x.protobuf.MysqlxCrud;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

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
    public void test1(){

    }
}
