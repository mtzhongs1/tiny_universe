import com.ailu.util.JwtUtil;
import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import io.jsonwebtoken.Claims;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

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
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        Long number = 2L;
        byte[] serialize = jackson2JsonRedisSerializer.serialize(number);
        Object deserialize = jackson2JsonRedisSerializer.deserialize(serialize);
        System.out.println(deserialize.getClass());
        System.out.println(deserialize);
        System.out.println(number.getClass());

        GenericFastJsonRedisSerializer genericFastJsonRedisSerializer = new GenericFastJsonRedisSerializer();
        byte[] serialize1 = genericFastJsonRedisSerializer.serialize(number);
        Object deserialize1 = genericFastJsonRedisSerializer.deserialize(serialize1);
        System.out.println(deserialize1.getClass());
        System.out.println(deserialize1);
        System.out.println(number.getClass());

        System.out.println(genericFastJsonRedisSerializer.deserialize(serialize).getClass());

        // jackson2JsonRedisSerializer

    }
}
