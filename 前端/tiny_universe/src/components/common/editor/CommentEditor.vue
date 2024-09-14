<template>
  <div class="comment-all">
    <div class="comment-edit">
      <!--工具栏-->
      <Toolbar :default-config="toolbarConfig" :editor="editor"/>
      <Editor :defaultConfig="editorConfig" @onCreated="handleEditorCreated"/>
    </div>
    <FuncButton style="margin-top:20px;height:70px" @click="sendComment(props.parentId)" :message="'发送'"></FuncButton>
  </div>
</template>
<script setup>

import {inject, onBeforeUnmount, onMounted, ref, shallowRef} from "vue";
import FuncButton from "@/components/common/button/FuncButton.vue";
import {Editor, Toolbar} from "@wangeditor/editor-for-vue";
import {ElMessage} from "element-plus";
import {doPost} from "@/http/httpRequest.js";
import {isEmpty} from "@/util/util.js";
const editor = shallowRef(null);
let comment = ref('');
const articleId = inject("articleId");
let user = inject("user");
let reloadComment = inject("reloadComment");
let props = defineProps(['parentId']);
//编辑器配置（即文本框的内容）
const editorConfig = {
  // MENU_CONF:{},
  placeholder: '请输入内容...',
  scroll: true,
}
const toolbarConfig = {}

onMounted(() => {
  toolbarConfig.toolbarKeys = ['emotion']
})

onBeforeUnmount(() => {
  //实例附加到 editor 引用。
  editor.value.destroy()

})

const handleEditorCreated = (editorInstance) => {
  //实例附加到 editor 引用。
  editor.value = editorInstance
};

const sendComment = (parentId) => {
  const fd = new FormData();
  if(!isEmpty(parentId)){
    fd.append("parentId",parentId)
  }
  fd.append("articleId",articleId.value);
  fd.append("userId",user.id);
  fd.append("content",editor.value.getHtml())
  doPost("/comment",fd).then((resp) => {
    if(resp.data.code === 1){
      ElMessage.success("评论成功");
      editor.value.setHtml("");
      reloadComment();
    }else{
      ElMessage.error("服务器繁忙")
    }
  })
}


</script>
<style scoped>
.comment-all{
  display: flex;
  gap: 20px;
  flex-direction: row;
  width: 100%;
}
.comment-edit{
  width: 100%;
  display: flex;
  flex-direction: column;
  height: 100px;
  vertical-align: middle;
}

:deep(.w-e-toolbar) {
  background-color: var(--comment-color) !important;
}
:deep(.w-e-text-container){
  background-color: var(--comment-color) !important;
}
</style>
