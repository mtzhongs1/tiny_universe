package com.ailu.mapper;

import com.ailu.dto.user.MessageDTO;
import com.ailu.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/7/3 下午2:47
 */
@Mapper
public interface ChatMapper {
    @Select("select * form char limit 50")
    List<Message> getHistory();

    void saveHistory(List<MessageDTO> messageDTOs);
}
