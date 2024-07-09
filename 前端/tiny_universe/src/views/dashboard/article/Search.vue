<template>
  <el-tabs type="border-card">
    <el-tab-pane label="文章">
      <AsyncArticleList :getArticles="getArticles" :name="name" :isModify="false"></AsyncArticleList>
    </el-tab-pane>
    <el-tab-pane label="用户">专门定义一个用户组件</el-tab-pane>
  </el-tabs>

</template>
<script setup>
import {useRoute} from "vue-router";
import {defineAsyncComponent, inject, ref} from "vue";
import {doGet} from "@/http/httpRequest.js";
import {ElMessage} from "element-plus";
const AsyncArticleList = defineAsyncComponent(() => import("@/components/dashboard/home/ArticleList.vue"));

const route = useRoute();
let params = route.params;
let name = params.name;

let user = inject("user");

const getArticles = (pageNum,pageSize,total,articles,type,name) => {
  if(user.id !== undefined && user.id !== '') {
    // 一页显示五条数据
    doGet("/article/search/" + pageNum.value + "/" + pageSize.value + "/" + type+ "/" + name).then((resp) => {
      if (resp.data.code === 1) {
        //TODO:特定字符串高亮设置
        const reg = new RegExp("(" + name + ")", "g");
        articles.value = resp.data.data.records;
        total.value = resp.data.data.total;
        articles.value.forEach((article) => {
          const cover = article.cover === null || article.cover === "" ? [] : article.cover.split(",");
          article.cover = cover;
          article.content = article.content.replace(reg, "<span style='color: red'>$1</span>");
          article.title = article.title.replace(reg, "<span style='color: red'>$1</span>");
          if (article.isLove) {
            article.loveColor = "#62b9ec";
          }
          if (article.isCollection) {
            article.collectionColor = "#62b9ec";
          }
        })
      } else {
        ElMessage.error("服务器繁忙");
      }
    })
  }
}


</script>
<style scoped>
</style>
