<template>
<div
    v-if="props.dialogVisible"
    class="custom-dialog"
    style="
      position: fixed;
      display: flex;
      flex-direction: column;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      z-index: 1000;">
  <!-- 弹窗内容 -->
  <div style="position: relative; width: 100%;">
    <!-- 关闭按钮 -->
    <button class="close-btn"
            @click="props.closeDialog"
            style="
          position: absolute;
          top: 15px;
          right: 10px;
          cursor: pointer;
          font-size: 30px;
          z-index: 10000;
          ">
      ×
    </button>

    <!-- AiChat 组件 -->
    <div>
      <slot></slot>
    </div>
  </div>
</div>

<!-- 遮罩层 -->
<div
    v-if="props.dialogVisible"
    class="overlay"
    @click="props.closeDialog"
    style="
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: rgba(0, 0, 0, 0.5);
      z-index: 999;">
</div>
</template>
<script setup lang="ts">
let props = defineProps(['dialogVisible','closeDialog'])
</script>
