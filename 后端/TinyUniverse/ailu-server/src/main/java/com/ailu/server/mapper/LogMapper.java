package com.ailu.server.mapper;

import com.ailu.entity.LogEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/7/8 下午4:07
 */
@Mapper
public interface LogMapper {
    @Insert("INSERT INTO log(status, ip, url, error_msg, method, request_method, cost_time, location, "
            + "operation_type, title, operator_type, json_result, oper_param,oper_time) "
            + "VALUES (#{status}, #{ip}, #{url}, #{errorMsg}, #{method}, #{requestMethod}, #{costTime}, "
            + "#{location}, #{operationType}, #{title}, #{operatorType}, #{jsonResult}, #{operParam},#{operTime})")
        //TODO:使用注解方式进行主键填充：@Options(useGeneratedKeys = true, keyProperty = "id")
    void insertLog(LogEntity logEntity);
}
