<template>
    <div>
      <el-tabs class="demo-tabs">
<!--        <el-tab-pane label="音乐">-->
<!--          <Music></Music>-->
<!--        </el-tab-pane>-->
<!--        <el-tab-pane label="卡片">-->
<!--          <card></card>-->
<!--&lt;!&ndash;          <AsyncCard></AsyncCard>&ndash;&gt;-->
<!--        </el-tab-pane>-->
        <el-tab-pane label="在线聊天室">
<!--          <chat-room></chat-room>-->
          <ChatRoom v-if="isChatRoomAlive" :reloadChatRoom="reloadChatRoom"></ChatRoom>
        </el-tab-pane>
      </el-tabs>

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
