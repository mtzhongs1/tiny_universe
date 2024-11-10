<template>
  <ChatRoom2 v-if="isChatRoomAlive" :reloadChatRoom="reloadChatRoom"></ChatRoom2>
  <div>
  </div>
</template>

<script setup>
// import Music from '@/components/dashboard/entertainment/Music.vue';
// import Card from "@/components/dashboard/entertainment/Card.vue";
// import ChatRoom from "@/components/dashboard/entertainment/ChatRoom.vue";
//TODO:定义异步组件，只在需要显示时才进行渲染
import {ref} from "vue";
import {reloadUtil} from "@/util/util.js";
// import Card from "@/components/dashboard/entertainment/Card.vue";
import ChatRoom from "@/components/dashboard/entertainment/ChatRoom.vue";
import ChatRoom2 from "@/components/dashboard/entertainment/ChatRoom2.vue";
let isChatRoomAlive = ref(true);
const reloadChatRoom = () => {
  reloadUtil(isChatRoomAlive)
}
</script>

<style>
.demo-tabs{
  margin-left: 30px;
  width: 90%;
}
</style>
