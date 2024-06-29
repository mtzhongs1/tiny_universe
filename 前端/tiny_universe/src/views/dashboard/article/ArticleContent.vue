<template>
  <div class="outer-div">
    <div class="article-detail">
      <div class="header">
        <h1>{{ article.title }}</h1>
        <p class="meta">
          <span>{{ article.author }}</span>
          <span>{{ article.createTime }}</span>
          <span><el-icon><View /></el-icon>{{article.watch}}</span>
        </p>
      </div>
<!--      <iframe :src="article.content" id="content" onload="load()"></iframe>-->
      <article-content-view :content="article.content"></article-content-view>
    </div>
  </div>
</template>
<script setup>
import {onBeforeMount,reactive} from "vue";
import {ElMessage} from "element-plus";
import {doGet, doPostxwww} from "@/http/httpRequest.js";
import {useRoute} from "vue-router";
import ArticleContentView from "@/components/dashboard/article/ArticleContentView.vue";

let article = reactive({
  id:'',
});
const route = useRoute();
const params = route.params;
const query = route.query;
onBeforeMount(() => {
  getArticle();
})
const getArticle = async () => {
  article.id = params.articleId;
  console.log(article.id);
  const resp = await doGet("article/"+article.id)
    if(resp.data.code === 1){
      const tempArticle = resp.data.data;
      Object.assign(article,tempArticle);
    }else{
      ElMessage.error("服务器繁忙");
    }
  article.love = query.love;
  article.collectionCount = query.collectionCount;
  article.isLove = query.isLove;
  article.commentCount = query.commentCount;
  article.isCollection = query.isCollection;
  article.watch = query.watch;
  // 浏览量加一
  doPostxwww("article_active/watch",{articleId:article.id});
}
</script>
<style scoped>
.article-detail {
  max-width: 1000px;
  padding: 20px 10px;
  margin: auto;
  background: var(--main-beside-color);
}

.header h1{
  text-align: center;
}

.meta {
  color: #666;
  font-size: 0.8rem;
  text-align: center;
}

.meta span{
  margin: 10px;
}

.cover-image {
  width: 100%;
  height: auto;
}

#content {
  width: 100%;
  overflow: hidden;
  height: 100vh;
  border: none;
}
</style>
