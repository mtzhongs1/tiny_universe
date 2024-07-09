<template>
  <a href="/dashboard/article_editor/-1" target="_blank">
    <button>发布文章</button>
  </a>
  <ArticleList :getArticles="getArticles" :isModify="false"></ArticleList>
</template>
<script setup>

// 文章相关操作
import {ElMessage} from "element-plus";
import {doGet} from "@/http/httpRequest.js";
import ArticleList from "@/components/dashboard/home/ArticleList.vue";
import {inject, ref} from "vue";

let user = inject("user");

const getArticles = (pageNum,pageSize,total,articles,type,name) => {
  if(user.id !== undefined && user.id !== '') {
    // 一页显示五条数据
    doGet("/article/pageAll/" + pageNum.value + "/" + pageSize.value + "/" + type).then((resp) => {
      if (resp.data.code === 1) {
        articles.value = resp.data.data.records;
        total.value = resp.data.data.total;
        articles.value.forEach((article) => {
          const cover = article.cover === null || article.cover === "" ? [] : article.cover.split(",");
          article.cover = cover;
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

<style>
</style>
