<template>
    <!-- TODO：动态样式 -->
    <div class="headerDiv" :style="{ backgroundImage: `url(${bgImagePath})` }">
      <Location></Location>
      <el-icon id="leftIcon" @click="setIndex(true)"><ArrowLeft /></el-icon>
      <el-icon id="rightIcon" @click="setIndex(false)"><ArrowRight /></el-icon>
    </div>
  <div class = ”mainDiv“ :style="{'background-color':color.main_color}">
    <router-view></router-view>
    <Setting></Setting>
  </div>


</template>

<script setup>
//导入区
import {computed, onMounted, ref, reactive, provide} from 'vue';
import {doGet} from '@/http/httpRequest.js'
// TODO：导入资源
import flowerSea from '@/assets/花海.jpg'; // 导入图片
import ailu1 from '@/assets/test/ailu1.jpg'; // 导入图片
import ailu2 from '@/assets/test/ailu2.jpg'; // 导入图片
import ailu3 from '@/assets/test/ailu3.jpg'; // 导入图片
import Location from '@/components/dashboard/Location.vue';
import Setting from "@/components/dashboard/Setting.vue";


//生命周期区
onMounted(() => {
  getUser();
})


//变量区
// TODO:轮播图切换
const images = [
    ailu1, ailu2, ailu3, flowerSea
];
let user = reactive({
  id: '',
  username: '',
  avatar: '',
  sex: '',
  email: '',
  birthday: '',
  description: '',
});
let index = ref(0);
let bgImagePath = computed(() => {return images[index.value]}); // 初始图片路径


//方法区
function setIndex(isLeft){
  index.value = isLeft
      ? (index.value - 1 + images.length) % images.length
      : (index.value + 1) % images.length;
}
//TODO:异步函数定义
//async声明异步函数，await等待axios调用完后获取相应对象
async function getUser(){
  const resp = await doGet("/user");
  if(resp.data.code === 1){
    const tempUser = resp.data.data;
    console.log(tempUser.birthday);
    Object.assign(user,tempUser);
    emailHide();

  }
}
//邮箱脱敏
function emailHide() {
  const email = user.email;
  const splitted = email.split('@');
  var email1 = splitted[0];
  const avg = email1.length / 2;
  email1 = email1.substring(0, email1.length - avg);
  const email2 = splitted[1];
  user.email = email1 + '***@' + email2;
}
//TODO:获取css全局变量
const getCssVariable = (varName) => {
  const rootStyle = window.getComputedStyle(document.documentElement);
  return rootStyle.getPropertyValue(varName);
}
let color = reactive({
  header_color : getCssVariable('--header-color'),
  main_color : getCssVariable('--main-color'),
  main_beside_color: getCssVariable('--main-beside-color'),
  text_color: getCssVariable('--text-color'),
  message_color: getCssVariable('--message-color'),
  message_text_color: getCssVariable('--message-text-color'),
  shadow_color:getCssVariable('--shadow-color'),
})


//其他区
provide("user",user);
provide("color",color);

</script>

<style scoped>
.headerDiv {
  width: 100%;
  height: 100vh;
  position: relative;
  background-size: cover;
  background-position: center center;
  background-repeat: no-repeat;
  transition: all 1s;
}
.el-icon{
  color: #e8e8e8;
}
#leftIcon {
  position: absolute;
  top:50%;
  left:1px;
  padding: 10px;
}
#rightIcon {
  position: absolute;
  top:50%;
  right:1px;
  padding: 10px;
}

#leftIcon:hover,#rightIcon:hover{
  background:rgb(0,0,0,0.3);
  cursor: pointer;
}


</style>
