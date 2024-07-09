package com.ailu.server.service.impl.comment;

import com.ailu.constant.SortConstant;
import com.ailu.dto.comment.CommentDTO;
import com.ailu.dto.comment.CommentVO;
import com.ailu.server.mapper.CommentMapper;
import com.ailu.server.service.comment.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/4 上午11:55
 */

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public List<CommentVO> getComments(Long articleId,int type) {
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
        return commentTrees;
    }

    @Override
    public void saveComment(CommentDTO commentDTO) {
        commentMapper.saveComment(commentDTO);
    }

    @Override
    public void deleteComment(Long id) {
        commentMapper.deleteComment(id);
    }

    @Override
    public void deleteCommentByArticleId(List<Long> articleIds) {
        commentMapper.deleteCommentByArticleId(articleIds);
    }


    //TODO:构建二层树结构
    private List<CommentVO> buildTree(List<CommentVO> commentVOs) {
        //map分组，key为parentId,value为CommentVO
        Map<Long, List<CommentVO>> map = new HashMap<>();
        for (CommentVO commentVO : commentVOs) {
            map.merge(commentVO.getParentId(),new ArrayList<>(),(old,newList)->{
                old.add(commentVO);
                return old;
            });
        }
        //顶级节点
        List<CommentVO> parents = map.get(null);
        for (CommentVO parent : parents) {
            parent.setChildren(new ArrayList<>());
            recursion(parent.getChildren(),parent.getId(),map);
        }
        return parents;
    }

    //递归,commentVOs找出父id==parentId的节点，然后添加到parent集合中
    private void recursion(List<CommentVO> parent, Long parentId, Map<Long,List<CommentVO>> map){
        List<CommentVO> childs = map.get(parentId);
        for (CommentVO child : childs) {
            parent.add(child);
            //TODO:这里用parent是因为是二级结构，如果是多级结构则是child.getChildren()
            recursion(parent,child.getId(),map);
        }
    }


}
