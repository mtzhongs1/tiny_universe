<template>
  <div class="outerDiv">
    <!-- TODO：动态样式，注意：路径中不能有空格！！！ -->
    <div :style="{ 'background-image': `url(${bgImagePath})` }" class="headerDiv">
      <Location class="location"></Location>
      <el-icon v-show="upDown" id="leftIcon" @click="setIndex(true)">
        <ArrowLeftBold />
      </el-icon>
      <el-icon v-show="upDown" id="rightIcon" @click="setIndex(false)">
        <ArrowRightBold />
      </el-icon>
      <el-icon v-if="upDown" class="upDownIcon" @click="setUpDown">
        <ArrowUpBold />
      </el-icon>
      <el-icon v-else class="upDownIcon" @click="setUpDown">
        <ArrowDownBold />
      </el-icon>
    </div>
    <div class="mainDiv">
      <router-view style="margin: 50px auto auto;"></router-view>
      <Setting :images = "images" :updateBgImage="updateBgImage"></Setting>
    </div>
    <div class="footer">
      &copy; 2024 By ailu &nbsp
      <a href="https://github.com/mtzhongs1/tiny_universe" target="_blank">
        <img alt="GitHub" class="github" src="../../assets/GitHub.svg" width="30">
      </a>
    </div>
  </div>

</template>
<script setup>
//导入区
import {onMounted, ref, reactive, provide, computed} from 'vue';
import { doGet } from '@/http/httpRequest.js'
// TODO：导入资源
import Location from '@/components/dashboard/Location.vue';
import Setting from "@/components/dashboard/Setting.vue";
import { setCssVariable, setProperty} from "@/util/util.js";
import {ElMessage} from "element-plus";

//生命周期区
onMounted(() => {
  getImages();
  switchTheme();
  getUser();
})


//变量区
// TODO:轮播图切换
let upDown = ref(false);
let isfooter = ref(false);
const images = ref([]);
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
let bgImagePath = computed(() => {return images.value[index.value]}); // 初始图片路径
//方法区
const setUpDown = () => {
  if (upDown.value) {
    //  点击向上的箭头
    setProperty('--header-height','60px');
    // 修改底部的样式

  }else{
    setProperty('--header-height','100vh');
  }
  upDown.value = !upDown.value
  setTimeout(() => {
    isfooter.value = !isfooter.value;
  },500);
}
function setIndex(isLeft) {
  index.value = isLeft
    ? (index.value - 1 + images.value.length) % images.value.length
    : (index.value + 1) % images.value.length;
}

const updateBgImage = (index,url) => {
  images[index] = url;
}

function getUser() {
  doGet("/user").then((resp) => {
  if (resp.data.code === 1) {
    const tempUser = resp.data.data;
    Object.assign(user, tempUser);
    emailHide();
  }
  })
}
const getImages = () => {
  doGet("/setting/background").then((resp) => {
    if(resp.data.code === 1) {
      var tempImages = resp.data.data;
      tempImages = tempImages.split(",");
      for(var i = 0 ; i < tempImages.length; i++) {
        images.value[i] = tempImages[i];
      }
      // images.value = tempImages;
    }else{
      ElMessage.error("获取背景图失败，服务器繁忙");
    }
  }
)
}
const switchTheme = () => {
  let theme = window.localStorage.getItem("theme");
  if (theme === null) {
    theme = true;
    window.localStorage.setItem("theme", JSON.stringify(theme));
  } else {
    theme = JSON.parse(theme);
  }
  setCssVariable(theme, color);
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
let color = reactive({
  header_color: '',
  main_color: '',
  main_beside_color: '',
  text_color: '',
  message_color: '',
  message_text_color: '',
  shadow_color: '',
})


//其他区
provide("user", user);
provide("color", color);

</script>

<style scoped>
.headerDiv {
  width: 100%;
  height: var(--header-height);
  position: relative;
  background-size: cover;
  background-position: center center;
  background-repeat: no-repeat;
  transition: all 1s;
}

.mainDiv {
  width: 100%;
  position: relative;
  background-color: var(--main-color);
}

.upDownIcon {
  position: absolute;
  left:50%;
  bottom: 10px;
  cursor: pointer;
  z-index:1;
}

.el-icon {
  color: #e8e8e8;
}

#leftIcon {
  position: absolute;
  top: 50%;
  left: 1px;
  padding: 10px;
}

#rightIcon {
  position: absolute;
  top: 50%;
  right: 1px;
  padding: 10px;
}

.location{
  z-index:0;
}

.el-icon:hover{
  color: var(--common-color);
  cursor: pointer;
}

.github:hover{
  cursor: pointer;
}

.outerDiv{
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  overflow: hidden;
}

.footer {
  width: 100%;
  background: #42ace8;
  color: white;
  text-align: center;
  margin-top: 400px;
  padding: 40px 0 40px 0;
  font-size: 18px;
}

</style>
