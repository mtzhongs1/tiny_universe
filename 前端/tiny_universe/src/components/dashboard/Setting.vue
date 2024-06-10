<template>
  <el-space class = "setting" direction="vertical">
    <div v-if="isHidden">
        <el-icon @click="switchStyle(false)" :color="color.text_color" v-if="isBlack"><Moon/></el-icon>
        <el-icon @click="switchStyle(true)" :color="color.text_color" v-else><Sunny /></el-icon>
    </div>
    <el-icon @click="isHidden = !isHidden" class="setting_icon" :color="color.text_color"><Setting /></el-icon>
  </el-space>
</template>
<script setup>
import {inject, ref} from "vue";
let color = inject('color');

let isBlack = ref(true);
let isHidden = ref(false);

const getCssVariable = (varName) => {
  const rootStyle = window.getComputedStyle(document.documentElement);
  return rootStyle.getPropertyValue(varName);
}

function switchStyle(isTrue){
  isBlack.value = isTrue;
  if(isBlack.value){
    color.header_color = getCssVariable('--header-black-color');
    color.main_color = getCssVariable('--main-black-color');
    color.main_beside_color = getCssVariable('--main-beside-black-color');
    color.text_color = getCssVariable('--text-black-color');
    color.message_color = getCssVariable('--message-black-color');
    color.message_text_color = getCssVariable('--message-black-text-color');
    color.shadow_color = getCssVariable('--shadow-black-color');
  }else{
    color.header_color = getCssVariable('--header-white-color');
    color.main_color = getCssVariable('--main-white-color');
    color.main_beside_color = getCssVariable('--main-beside-white-color');
    color.text_color = getCssVariable('--text-white-color');
    color.message_color = getCssVariable('--message-white-color');
    color.message_text_color = getCssVariable('--message-white-text-color');
    color.shadow_color = getCssVariable('--shadow-white-color');
  }
}
</script>
<style scoped>
.setting{
  position: fixed;
  bottom: 50px;
  right: 20px;
  cursor: pointer;
}
/*旋转动画效果*/
.setting_icon{
  animation: spin 2s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

</style>
