<template>
  <div class="common-layout">
    <el-row style="margin: 0 50px" :gutter="40">
      <el-col :md="16" :span="24">
        <!--TODO：必须写成函数调用的形式，不然emit不生效-->
        <MyArticleList v-if="myArticleListIsAlive" @reload="reloadMyArticleList"></MyArticleList>
     </el-col>
      <el-col :md="8" :span="24">
        <UserMessage :user="user" :color="color" ></UserMessage>
        <AiChat></AiChat>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import {inject, ref} from "vue";
import UserMessage from "@/components/dashboard/home/UserMessage.vue";

import AiChat from "@/components/dashboard/home/AiChat.vue";
import MyArticleList from "@/components/dashboard/article/MyArticleList.vue";
import {reloadUtil} from "@/util/util.js";

let user = inject('user')
let color = inject('color')
let myArticleListIsAlive = ref(true);
const reloadMyArticleList = () => {
  reloadUtil(myArticleListIsAlive);
}
</script>

<style scoped>
</style>
