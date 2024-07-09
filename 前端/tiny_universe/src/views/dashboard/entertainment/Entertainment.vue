<template>
    <div>
      <el-tabs class="demo-tabs">
<!--        <el-tab-pane label="音乐">-->
<!--          <Music></Music>-->
<!--        </el-tab-pane>-->
        <el-tab-pane label="卡片">
<!--          <card></card>-->
          <AsyncCard></AsyncCard>
        </el-tab-pane>
        <el-tab-pane label="在线聊天室">
<!--          <chat-room></chat-room>-->
          <AsyncChatRoom></AsyncChatRoom>
        </el-tab-pane>
        <el-tab-pane label="广告">
        </el-tab-pane>
      </el-tabs>

    </div>
</template>

<script setup>
// import Music from '@/components/dashboard/entertainment/Music.vue';
// import Card from "@/components/dashboard/entertainment/Card.vue";
// import ChatRoom from "@/components/dashboard/entertainment/ChatRoom.vue";
//TODO:定义异步组件，只在需要显示时才进行渲染
import {defineAsyncComponent} from "vue";
const AsyncCard = defineAsyncComponent(() => import("@/components/dashboard/entertainment/Card.vue"));
const AsyncChatRoom = defineAsyncComponent(() => import("@/components/dashboard/entertainment/ChatRoom.vue"));
</script>

<style>
.demo-tabs > .el-tabs__content {
  padding: 32px;
  color: #6b778c;
  font-size: 32px;
  font-weight: 600;
}
</style>
