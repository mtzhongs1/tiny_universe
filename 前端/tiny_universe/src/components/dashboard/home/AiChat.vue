<template>
  <div class="flex-col shrink-0 section_19 ai-chat-outer-dic" style="margin-top: 20px">
    <div class="flex-col section_4">
      <div class="flex-row justify-between items-center group_17">
        <div class="flex-row items-center">
<!--          <div class = "switchAssistant" @click="listAssistant">切换助理</div>-->
          <el-space direction="vertical">
            <img src="@/assets/机器人.svg" alt="" width="20px">
            <span class="font text" style="color: #7ec699">miku</span>
          </el-space>
        </div>
      </div>
      <el-scrollbar :max-height="400" class="message-list">
        <div class="flex-row justify-between items-center group_18">
          <!--el-space左对齐-->
          <div class="flex-row items-center problems">
            <span @click="send('你是谁？')" class="problem">你是谁?  &nbsp;&nbsp;&nbsp;></span>
            <span @click="send('怎么练习能唱得和你一样好听？')" class="problem">怎么练习能唱得和你一样好听？ &nbsp;&nbsp;&nbsp;></span>
            <div v-for="message in messages" :key="message" class="message">
              <!--TODO:根据不同情况绑定class类选择对应的样式-->
              <div class="message-content" :class="message.self ? 'rightMessage' : 'leftMessage'">
                <div style="display: flex" :class="message.self ? 'rightUser' : 'leftUser'">
                  <p class="username"> {{message.self?'我':'miku'}} </p>
                </div>
                <div style="display: flex" :class="!message.self ? 'rightUser' : 'leftUser'">
                  <img v-if="message.self" class="username" :src="message.avatar" style="width: 50px"/>
                  <img v-else class="username" src='@/assets/初音未来.jpg' style="width: 50px"/>
                </div>
                <div v-if="!isEmpty(message.urls)">
                  <img v-for="(url, index) in message.urls" :key="index" class="content" :src="url" alt="">
                </div>
                <!--markdown格式输出文件-->
                <div class="content" v-html="md.render(message.content)"></div>
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
</template>
<script setup>
import SendInput from "@/components/common/input/SendInput.vue";
import {inject, reactive, ref} from "vue";
import {doPostFile} from "@/http/httpRequest.js";
import {ElMessage} from "element-plus";
import {isEmpty} from "@/util/util.js";
import MarkdownIt from "markdown-it";
import { fetchEventSource } from '@microsoft/fetch-event-source';
let content = ref('');
let user = inject("user");
let md = new MarkdownIt();
let knowledgeId = ref(0);
let knowledgeName = ref('');
let messages = reactive([]);
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

const send = async (tempContent) => {
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
    avatar: user.avatar
  }
  messages.push(selfMessage);

  const aiMessage = reactive({
    self:false,
    content:'',
    urls: '',
  })
  messages.push(aiMessage);

  // const url = `http://120.46.95.186:8888/gpt/agent?problem=${encodeURIComponent(content.value)}&knowledgeId=${encodeURIComponent(knowledgeId.value)}`;
  const url = `http://localhost:8888/gpt/agent?problem=${encodeURIComponent(content.value)}&knowledgeId=${encodeURIComponent(knowledgeId.value)}`;
  fetchEventSource(url,
      {
        method: 'GET',
        async onmessage(event) {
          aiMessage.content += decodeUnicode(event.data.replace(/"/g, ''));
          aiMessage.urls = extractUrls(aiMessage.content);
        },
        onerror: () => {
          console.log('error');
          this.onclose();
        },
        onclose: () => {
          content.value = ''
          console.log('close');
        }})
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

// 解码包含Unicode转义序列的字符串
function decodeUnicode(str) {
  return str.replace(/\\u[\dA-Fa-f]{4}/g, function (match) {
    return String.fromCharCode(parseInt(match.substr(2), 16));
  });
}
</script>
<style lang="scss" scoped>
img{
  border-radius: 50%; /* 将图片变为圆形的关键属性 */
  object-fit: cover; /* 保持图片的宽高比并裁剪超出部分 */
}
.ai-chat-outer-dic{
  width: 600px;
}
.message-list {
  overflow-y: auto;
  position: relative;
}
.message {
  margin-bottom: 10px;
}

.message-content {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  width: 500px;
}
/*区分自己和别人的消息的样式*/
.leftMessage > * {
  align-self: flex-start;
}
.rightMessage > * {
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
    color: #7ec699;
  }
}
.leftUser {
  justify-content: flex-start;
}

.rightUser {
  justify-content: flex-end;
}
.username {
  font-weight: bold;
  color: rgb(15, 201, 238);
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
  background: var(--main-beside-color);
  flex: 1;
  color: var(--common-color);
  border-radius: 14.06px 14.06px 14.06px 14.06px;
  background: white;
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
