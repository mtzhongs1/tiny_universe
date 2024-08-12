package com.ailu.server.mapper;

import com.ailu.dto.comment.CommentDTO;
import com.ailu.dto.comment.CommentUpdateDTO;
import com.ailu.dto.comment.CommentVO;
import com.ailu.server.aop.AutoFill;
import com.ailu.server.aop.InsertOrUpdate;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/7/5 上午11:23
 */
@Mapper
public interface CommentMapper {
    @Select("select c.id,u.id as userId,c.article_id,c.love,u.username,u.avatar,c.parent_id,c.content,c.create_time,c.update_time " +
            "from comment c,user u " +
            "where u.id = c.user_id and c.article_id = #{articleId} " +
            "order by c.create_time asc")
    List<CommentVO> getComments(Long articleId);

    @Insert("insert into comment(content,create_time,update_time,user_id,article_id,parent_id) " +
            "values(#{content},#{createTime},#{updateTime},#{userId},#{articleId},#{parentId})")
    @AutoFill(InsertOrUpdate.INSERT)
    void saveComment(CommentDTO comment);

    @Delete("delete from comment where id = #{id} or parent_id = #{id}")
    void deleteComment(Long id);

    @Delete("<script>" +
            "delete from comment where article_id in " +
            "<foreach item='articleId' index='index' collection='articleIds' open='(' separator=',' close=')'>" +
            "#{articleId}" +
            "</foreach>" +
            "</script>")
    void deleteCommentByArticleId(List<Long> articleIds);

    @AutoFill(InsertOrUpdate.UPDATE)
    void updateComment(CommentUpdateDTO commentDTO);
}
