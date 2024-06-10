import com.ailu.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.mindrot.jbcrypt.BCrypt;

/**
 * @Description:
 * @Author: ailu
 * @Date: 7/6/2024 下午3:58
 */

public class Test {
    @org.junit.jupiter.api.Test
    public void test(){
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MTgzNjcxMzEsInVzZXJJZCI6MX0.fORHwq9OhsvJWoAqKJ8CMw3XLJ8IKyzUrjvKp47o3O4";
        Claims claims = JwtUtil.parseJWT("ailu", token);
        System.out.println(claims.get("userId"));
    }
}
