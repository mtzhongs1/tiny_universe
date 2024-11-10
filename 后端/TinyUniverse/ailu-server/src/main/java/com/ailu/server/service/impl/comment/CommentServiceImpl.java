package com.ailu.server.service.impl.comment;

import com.ailu.constant.SortConstant;
import com.ailu.context.BaseContext;
import com.ailu.dto.comment.CommentDTO;
import com.ailu.dto.comment.CommentUpdateDTO;
import com.ailu.dto.comment.CommentVO;
import com.ailu.result.PageResult;
import com.ailu.server.mapper.CommentMapper;
import com.ailu.server.service.comment.CommentService;
import com.ailu.server.util.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/4 上午11:55
 */

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private RedisCache redisCache;

    @Override
    public PageResult getComments(Long articleId, int type) {
        List<CommentVO> commentVOs = commentMapper.getComments(articleId);
        List<CommentVO> commentTrees = buildTree(commentVOs);
        //根据评论时间进行降序排序
        if(type == SortConstant.TimeSort){
            commentTrees.sort((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()));
        }
        //根据点赞数进行排序
        else{
            commentTrees.sort((o1, o2) -> o2.getLove().compareTo(o1.getLove()));
        }
        return new PageResult(commentVOs.size(),commentTrees);
    }

    @Override
    public void saveComment(CommentDTO commentDTO) {
        // commentDTO.setLove(0L);
        updateCommentCount(commentDTO.getArticleId(), 1L);
        commentMapper.saveComment(commentDTO);
    }



    @Override
    public void deleteComment(Long articleId,Long id) {
        updateCommentCount(articleId, -1L);
        commentMapper.deleteComment(id);
    }

    @Override
    public void deleteCommentByArticleId(List<Long> articleIds) {
        commentMapper.deleteCommentByArticleId(articleIds);
    }

    @Override
    public void updateComment(CommentUpdateDTO commentDTO) {
        commentMapper.updateComment(commentDTO);
    }

    @Override
    public Boolean doLove(Long id) {
        Long userId = BaseContext.getCurrentId();
        SetOperations setOperations = redisCache.redisTemplate.opsForSet();
        String skey = "comment:love:"+id;
        Boolean isMember = setOperations.isMember(skey, userId);
        if(Boolean.TRUE.equals(isMember)){
            setOperations.remove(skey,userId);
        }
        else{
            setOperations.add(skey,userId);
        }
        return !isMember;
    }
    private void updateCommentCount(Long articleId,long value) {
        String key = "article_active:"+ articleId;
        // redisCache.redisTemplate.opsForHash().increment(key,"commentCount",1);
        Long commentCount = redisCache.getCacheMapValue(key, "commentCount");
        commentCount+=value;
        redisCache.setCacheMapValue(key,"commentCount",commentCount);
    }

    //TODO:构建二层树结构
    private List<CommentVO> buildTree(List<CommentVO> commentVOs) {
        //map分组，key为parentId,value为CommentVO
        Map<Long, List<CommentVO>> map = new HashMap<>();
        SetOperations setOperations = redisCache.redisTemplate.opsForSet();
        Long userId = BaseContext.getCurrentId();
        for (CommentVO commentVO : commentVOs) {
            commentVO.setIsLove(setOperations.isMember("comment:love:"+commentVO.getId(),userId));
            commentVO.setLove(setOperations.size("comment:love:"+commentVO.getId()));
            map.merge(commentVO.getParentId(),new ArrayList<>(Collections.singletonList(commentVO)),(old, newList)->{
                old.add(commentVO);
                return old;
            });
        }
        //顶级节点
        List<CommentVO> parents = map.getOrDefault(null,new ArrayList<>());
        for (CommentVO parent : parents) {
            parent.setChildren(new ArrayList<>());
            recursion(parent.getChildren(),parent.getId(),map);
        }
        return parents;
    }

    //递归,commentVOs找出父id==parentId的节点，然后添加到parent集合中
    private void recursion(List<CommentVO> parent, Long parentId, Map<Long,List<CommentVO>> map){
        List<CommentVO> childs = map.get(parentId);
        if(childs != null){
            for (CommentVO child : childs) {
                parent.add(child);
                //TODO:这里用parent是因为是二级结构，如果是多级结构则是child.getChildren()
                recursion(parent,child.getId(),map);
            }
        }
    }


}
