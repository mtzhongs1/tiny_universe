<template>
  <div class="outerDiv">
    <!-- TODO：动态样式，注意：路径中不能有空格！！！ -->
    <div :style="{ 'background-image': `url(${bgImagePath})` }" class="headerDiv">
      <Location class="location"></Location>
      <el-icon v-show="upDown" class="arrow-icon" id="leftIcon" @click="setIndex(true)" :size="50">
        <ArrowLeftBold />
      </el-icon>
      <el-icon v-show="upDown" class="arrow-icon" id="rightIcon" @click="setIndex(false)" :size="50">
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
      <router-view style="margin: 50px auto auto auto;"></router-view>
      <Setting :images = "images" :updateBgImage="updateBgImage"></Setting>
    </div>
    <div class="footer">
      &copy; 2024 By ailu &nbsp
      <a href="https://github.com/mtzhongs1/tiny_universe" target="_blank">
        <img alt="GitHub" class="github" src="../../assets/GitHub.svg" width="30">
      </a>
    </div>
    <div class="ai-chat" @click="showDialog"></div>
    <CommonDialog :dialogVisible="dialogVisible" :close-dialog="closeDialog">
      <AiChat></AiChat>
    </CommonDialog>
  </div>
<!--  <el-button class="file-box" text>-->
<!--    <el-button @click="uploadFile"></el-button>-->
<!--  </el-button>-->
</template>
<script setup>

import * as live2d from 'live2d-render';
import AiChat from "@/components/dashboard/home/AiChat.vue";
import CommonDialog from "@/components/common/dialog/CommonDialog.vue";

// 设置消息方法
const setMessage = (msg, duration) => {
  live2d.setMessageBox(msg, duration);
};

// 弹窗显示控制
let dialogVisible = ref(false);

// 显示弹窗的方法
const showDialog = () => {
  dialogVisible.value = true;
};
// 隐藏弹窗的方法

const closeDialog = () => {
  dialogVisible.value = false;
};
// 初始化 Live2D 模型

onMounted(async () => {
  await live2d.initializeLive2D({
    BackgroundRGBA: [0.0, 0.0, 0.0, 0.0],
    ShowToolBox: true,
    ResourcesPath: '/public/miku/miku_pro_jp/runtime/miku_sample_t04.model3.json',
    CanvasSize: {
      width: 200,
      height: 200 * 450 / 250
    }
  });

  setMessage("欢迎回来！很高兴再次见到你！你想听我唱一首歌来庆祝你的回归？😄🎉🎶",3000);
});
// 提供 Live2D 消息设置方法
provide("setMessage", setMessage);

//导入区
import {onMounted, ref, reactive, provide, computed} from 'vue';
import {doGet} from '@/http/httpRequest.js'
// TODO：导入资源
import Location from '@/components/dashboard/Location.vue';
import Setting from "@/components/dashboard/Setting.vue";
import {ArrowDownBold, ArrowLeftBold, ArrowRightBold, ArrowUpBold} from "@element-plus/icons-vue";
import {isEmpty, setCssVariable, setProperty} from "@/util/util.js";
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
// let isfooter = ref(false);
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
let userActive = reactive({
  userId: '',
  fans: '',
  follows: ''
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
  // setTimeout(() => {
  //   isfooter.value = !isfooter.value;
  // },500);
}
function setIndex(isLeft) {
  index.value = isLeft
    ? (index.value - 1 + images.value.length) % images.value.length
    : (index.value + 1) % images.value.length;
}

const updateBgImage = (index,url) => {
  images[index] = url;
}

const getUser = async () => {
  const resp = await doGet("/user");
  if (resp.data.code === 1) {
    const tempUser = resp.data.data;
    Object.assign(user, tempUser);
    emailHide();
  }
  // 获取用户活动属性
  doGet("/user_active/" + user.id).then((resp) =>{
    if(resp.data.code === 1){
      const tempUserActive = resp.data.data;
      Object.assign(userActive,tempUserActive);
    }else{
      ElMessage.error("服务器繁忙");
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
  if (isEmpty(theme)) {
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
provide("userActive",userActive);

</script>

<style scoped>
.ai-chat {
  display: flex;
  color: #a5e0ec;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  z-index: 10009;
  opacity: 1;
  transition: 0.7s cubic-bezier(0.23, 1, 0.32, 1);
  position: fixed;
  right: 163px;
  top: 550px;
  width: 25px;
  height: 18px;
  cursor: pointer;
}

.close-btn:hover{
  color: var(--common-color);
}

.headerDiv {
  width: 100%;
  height: var(--header-height);
  position: relative;
  background-size: cover;
  background-position: center center;
  background-repeat: no-repeat;
  transition: all 1s;
  z-index:2;
}

.mainDiv {
  width: 100%;
  position: relative;
}

.upDownIcon {
  position: absolute;
  left:50%;
  bottom: 10px;
  cursor: pointer;
  z-index:100;
}

.el-icon {
  color: #e8e8e8;
}

#leftIcon {
  left: 1px;
}
#rightIcon {
  right: 1px;
}
.arrow-icon{
  padding: 10px;
  position: absolute;
  top: 50%;
  z-index: 15;
  color: white;
}

.location{
  z-index:5;
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
  background: var(--main-bg-color);
  background-size: cover;
  background-position: center center;
  background-repeat: no-repeat;
}

.footer {
  width: 100%;
  background: #42ace8;
  color: white;
  padding: 40px 0 40px 0;
  font-size: 18px;
  margin-top: auto;
  z-index:10;
  display: flex;
  justify-content: center;
}
.__live2d-toolbox-item {
    margin: 2px;
    padding: 2px;
    display: flex;
    height: 20px;
    width: 20px;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    font-size: 0.7rem;
    background-color: rgb(255, 149, 188);
    color: white;
    border-radius: 0.9em;
    transition: .5s ease;
}
</style>
