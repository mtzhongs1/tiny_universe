package com.ailu.mapper;

import com.ailu.aop.AutoFill;
import com.ailu.aop.InsertOrUpdate;
import com.ailu.dto.user.UserUpdateDTO;
import com.ailu.entity.User;
import com.ailu.vo.user.UserSocketVO;
import com.ailu.vo.user.UserVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2/6/2024 下午8:25
 */
@Mapper
public interface UserMapper {

    @Select("select * from user where username = #{username}")
    User getUserByUsername(String username);

    @AutoFill(InsertOrUpdate.INSERT)
    void saveUser(User user);

    @Select("select id,username,sex,email,avatar,description,birthday from user where id = #{userId}")
    UserVO getUser(Long userId);

    @AutoFill(InsertOrUpdate.UPDATE)
    void updateMsg(UserUpdateDTO userUpdateDTO);

    List<UserSocketVO> getUsersById(List<Long> ids);
}
