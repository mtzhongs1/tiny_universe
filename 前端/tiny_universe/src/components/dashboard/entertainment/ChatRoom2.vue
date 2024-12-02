<template>
  <div class="min-h-screen bg-[#1B4B66] relative overflow-hidden outer-dash">
    <!-- Decorative Background Elements -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none">
      <div class="wave-animation absolute w-full">
        <svg class="w-full h-100 text-white/5" viewBox="0 0 1440 120" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M0,40 C320,100 420,0 740,60 C1060,120 1380,80 1440,80 L1440,120 L0,120 Z" fill="currentColor"/>
        </svg>
      </div>
      <!-- Fish Icons -->
      <div class="absolute left-20 top-1/4">
        <Fish class="w-8 h-8 text-white/20" />
      </div>
      <div class="absolute right-20 top-3/4">
        <Fish class="w-8 h-8 text-white/20" />
      </div>
      <!-- Anchor Icons -->
      <div class="absolute left-10 bottom-20">
        <Anchor class="w-8 h-8 text-white/20" />
      </div>
      <div class="absolute right-10 bottom-40">
        <Anchor class="w-8 h-8 text-white/20" />
      </div>
    </div>

    <!-- Main Chat Container -->
    <div class="relative min-h-screen flex flex-col items-center py-4 px-4">
      <div class="bg-white/10 backdrop-blur-sm rounded-2xl p-4 h-full overflow-y-auto">
        <div class="flex flex-wrap gap-4">
          <div v-for="user in users" :key="user.id" class="flex flex-col items-center">
            <div class="relative">
              <img :src="user.avatar" :alt="user.name" class="w-10 h-10 rounded-full">
              <span class="absolute bottom-0 right-0 w-3 h-3 bg-green-400 border-2 border-[#1B4B66] rounded-full"></span>
            </div>
            <p>{{ user.username }}</p>
          </div>
        </div>
      </div>

      <!-- Header -->
      <header class="w-full max-w-2xl flex items-center justify-between mb-4">
        <h1 class="text-white text-2xl font-semibold">聊天室</h1>
        <h6 style="color: white;text-align: center">当前房间在线人数：{{count}}</h6>
        <div class="flex items-center gap-4">
          <button class="text-white/80 hover:text-white">
            <Brain class="w-6 h-6" />
          </button>
        </div>
      </header>

      <!-- Chat Window -->
      <div class="w-full max-w-2xl bg-white/10 backdrop-blur-sm rounded-2xl flex flex-col h-[600px]">
        <!-- Chat Header -->
        <div class="flex items-center p-4 border-b border-white/10">
          <button class="text-white/80 hover:text-white">
            <Menu class="w-6 h-6" />
          </button>
          <div class="flex-1 mx-4">
            <input
                type="text"
                placeholder="搜索用户"
                class="w-full bg-transparent text-white border-none outline-none placeholder-white/60"
                v-model="username"
            >
          </div>
          <div class="flex items-center gap-2">
            <button class="text-white/80 hover:text-white">
              <Search class="w-6 h-6" @click="getUsers(username)"/>
            </button>
            <button class="text-white/80 hover:text-white">
              <Smile class="w-6 h-6" />
            </button>
            <button class="text-white/80 hover:text-white">
              <Image class="w-6 h-6" />
            </button>
          </div>
        </div>

        <!-- Messages Area -->
        <div class="flex-1 overflow-y-auto p-4 space-y-4">
          <div v-for="message in messages" :key="message.id"
               class="flex items-start gap-2"
               :class="{ 'justify-end': message.userId === user.id }">
            <template v-if="message.userId !== user.id">
              <img :src="message.avatar" alt="User avatar" class="w-8 h-8 rounded-full">
            </template>
            <div
                class="max-w-[70%] rounded-2xl px-4 py-2"
                :class="message.userId === user.id ? 'bg-blue-200 text-blue-300' : 'bg-white/10 backdrop-blur-sm text-white'"
            >
              <p>{{ message.text }}</p>
              <span class="text-xs opacity-60 mt-1 block">{{ message.id }}</span>
            </div>
            <template v-if="message.userId === user.id">
              <img :src="message.avatar" alt="User avatar" class="w-8 h-8 rounded-full" @click="toUser(message.userId)">
            </template>
          </div>
        </div>

        <!-- Input Area -->
        <div class="p-4 border-t border-white/10">
          <div class="flex items-center gap-2">
            <button class="text-white/80 hover:text-white p-2 bg-white/10 rounded-full">
              <Menu class="w-6 h-6" />
            </button>
            <div class="flex-1 relative">
              <input
                  type="text"
                  placeholder="请开始你的表演！"
                  class="w-full bg-white/10 text-white rounded-full px-4 py-2 pr-12 outline-none placeholder-white/60"
                  v-model="newMessage"
              >
            </div>
            <button class="bg-[#3B6F8F] text-white px-4 py-2 rounded-full hover:bg-[#2C5A7A]" @click="sendMessage">
              Send
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Menu, Brain, Search, Smile, Image, Fish, Anchor } from 'lucide-vue-next'
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
  // ws = new WebSocket("ws://120.46.95.186:8888/ws/" + user.id);
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

const getUsers = () => {
  doGet("/socket/users",{
    username:username.value,
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
.wave-animation {
  animation: wave 5s linear infinite;
  transform-origin: center;
}

p{
  color: #ffffff;
}

.outer-dash{
  background-image: linear-gradient(to top, #1b4b66, #3b6f8f);
}

@keyframes wave {
  0% {
    transform: translateX(0) translateY(0);
  }
  50% {
    transform: translateX(-25px) translateY(15px);
  }
  100% {
    transform: translateX(0) translateY(0);
  }
}
</style>
