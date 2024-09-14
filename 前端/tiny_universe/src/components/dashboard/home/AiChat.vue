<template>
  <div class="flex-col shrink-0 section_19">
    <div class="flex-col section_4">
      <div class="flex-row justify-between items-center group_17">
        <div class="flex-row items-center">
          <el-space direction="vertical">
            <img src="@/assets/机器人.svg" alt="" width="20px">
            <span class="font text">学习小助理</span>
          </el-space>
        </div>
      </div>
      <el-scrollbar :max-height="400" class="message-list">
        <div class="flex-row justify-between items-center group_18">
          <!--el-space左对齐-->
          <div class="flex-row items-center problems">
            <span @click="send('如何学习Java?')" class="problem">如何学习Java?  &nbsp;&nbsp;&nbsp;></span>
            <span @click="send('PHP是不是世界上最好的语言？')" class="problem">PHP是不是世界上最好的语言？ &nbsp;&nbsp;&nbsp;></span>
            <div v-for="message in messages" class="message">
              <!--TODO:根据不同情况绑定class类选择对应的样式-->
              <div class="message-content" :class="message.self ? 'rightMessage' : 'leftMessage'">
                <div style="display: flex" :class="message.self ? 'rightUser' : 'leftUser'">
                  <p class="username"> {{message.self?'我':'学习小助理'}} </p>
                </div>
                <p class="content">{{ message.content }}</p>
              </div>
            </div>
          </div>
        </div>
      </el-scrollbar>
      <div style="margin-top: 25px">
        <SendInput @send="send" @updateValue="updateValue"></SendInput>
      </div>
    </div>
  </div>
</template>
<script setup>
import SendInput from "@/components/common/input/SendInput.vue";
import {inject, reactive, ref} from "vue";
import {doGet} from "@/http/httpRequest.js";
import {ElMessage} from "element-plus";
import {isEmpty} from "@/util/util.js";
let content = ref('');
let user = inject("user");
let messages = reactive([]);
const updateValue = (contentValue) => {
  content.value = contentValue;
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
  doGet("/gpt/chat",{content:content.value}).then((resp) => {
    if(resp.data.code === 1){
      const aiMessage = {
        self:false,
        content:resp.data.data,
      }
      content.value = '';
      messages.push(aiMessage);
    }else{
      ElMessage.error('请求繁忙');
    }
  })
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
  margin: 25px 0;
  background-color: var(--compare-color);
  border-radius: 14.06px 14.06px 14.06px 14.06px; /* 转换 rpx 到 px */
  filter: drop-shadow(7.03px 1.87px 13.59px rgba(0, 0, 0, 0.1));
  overflow: hidden;
  font-size: 16px;
}

</style>
