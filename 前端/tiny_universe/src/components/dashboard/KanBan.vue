<script setup>
import * as PIXI from 'pixi.js';
import { Live2DModel } from 'pixi-live2d-display/cubism4';
import { ref, onMounted, onBeforeUnmount } from 'vue';

window.PIXI = PIXI; // 为了pixi-live2d-display内部调用

const app = ref(null); // 为了存储pixi实例
const model = ref(null); // 为了存储live2d实例

const modelPaths = [
  '/public/aug/destroy/destroy.model3.json',
  '/public/miku/miku/runtime/miku.model3.json',
  '/public/miku/mao_pro_zh/runtime/mao_pro.model3.json',
  '/public/卡芙卡_vts/kafuka1.model3.json',
  '/public/星野爱/星野爱.model3.json'
];

const liveCanvas = ref(null);

onMounted(async () => {
  app.value = new PIXI.Application({
    view: liveCanvas.value,
    autoStart: true,
    resizeTo: window,
    backgroundAlpha: 0,
  });

  model.value = await Live2DModel.from(modelPaths[2]);
  app.value.stage.addChild(model.value);
  model.value.scale.set(0.2);
  console.log(model.value);
  console.log(model.value.motion)
  // model.value.x = 1150;
  // model.value.y = -200;
  model.value.width = 300;
  model.value.height = model.value.width * 1680 / 1160
  // model.value.height=1000;
});

onBeforeUnmount(() => {
  model.value?.destroy();
  app.value?.destroy();
});

const modelTap = () => {
    model.value.motion('mtn','01','01');
}

</script>

<template>
    <el-button @click="modelTap">Tap Model</el-button>
    <canvas class="live2d" ref="liveCanvas"></canvas>
</template>

<style scoped>
.app-div{
  position: relative;
}
.live2d{
  z-index: 10000;
  position: absolute;
}

</style>