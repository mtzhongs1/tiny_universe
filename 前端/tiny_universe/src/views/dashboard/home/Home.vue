<template>
  <div class="common-layout">
    <el-row style="margin: 0 50px" :gutter="40">
      <el-col :md="18" :span="22">
        <ArtilceList :reload="reload" v-if="isRouterAlive"></ArtilceList>
      </el-col>
      <el-col :md="5" :span="22">
        <UserMessage :user="user" :color="color" ></UserMessage>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import {inject, nextTick, provide, ref} from "vue";
import UserMessage from "@/components/dashboard/home/UserMessage.vue";
import ArtilceList from "@/components/dashboard/home/ArticleList.vue"

let user = inject('user')
let color = inject('color')
let isRouterAlive = ref(true);
const reload = () => {
  isRouterAlive.value = false;
  nextTick(() => {
    isRouterAlive.value = true;
  });
};
</script>

<style scoped>

</style>
