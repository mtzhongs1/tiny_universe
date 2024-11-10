package com.ailu.server.dataSource.searchType;

import com.ailu.result.PageResult;
import com.ailu.server.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/9/8 下午5:43
 */
@Service
public class UserDataSource implements DataSource{

    @Autowired
    private UserService userService;

    @Override
    public PageResult search(String content, Integer pageNum, Integer pageSize, Integer type) {
        return userService.search(content,pageNum,pageSize,type);
    }
}
