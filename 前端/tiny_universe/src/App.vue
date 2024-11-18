<script setup>
// import KanBan from "@/components/dashboard/KanBan.vue";

//复制下文script的内容稍作修改即可，其余地方不用
import {onMounted, provide} from 'vue';
import * as live2d from 'live2d-render';

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
     width: 300,
     height: 300 * 5 / 4
   }
  })
   console.log('finish loading');
});

provide("setMessage",setMessage);

</script>

<template>
  <div class="app">
    <router-view>
    </router-view>
<!--    <KanBan></KanBan>-->
  </div>
</template>

<style scoped>
.app {
  position: relative;
  background-color: #f1e6aa;
}
header {
  line-height: 1.5;
}

</style>
