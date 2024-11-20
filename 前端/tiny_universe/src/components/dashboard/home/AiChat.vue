<template>
  <div class="flex-col shrink-0 section_19" style="margin-top: 20px">
    <div class="flex-col section_4">
      <div class="flex-row justify-between items-center group_17">
        <div class="flex-row items-center">
<!--          <div class = "switchAssistant" @click="listAssistant">切换助理</div>-->
          <el-space direction="vertical">
            <img src="@/assets/机器人.svg" alt="" width="20px">
            <span class="font text">miku</span>
          </el-space>
        </div>
      </div>
      <el-scrollbar :max-height="400" class="message-list">
        <div class="flex-row justify-between items-center group_18">
          <!--el-space左对齐-->
          <div class="flex-row items-center problems">
            <span @click="send('如何学习Java?')" class="problem">如何学习Java?  &nbsp;&nbsp;&nbsp;></span>
            <span @click="send('PHP是不是世界上最好的语言？')" class="problem">PHP是不是世界上最好的语言？ &nbsp;&nbsp;&nbsp;></span>
            <div v-for="message in messages" :key="message" class="message">
              <!--TODO:根据不同情况绑定class类选择对应的样式-->
              <div class="message-content" :class="message.self ? 'rightMessage' : 'leftMessage'">
                <div style="display: flex" :class="message.self ? 'rightUser' : 'leftUser'">
                  <p class="username"> {{message.self?'我':'miku'}} </p>
                </div>
                <div v-if="!isEmpty(message.urls)">
                  <img v-for="(url, index) in message.urls" :key="index" class="content" :src="url" alt="" style="width: 365px;">
                </div>
                <div class="content" v-html="message.content"></div>
              </div>
            </div>
          </div>
        </div>
      </el-scrollbar>
      <div style="margin-top: 25px">
        <SendInput @send="send" @updateValue="updateValue"></SendInput>
        <el-upload
            class="avatar-uploader"
            :show-file-list="false"
            :http-request="uploadBaseKnowledge"
            style="position: absolute;bottom: 10px"
        >
          <p style="color: #0fc9ee">上传文档</p>
        </el-upload>
        <p>{{knowledgeName}}</p>
      </div>
    </div>
  </div>
  <el-dialog
      v-model="dialog.dialogVisible"
      title="智能体列表"
      width="800"
      :center="true"
  >
    <template #footer>
      <Agent></Agent>
    </template>
  </el-dialog>
</template>
<script setup>
import SendInput from "@/components/common/input/SendInput.vue";
import {inject, reactive, ref} from "vue";
import {doGet, doPostFile, doPostxwww} from "@/http/httpRequest.js";
import {ElMessage} from "element-plus";
import {isEmpty} from "@/util/util.js";
import Agent from "@/components/dashboard/agent/Agent.vue";

let content = ref('');
let user = inject("user");
let dialog = reactive({
  dialogVisible:false
})
let knowledgeId = ref(0);
let knowledgeName = ref('');
let messages = reactive([]);
let isLoad = ref(false);
const updateValue = (contentValue) => {
  content.value = contentValue;
}
const uploadBaseKnowledge = (fileObject) => {
  // const formData = new FormData();
  // formData.append("doc", fileObject.file)
  // formData.append("knowledgeId",fileObject.uid)
  knowledgeId.value = generateUUID();
  console.log(fileObject)
  doPostFile('/knowledge/save', {
    "doc":fileObject.file,
    "knowledgeId":knowledgeId.value
  }).then((resp) => {
    if(resp.data.code === 1){
      ElMessage.success('上传成功');
      knowledgeName.value = fileObject.file.name;
    }else{
      ElMessage.error('上传失败');
    }
  })
}
function generateUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    const r = Math.random() * 16 | 0;
    const v = c === 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

const send = (tempContent) => {
  if(!isEmpty(tempContent)){
    content.value = tempContent;
  }
  if(content.value === ''){
    ElMessage.error('请输入内容');
    return;
  }
  const selfMessage = {
    self:true,
    content:content.value,
  }
  messages.push(selfMessage);
  isLoad.value = true;
  doGet("/gpt/agent",{problem:content.value,knowledgeId:knowledgeId.value}).then((resp) => {
    if(resp.data.code === 1){
      const aiMessage = {
        self:false,
        content:resp.data.data,
        urls: extractUrls(resp.data.data)
      }
      content.value = '';
      messages.push(aiMessage);
      isLoad.value = false;
    }else{
      ElMessage.error('请求繁忙');
    }
  })
}
function extractUrls(text) {
  // 正则表达式匹配常见的图片文件扩展名
  const regex = /https?:\/\/[^\s]+?\.(jpg|jpeg|png|gif)(\?[^"\s]*)?/gi;
  let urls = [];
  let match;

  // 使用正则表达式的 exec 方法遍历所有匹配项
  while ((match = regex.exec(text)) !== null) {
    urls.push(match[0]);
  }

  return urls;
}
</script>
<style lang="scss" scoped>
.message-list {
  overflow-y: auto;
}
.message {
  display: flex;
  margin-bottom: 10px;
  position: relative;
}

.message-content {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  flex-wrap: nowrap;
}
/*区分自己和别人的消息的样式*/
.leftMessage > * {
  flex: 0 0 auto;
  align-self: flex-start;
}
.rightMessage > * {
  flex: 0 0 auto;
  align-self: flex-end;
}
.content {
  margin-top: 5px;
  word-wrap: break-word;
  background: white;
  width: 80%;
  padding: 10px;
  color: #6494cc;
  border-radius: 10px;
}
.leftUser {
  .avatar{
    order: 1;
  }
  .username{
    order: 2;
  }
}

.username {
  font-weight: bold;
  color: var(--text-color);
}
.items-center{
  text-align: center;
}

.problems{
  margin-top: 20px;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  flex-wrap: nowrap;
  gap: 10px;
}

.problem {
  text-align: center;
  opacity: 0.6;
  backdrop-filter: blur(15px) saturate(180%);
  -webkit-backdrop-filter: blur(15px) saturate(180%);
  padding: 15px;
  font-size: 14px;
  width: 80%;
  background: var(--main-beside-color);
  flex: 1;
  border-radius: 14.06px 14.06px 14.06px 14.06px;
}
.problem:hover{
  border: var(--common-color) 1px solid;
}
.section_4 {
  padding: 0 16.07px; /* 转换 rpx 到 px */
  background-color: var(--compare-color);
  position: relative;
}
.font {
  color: var(--text-color);
}
.text {
  transform: rotate(0deg);
}

.group_17 {
  padding: 13.39px 2.68px 9.38px; /* 转换 rpx 到 px */
  border-bottom: solid 0.10px var(--text-color); /* 转换 rpx 到 px */
}

.group_18 {
  background: var(--compare-color);
  padding: 10.71px 0 10.71px 4.02px; /* 转换 rpx 到 px */
}

.section_19 {
  border-radius: 14.06px 14.06px 14.06px 14.06px; /* 转换 rpx 到 px */
  filter: drop-shadow(7.03px 1.87px 13.59px rgba(0, 0, 0, 0.1));
  overflow: auto;
  font-size: 16px;
}

</style>
