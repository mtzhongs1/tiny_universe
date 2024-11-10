<template>
  <div>
    <el-icon id="leftIcon" class="arrow-icon" @click="setIndex(true)" :size="50">
      <ArrowLeftBold />
    </el-icon>
    <el-icon id="rightIcon" class="arrow-icon" @click="setIndex(false)" :size="50">
      <ArrowRightBold />
    </el-icon>
    <router-view></router-view>
  </div>
</template>

<script setup>
import {useRouter} from "vue-router";

let router = useRouter();
let index = 0;
let array = ['bottle','class_room'];
const setIndex = (isLeft) => {
  index = isLeft
      ? (index - 1 + array.length) % array.length
      : (index + 1) % array.length;
  router.push(array[index]);
}
</script>

<style scoped>
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

.arrow-icon:hover{
  color: #7ec699;
  cursor: pointer;
}
</style>
