package com.ailu.server.service.impl.user;

import com.ailu.constant.BcryptConstant;
import com.ailu.context.BaseContext;
import com.ailu.dto.user.FolFanDTO;
import com.ailu.dto.user.UserLoginDTO;
import com.ailu.dto.user.UserRegisterDTO;
import com.ailu.dto.user.UserUpdateDTO;
import com.ailu.entity.User;
import com.ailu.exception.BaseException;
import com.ailu.result.PageResult;
import com.ailu.server.mapper.SettingMapper;
import com.ailu.server.mapper.UserMapper;
import com.ailu.server.properties.JwtProperties;
import com.ailu.server.service.user.EmailService;
import com.ailu.server.service.user.UserService;
import com.ailu.server.util.RedisCache;
import com.ailu.util.JwtUtil;
import com.ailu.vo.user.UserVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.ObjectUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    @Autowired
    private SettingMapper settingMapper;

    @Autowired
    private EmailService emailService;
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
    @Transactional
    public void register(UserRegisterDTO userRegisterDTO) {
        String code = userRegisterDTO.getCode();
        String key = emailService.checkCode(userRegisterDTO.getEmail(), code);
        String username = userRegisterDTO.getUsername();
        User user = userMapper.getUserByUsername(username);
        if(ObjectUtils.isNotEmpty(user)){
            throw new BaseException("用户名已存在");
        }
        user = new User();
        BeanUtils.copyProperties(userRegisterDTO,user);
        user.setPassword(BCrypt.hashpw(user.getPassword(), BcryptConstant.salt));

        userMapper.saveUser(user);

        //保存用户设置
        settingMapper.saveSetting(user.getId());

        redisCache.deleteObject(key);
    }


    @Override
    public UserVO getUser(Long userId) {
        userId = userId == null ? BaseContext.getCurrentId() : userId;
        UserVO userVO = userMapper.getUser(userId);
        return userVO;
    }

    @Override
    public void updatePwd(String email,String password) {
        //有没有该email
        Long id = userMapper.getUserByEmail(email);
        BCrypt.hashpw(password, BcryptConstant.salt);
        userMapper.updatePwd(id,password);
    }

    @Override
    public PageResult search(String content, Integer pageNum, Integer pageSize, Integer type) {
        PageHelper.startPage(pageNum,pageSize);
        Page<FolFanDTO> users = userMapper.search(content);
        String folkey = "user:" + BaseContext.getCurrentId() + "follow";
        ZSetOperations setOperations = redisCache.redisTemplate.opsForZSet();
        Set<Long> set = setOperations.rangeWithScores(folkey, 0, -1);
        users.getResult().forEach(user -> {
            user.setIsFollow(set.contains(user.getId()));
        });
        return new PageResult(users.getTotal(),users.getResult());
    }

    @Override
    public void updateMsg(UserUpdateDTO userUpdateDTO) {
        userMapper.updateMsg(userUpdateDTO);
    }

}
