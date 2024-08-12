<template>
  <div class="commentDiv">
    <div>
      评论：{{total}}
    </div>
    <div class="commentChild">
      <el-avatar fit="cover" :src="user.avatar" :size="70"></el-avatar>
      <CommentEditor ></CommentEditor>
    </div>
    <el-divider></el-divider>
    <div>
      <comment-item :comments="comments" :removeComment="removeComment"></comment-item>
    </div>
  </div>
</template>
<script setup>

import CommentEditor from "@/components/common/editor/CommentEditor.vue";
import {doGet, doPost} from "@/http/httpRequest.js";
import {inject, onMounted, provide, ref, watch} from "vue";
import CommentItem from "@/components/dashboard/article/CommentItem.vue";


let user = inject("user");
let articleId = inject("articleId");
let comments = ref([]);

let total = ref(0);

onMounted(() => {
  getComments();
})

watch(() => articleId.value,() => {
  getComments();
})

const removeComment = (comment) => {
  comments.value.splice(comments.value.indexOf(comment),1);
  total.value--;
  total.value -= comment.children ? comment.children.length : 0;
}

const getComments = () => {
  if(articleId.value === undefined){
    return;
  }
  doGet("/comment/comments/"+articleId.value+"/"+1).then(res => {
    const data = res.data.data;
    comments.value = data.records;
    total.value = data.total;
  })
}

</script>
<style scoped>

.commentChild{
  display: flex;
  flex-direction: row;
  gap: 20px;
  width: 100%;
}

.commentDiv{
  background: var(--main-beside-color);
  padding: 50px;
  display: flex;
  flex-direction: column;
  gap: 40px;
}
.commentDiv > *{
  flex: 0 0 auto;
  align-self: flex-start;
}
</style>
