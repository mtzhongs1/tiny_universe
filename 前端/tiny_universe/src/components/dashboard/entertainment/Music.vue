<template>
  <el-row :gutter="20">
    <el-col :span="24">
<!--      <MusicPlayer></MusicPlayer>-->
    </el-col>
<!--    <el-col :span="18" class="musicBox">-->
<!--      <el-button style="margin:10px;position: absolute;right: 10px">上传本地音乐</el-button>-->
<!--      <div class="musicList">-->
<!--        <div v-for="(music,index) in musicList" :key="index">-->
<!--          <span>-->
<!--            <img src="../../../assets/音乐唱片.svg" style="width: 75px; height: 75px;">-->
<!--          </span> music~-->
<!--        </div>-->
<!--      </div>-->
<!--    </el-col>-->
  </el-row>
</template>

<script setup>
import MusicPlayer from '@/components/dashboard/entertainment/MusicPlayer.vue';
import {onMounted, reactive} from "vue";

let musicList = reactive([1, 2, 3, 4, 5]);
onMounted(() => {

})

const load = () => {

}

</script>

<style scoped>
.musicList{
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.musicBox{
}
</style>

