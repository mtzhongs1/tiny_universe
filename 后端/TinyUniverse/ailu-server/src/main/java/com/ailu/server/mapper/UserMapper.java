package com.ailu.server.mapper;

import com.ailu.dto.user.FolFanDTO;
import com.ailu.dto.user.UserUpdateDTO;
import com.ailu.entity.User;
import com.ailu.server.aop.AutoFill;
import com.ailu.server.aop.InsertOrUpdate;
import com.ailu.vo.user.UserSocketVO;
import com.ailu.vo.user.UserVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;
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

    List<UserSocketVO> getUsersById(Collection<Long> ids);

    List<FolFanDTO> getUsersByFid(Collection<Long> ids);

    @Select("select id from user where email = #{email}")
    Long getUserByEmail(String email);

    @Update("update user set password = #{password} where id = #{id}")
    void updatePwd(Long id, String password);

    @Select("select id,username,avatar from user where username like  CONCAT('%', #{content}, '%')")
    Page<FolFanDTO> search(String content);
}
