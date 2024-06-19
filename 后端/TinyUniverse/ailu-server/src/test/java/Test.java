import com.ailu.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Arrays;

/**
 * @Description:
 * @Author: ailu
 * @Date: 7/6/2024 下午3:58
 */

public class Test{

    interface Child{
        // private String name;
        default void printName(){
            System.out.println("我被打印了");
        }
    }

    class ChildImpl implements Child{
        // @Override
        // public void printName() {
        //     System.out.println("我被打印了");
        // }
    }

    @org.junit.jupiter.api.Test
    public void test(){

        float x = 0.2f;
        float y = 2.0f - 1.8f;
        System.out.println(y);
        System.out.println(x);

    }
}
