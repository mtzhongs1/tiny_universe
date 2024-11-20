<script setup>
// import KanBan from "@/components/dashboard/KanBan.vue";

//复制下文script的内容稍作修改即可，其余地方不用
import {onMounted, provide} from 'vue';
import * as live2d from 'live2d-render';
import {ChatDotRound} from "@element-plus/icons-vue";

const setMessage = (msg,duration) => {
  live2d.setMessageBox(msg,duration);
}

onMounted(async () => {
   await live2d.initializeLive2D({
   BackgroundRGBA: [0.0, 0.0, 0.0, 0.0],
   // LoadFromCache: true,
   ShowToolBox: true,
   ResourcesPath: '/public/miku/miku_pro_jp/runtime/miku_sample_t04.model3.json',
   // ResourcesPath: '/public/miku/miku/runtime/miku.model3.json',
   // ResourcesPath: '/public/星野爱/星野爱.model3.json',
   //这是你自己的live2d资源文件，相对路径引用即可
   CanvasSize:{
     width: 200,
     height: 200 * 450/250
   }
  })
});

provide("setMessage",setMessage);

</script>

<template>
  <div class="app">
    <router-view>
    </router-view>
  </div>
</template>

<style scoped>
.ai-chat {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  z-index: 10001;
  transition: 0.7s cubic-bezier(0.23, 1, 0.32, 1);
  position: fixed;
  right: 165px;
  top: 400px;
}

.ai-chat *{
  margin: 2px;
  display: flex;
  height: 20px;
  width: 20px;
  justify-content: center;
  align-items: center;
  cursor: pointer;

  background-color: deepskyblue;
  color: white;
  border-radius: 0.9em;
  transition: .5s ease;
}

.app {
  position: relative;
  background-color: #f1e6aa;
}
header {
  line-height: 1.5;
}
#live2dMessageBox-content {
    background-color: #FF95BC;
    color: white;
    font-family: var(--base-font);
    padding: 10px;
    height: fit-content;
    border-radius: .7em;
    word-break: break-all;
    border-right: 1px solid transparent;
}

.live2dMessageBox-content-hidden {
    opacity: 0;
    transform: scaleY(0.2);
    transition: all 0.35s ease-in;
    -moz-transition: all 0.35s ease-in;
    -webkit-transition: all 0.35s ease-in;
}

.live2dMessageBox-content-visible {
    opacity: 1;
    transition: all 0.35s ease-out;
    -moz-transition: all 0.35s ease-out;
    -webkit-transition: all 0.35s ease-out;
}
</style>
