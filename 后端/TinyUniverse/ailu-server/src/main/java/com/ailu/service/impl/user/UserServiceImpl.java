package com.ailu.service.impl.user;

import com.ailu.constant.BcryptConstant;
import com.ailu.context.BaseContext;
import com.ailu.dto.user.UserLoginDTO;
import com.ailu.dto.user.UserRegisterDTO;
import com.ailu.dto.user.UserUpdateDTO;
import com.ailu.entity.User;
import com.ailu.exception.BaseException;
import com.ailu.mapper.UserMapper;
import com.ailu.properties.JwtProperties;
import com.ailu.service.UserService;
import com.ailu.util.JwtUtil;
import com.ailu.util.RedisCache;
import com.ailu.vo.user.UserVO;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2/6/2024 下午4:48
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private JwtProperties jwtProperties;
    @Override
    public String login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();
        //根据用户名查询用户
        User user = userMapper.getUserByUsername(username);
        if(ObjectUtils.isEmpty(user)){
            throw new BaseException("用户不存在");
        }
        if(!user.getStatus()){
            throw new BaseException("账号被封禁");
        }
        String retPassword = user.getPassword();
        password = BCrypt.hashpw(password,BcryptConstant.salt);
        if(!password.equals(retPassword)) {
            throw new BaseException("密码错误");
        }
        Map<String,Object> claims = new HashMap<>();
        claims.put("userId",user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getSecret(),Long.parseLong(jwtProperties.getExpiration()),claims);
        return token;
    }

    @Override
    public void register(UserRegisterDTO userRegisterDTO) {
        String code = userRegisterDTO.getCode();
        String key = checkCode(userRegisterDTO.getEmail(), code);
        String username = userRegisterDTO.getUsername();
        User user = userMapper.getUserByUsername(username);
        if(ObjectUtils.isNotEmpty(user)){
            throw new BaseException("用户名已存在");
        }
        user = new User();
        BeanUtils.copyProperties(userRegisterDTO,user);
        user.setPassword(BCrypt.hashpw(user.getPassword(), BcryptConstant.salt));

        userMapper.saveUser(user);
        redisCache.deleteObject(key);
    }


    @Override
    public UserVO getUser() {
        Long userId = BaseContext.getCurrentId();
        UserVO userVO = userMapper.getUser(userId);
        return userVO;
    }

    @Override
    public void updateMsg(UserUpdateDTO userUpdateDTO) {
        userUpdateDTO.setId(BaseContext.getCurrentId());
        userMapper.updateMsg(userUpdateDTO);
    }

    private String checkCode(String email, String code) {
        String key = "code：" + email;
        String redisCode = redisCache.getCacheObject(key);
        if(StringUtils.isEmpty(redisCode) || !code.equals(redisCode)){
            throw new BaseException("验证码错误");
        }
        return key;
    }
}
