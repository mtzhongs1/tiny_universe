package com.ailu.mapper;

import com.ailu.aop.AutoFill;
import com.ailu.aop.InsertOrUpdate;
import com.ailu.dto.user.UserUpdateDTO;
import com.ailu.entity.User;
import com.ailu.vo.user.UserVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
    @Insert("insert into user(username,password,email,update_time,create_time) " +
            "values(#{username},#{password},#{email},#{updateTime},#{createTime})")
    void saveUser(User user);

    @Select("select username,sex,email,avatar,description,birthday from user where id = #{userId}")
    UserVO getUser(Long userId);

    @AutoFill(InsertOrUpdate.UPDATE)
    void updateMsg(UserUpdateDTO userUpdateDTO);

}
