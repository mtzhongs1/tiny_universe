<template>
  <el-card v-for="(comment,index) in props.comments" :key="index">
    <el-space alignment="flex-start" :size="20">
      <el-avatar @click="toUser(comment.userId)" :fit="'cover'" :src="comment.avatar" :size="50"></el-avatar>
      <div class="comment-item">
        <span>{{ comment.username}}</span>
        <span v-if="props.parent && props.parent.id !== comment.parentId"> 回复 {{props.parent.username}}： </span>
        <p v-html="comment.content"></p>
        <el-space>
          <p>{{comment.createTime}}</p>
          <span>
            <el-icon :color="comment.isLove?'#62b9ec':''" @click="doLove(comment)"><Sugar /></el-icon>{{comment.love}}
          </span>
          <span>
            <el-popover placement="bottom" :width="600" trigger="click">
            <template #reference>
              <span>
                <el-icon><ChatLineSquare /></el-icon>
                <span v-if="!comment.parentId">
                  {{comment.children ? comment.children.length : 0}}
                </span>
              </span>
            </template>
            <div style="text-align: center">
              <comment-editor :parentId = "comment.id"></comment-editor>
            </div>
          </el-popover>
          </span>
          <el-popover v-if="comment.userId === user.id" placement="right" :width="150" trigger="click">
            <template #reference>
              <el-icon><More /></el-icon>
            </template>
            <div style="text-align: center">
              <p @click="deleteComment(comment)" class="comment-func">删除</p>
            </div>
          </el-popover>
        </el-space>
        <comment-item v-if="comment.children && comment.children.length > 0" :comments="comment.children" :parent="comment"></comment-item>
      </div>
    </el-space>
  </el-card>
</template>

<script setup>

import {newRoute} from "@/util/router.js";
import {useRouter} from "vue-router";
import {doDelete, doDeletexwww, doPost, doPostxwww} from "@/http/httpRequest.js";
import {ElMessage} from "element-plus";
import CommentEditor from "@/components/common/editor/CommentEditor.vue";
import {inject} from "vue";
let router = useRouter();
let props = defineProps(["comments","removeComment","parent"]);
let user = inject("user");
let reloadComment = inject("reloadComment");
const toUser = (userId) => {
  newRoute("/dashboard/user_detail/"+userId,router);
}
const doLove = (comment) => {
  doPostxwww("/comment/doLove",{
    articleId:comment.articleId,
    id:comment.id
  }).then((resp) => {
    if(resp.data.code === 1){
      const isLove = resp.data.data;
      if(isLove){
        comment.love++;
      }
      else{
        comment.love--;
      }
      comment.isLove = isLove;
    }else{
      ElMessage.error("服务器繁忙");
    }
  })
}

const deleteComment = (comment) => {
  doDeletexwww("/comment",{
    id:comment.id,
    articleId: comment.articleId
  }).then((resp) => {
    if(resp.data.code === 1){
      ElMessage.success("删除成功");
      reloadComment();
      // props.removeComment(props.parent,comment);
    }else{
      ElMessage.error("服务器繁忙");
    }
  })
}
</script>

<style scoped>
.el-card,.el-card.is-always-shadow{
  border: none;
  box-shadow: none;
}

.el-icon:hover,.comment-func:hover{
  color: var(--common-color);
  cursor: pointer;
}
</style>
