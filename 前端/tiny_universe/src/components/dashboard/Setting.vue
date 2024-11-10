<template>
  <div class="setting">
    <el-space direction="vertical">
      <el-icon class="upDownIcon" @click="scrollToTop">
        <ArrowUpBold />
      </el-icon>
      <el-icon @click="drawer=true" class="setting_icon" style="color: var(--common-color)">
        <Setting/>
      </el-icon>
    </el-space>
    <el-drawer v-model="drawer" :size="300">
      <div>
        <p>风格：</p>
        <el-space direction="vertical">
          <el-space :size="40">
            <svg t="1718716275722" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
                 p-id="4785" width="100" height="100">
              <path
                  d="M0 0m102.4 0l819.2 0q102.4 0 102.4 102.4l0 819.2q0 102.4-102.4 102.4l-819.2 0q-102.4 0-102.4-102.4l0-819.2q0-102.4 102.4-102.4Z"
                  fill="#000000" p-id="4786"></path>
            </svg>
            <svg t="1718716275722" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
                 p-id="4785" width="100" height="100">
              <path
                  d="M0 0m102.4 0l819.2 0q102.4 0 102.4 102.4l0 819.2q0 102.4-102.4 102.4l-819.2 0q-102.4 0-102.4-102.4l0-819.2q0-102.4 102.4-102.4Z"
                  fill="#ffffff" p-id="4786"></path>
            </svg>
          </el-space>
          <el-radio-group v-model="isBlack" class="ml-4" @change="switchStyle">
            <el-space :size="90">
              <el-radio :value="true" size="large">黑夜</el-radio>
              <el-radio :value="false" size="large">白天</el-radio>
            </el-space>
          </el-radio-group>
        </el-space>
        <p>背景图：</p>
        <div class="bgImageDiv">
          <div :key="index" v-for="(image,index) in images">
            <el-upload
                class="avatar-uploader"
                :show-file-list="false"
                :http-request="updateImages(index)"
            >
              <el-image :src="image" class="bgImage"/>
            </el-upload>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>
<script setup>
import {inject, reactive, ref} from "vue";
import {setCssVariable} from "@/util/util.js";
import {doPostFile, doPostxwww} from "@/http/httpRequest.js";
import {ElMessage} from "element-plus";
import {ArrowUpBold} from "@element-plus/icons-vue";



let color = inject('color');

let isBlack = ref(JSON.parse(window.localStorage.getItem("theme")));
let drawer = ref(false);
let prop = defineProps({
  images: {
    type: Array,
    required: true
  },
  updateBgImage: {
    type: Function,
    required: true
  }
});
let images = reactive(prop.images);
let updateBgImage = prop.updateBgImage;
// TODO：传递自己的参数index的方法
const updateImages = (index) => (fileObject) => {
  const formData = new FormData();
  formData.append("image", fileObject.file)
  doPostFile('/file/uploadImg', formData).then((resp) => {
    if (resp.data.code === 1) {
      const url = resp.data.data;
      doPostxwww("/setting/background", {image: url, index: index}).then((resp) => {
        if (resp.data.code === 1) {
          images[index] = url;
          updateBgImage(index, url);
        } else {
          ElMessage.error('上传失败');
        }
      })
    } else {
      ElMessage.error('上传失败');
    }
  })
}
const scrollToTop = () => {
  window.scrollTo({
    // top: document.documentElement.offsetHeight, //回到底部
    top: 0, //回到顶部
    left: 0,
    behavior: "smooth", //smooth 平滑；auto:瞬间
  });
};
function switchStyle() {
  //当前的主题
  var theme = !isBlack.value;
  //切换为想要的主题
  theme = !theme;
  window.localStorage.setItem("theme", JSON.stringify(theme));
  setCssVariable(theme, color);
}
</script>
<style scoped>
.bgImageDiv {
  display: grid;
  grid-template: auto auto / auto auto;
  gap: 20px;
}

.setting {
  position: fixed;
  bottom: 50px;
  right: 20px;
  z-index: 11;
}

.icon {
  box-shadow: var(--shadow-white-color);
}
.upDownIcon {
  cursor: pointer;
  z-index:100;
  color: var(--common-color);
}
.bgImage {
  cursor: pointer;
  width: 100px;
  height: 100px;
}

/*旋转动画效果*/
.setting_icon {
  animation: spin 2s linear infinite;
  cursor: pointer;
  z-index: 20;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

</style>
