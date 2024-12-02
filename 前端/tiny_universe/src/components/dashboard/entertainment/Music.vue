<template>
  <el-row :gutter="20">
    <el-col :span="24">
<!--      <MusicPlayer></MusicPlayer>-->
    </el-col>
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

