<template>
  <div id="chat-app">
    <div class="chat-background">
      <el-row justify="center" class="all-window">
        <el-col :span="10" class="user-window">
          <el-row class="search-all">
            <el-col :span="2">
              <el-icon @click="getUsers(username)" style="color:white;opacity: 0.5;cursor:pointer" :size="25"><Search /></el-icon>
            </el-col>
            <el-col :span="6">
              <input class="search-name" v-model="username" placeholder="搜索..."/>
            </el-col>
          </el-row>
            <el-card class="user-card" v-for="(chatUser,index) in users" :key="index">
                <el-avatar @click="toUser(chatUser.id)" style="vertical-align: middle" :src="chatUser.avatar" :size="70" alt=""/>
                <span style="display: inline-block;margin: 0 20px">
                  {{chatUser.username}}
                </span>
            </el-card>
        </el-col>
        <el-col :span="14" class="chat-window">
          <h6 style="color: white;text-align: center">当前房间在线人数：{{count}}</h6>
          <el-divider>
            <el-icon style="color: white;" :size="30"><CameraFilled /></el-icon>
          </el-divider>
          <!-- 消息列表 -->
          <el-scrollbar :max-height="500" class="message-list">
            <div v-for="message in messages" :key="message.userId" class="message">
              <!--TODO:根据不同情况绑定class类选择对应的样式-->
              <div class="message-content" :class="user.id === message.userId ? 'rightMessage' : 'leftMessage'">
                <div style="display: flex" :class="user.id === message.userId ? 'rightUser' : 'leftUser'">
                  <p class="username">{{ message.username }}</p>
                  <el-avatar class="avatar" @click="toUser(message.userId)" fit="cover" :size="70" :src="message.avatar" />
                </div>
                <p class="text">{{ message.text }}</p>
              </div>
            </div>
          </el-scrollbar>

          <!-- 输入框和发送按钮 -->
          <div class="input-area">
            <textarea v-model="newMessage" placeholder="请开始你的表演..." @keyup.enter="sendMessage"></textarea>
            <button @click="sendMessage">Send</button>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script setup>
import {getCurrentInstance, inject, onMounted,reactive, ref} from "vue";
import {ElMessage} from "element-plus";
import {doGet, doPost} from "@/http/httpRequest.js";
import {newRoute} from "@/util/router.js";
import {useRouter} from "vue-router";
const instance = getCurrentInstance();
const router = useRouter();
let ws = null;
let user = inject('user');

let messages = reactive([]);
let newMessage = ref();

let users = ref([]);
//username:搜索框文字
let username = ref();
let count = ref(0);
const reconnectInterval = 3000;

onMounted( () => {
  connectWebSocket();
})

const sendMessage = () => {
  if(!newMessage.value){
    ElMessage.error("请输入内容");
    return;
  }
  const fd = new FormData();
  fd.append("userId",user.id);
  fd.append("username",user.username);
  fd.append("avatar",user.avatar);
  fd.append("text",newMessage.value);
  doPost("/socket/sendAll",fd).then((resp) => {
    if(resp.data.code === 1){
      newMessage.value = "";
    }else{
      ElMessage.error("服务器繁忙");
    }
  });
}
const connectWebSocket = () => {
  ws = new WebSocket("ws://localhost:8888/ws/" + user.id);
  ws.addEventListener("open", onOpen.bind(instance), false);
  ws.addEventListener("close", onClose.bind(instance), false);
  ws.addEventListener("error", onError.bind(instance), false);
  ws.addEventListener("message", onMessage.bind(instance), false);
};
const onOpen = () => {
  console.log("WebSocket连接成功");
  getUsers();
}
const onClose = () => {
  console.log("WebSocket连接关闭");
  getUsers();
}
const onError = (e) => {
  console.log("websocket连接失败",e);
  setTimeout(connectWebSocket, reconnectInterval);
}
const onMessage = (event) => {
  const allData = event.data;
  console.log("接收消息:",allData);
  const message = JSON.parse(allData);
  console.log(message)
  messages.push(message);
}

const toUser = (userId) => {
  newRoute('/dashboard/user_detail/article_view/'+userId,router);
}

const getUsers = (username) => {
  doGet("/socket/users",{
    username:username,
    pageSize:5
  }).then((resp) => {
    if(resp.data.code === 1){
      const res = resp.data.data;
      users.value = res.data;
      count.value = res.cnt;
      console.log(users.value);
    }else{
      ElMessage.error("服务器繁忙");
    }
  })
}

</script>
<style scoped>
#chat-app {
  display: flex;
  justify-content: center;
  align-items: center;
}

.chat-background{
  background: #c6dcf1;
  padding: 50px;
  border-radius: 10px;
  display:flex;
  gap: 0;
}

.all-window{
  width: 70vw;
}

.user-window{
  border-bottom-left-radius: 10px;
  border-top-left-radius: 10px;
  background: #2c5c9c;
}

.chat-window {
  border-bottom-right-radius: 10px;
  border-top-right-radius: 10px;
  background-color: #6494cc;
  padding: 20px;
  width:50vw;
}

.message-list {
  overflow-y: auto;
  height: 400px;
  margin-bottom: 20px;
  background-color: #6494cc;
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
  color: white;
}

.text {
  margin-top: 5px;
  word-wrap: break-word;
  background: white;
  width: 80%;
  padding: 10px;
  color: #6494cc;
  border-radius: 10px;
}

.input-area {
  display: flex;
  bottom: 20px;
}

textarea {
  flex-grow: 1;
  padding: 10px;
  border: none;
  border-radius: 5px;
  margin-right: 10px;
  resize: none;
  background: #2c5c9c;
  color: white;
}

textarea::placeholder{
  color: #b8f8f8;
}

textarea:focus{
  outline: none; /* 隐藏默认textarea的轮廓 */
}

button {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  background-color: #2c5c9c;
  color: #fff;
  cursor: pointer;
}

.search-all{
  margin: 20px;
  background: #2c4c74;
  padding: 10px;
  max-width: 100%;
  overflow: hidden;
}

.search-name{
  background: #2c4c74;
  color: white;
  padding: 5px;
  border-radius: 5px;
  border:none;
}

.search-name:focus{
  outline: none; /* 隐藏默认textarea的轮廓 */
}

.search-name::placeholder{
  color: white;
  opacity: 0.5;
}

.user-card{
  background:#2c5c9c;
  border: none;
  text-align: center;
  color: white;
}

:deep(.el-divider__text){
  background: #6494cc;
}
</style>
