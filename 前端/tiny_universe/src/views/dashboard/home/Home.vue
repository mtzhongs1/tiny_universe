<template>
  <div class="common-layout">
    <el-row style="margin: 0 50px" :gutter="40">
      <el-col :md="18" :span="22">
        <ArtilceList :getArticles="getArticles" :reload="reload" v-if="isRouterAlive"></ArtilceList>
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
import {ElMessage} from "element-plus";
import {doGet} from "@/http/httpRequest.js";
import {reloadUtil} from "@/util/util.js";

let user = inject('user')
let color = inject('color')
let isRouterAlive = ref(true);
const reload = () => {
  reloadUtil(isRouterAlive);
}

// 提供查询当前用户文章的操作
const getArticles = (pageNum,pageSize,total,articles,type,name) => {
  if(user.id !== undefined && user.id !== ''){
    // 一页显示五条数据
    doGet("/article/page",{
      userId:user.id,
      pageNum:pageNum.value,
      pageSize:pageSize.value,
      name:name
    }).then((resp) => {
      if(resp.data.code === 1){
        articles.value = resp.data.data.records;
        total.value = resp.data.data.total;
        articles.value.forEach((article) => {
          // TODO:优化
          const cover = article.cover === null || article.cover === "" ? [] : article.cover.split(",");
          article.cover=cover;
        })

      }else{
        ElMessage.error("服务器繁忙");
      }
    })
  }
}
</script>

<style scoped>

</style>
