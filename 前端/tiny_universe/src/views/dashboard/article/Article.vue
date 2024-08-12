<template>
  <el-space alignment="flex-start" class="article-all" :size="50">
    <el-scrollbar>
      <div class="tag-div">
        <el-radio-group class="tag-all" border="false" v-model="onTag" size="large">
          <el-radio-button class="tag-item"
                           label="全部" :value="-1" />
          <el-radio-button class="tag-item" v-for="(tag,index) in tags" :key="index"
                           :label="tag.name" :value="tag.id" />
        </el-radio-group>
      </div>
    </el-scrollbar>
    <div class="article-list">
      <el-tabs style="width: 58vw">
        <el-tab-pane label="热门">
          <ArticleList :type="0" :getArticles="getArticles"></ArticleList>
        </el-tab-pane>
        <el-tab-pane label="最新">
          <ArticleList :type="1" :getArticles="getArticles"></ArticleList>
        </el-tab-pane>
      </el-tabs>
      <div style="position: absolute;right: 25px;top: 10px">
        <el-button @click="toArticleEditor">发布文章</el-button>
      </div>
    </div>
    <el-space class="article-beside">

    </el-space>
  </el-space>
</template>
<script setup>

// 文章相关操作
import {ElMessage} from "element-plus";
import {doGet} from "@/http/httpRequest.js";
import ArticleList from "@/components/dashboard/home/ArticleList.vue";
import {inject, onMounted, provide, ref} from "vue";
import {useRouter} from "vue-router";
import {newRoute} from "@/util/router.js";

let tags = ref([]);
let onTag = ref(-1);
let router = useRouter();
onMounted(() => {
  getTags();
})
const getArticles = (pageNum,pageSize,total,articles,type,name,tag) => {
  // 一页显示五条数据
  doGet("/article/page/",{
    pageNum:pageNum.value,
    pageSize:pageSize.value,
    type:type,
    name:name,
    tag:tag
  }).then((resp) => {
    if (resp.data.code === 1) {
      articles.value = resp.data.data.records;
      total.value = resp.data.data.total;
      articles.value.forEach((article) => {
        const cover = article.cover === null || article.cover === "" ? [] : article.cover.split(",");
        article.cover = cover;
      })
    } else {
      ElMessage.error("服务器繁忙");
    }
  })
}
const getTags = () => {
  doGet("/tag",{name:''}).then((resp) => {
    if(resp.data.code === 1){
      tags.value = resp.data.data;
    }else{
      ElMessage.error("服务器繁忙");
    }
  });
}
const toArticleEditor = () => {
  newRoute("/dashboard/article_editor/-1",router);
}
provide("tag",onTag);
</script>

<style>
.article-all{
  position: relative;
}
.tag-div{
  height: 513px;
  width: 105px;
}
.el-radio-button{
  padding: 10px 10px;
}
.el-radio-button .el-radio-button__inner{
  border: none!important;
  font-size: 20px!important;
}

.el-radio-button .el-radio-button__original-radio:checked+.el-radio-button__inner {
  background-color: rgba(234, 242, 255, 0.2);
  color: var(--common-color);
}
.tag-all{
  display: flex;
  flex-direction: column;
}
.el-scrollbar{
  margin-left: 20px;
  background: var(--main-beside-color);
  /*position: fixed*/;
}
.tag-item:hover{
  color: var(--common-color);
}
.article-tag{
  padding: 25px 25px 25px 0;
  width: 100px;
  max-height: 600px;
}
.article-list{
  padding: 10px 25px;
  position: relative;
  background: var(--main-beside-color);
}
</style>
