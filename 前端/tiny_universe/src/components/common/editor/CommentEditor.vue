<template>
  <div>
    <div class="comment-edit" contenteditable="true">
      <div>
        <!--工具栏-->
        <Toolbar :default-config="toolbarConfig" :editor="editor"/>
      </div>
      <div>
        <Editor :defaultConfig="editorConfig" @onCreated="handleEditorCreated"/>
      </div>
    </div>
    <FuncButton @click="sendComment" :message="'发送'"></FuncButton>
  </div>
</template>
<script setup>

import {onBeforeUnmount, onMounted, ref, shallowRef} from "vue";
import FuncButton from "@/components/common/button/FuncButton.vue";
import {Editor, Toolbar} from "@wangeditor/editor-for-vue";
const editor = shallowRef(null);
let comment = ref('');
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

const sendComment = () => {

}


</script>
<style scoped>

</style>
