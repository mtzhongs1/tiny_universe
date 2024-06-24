<template>
  <div>
    <div class="article-detail">
      <div class="header">
        <h1>{{ article.title }}</h1>
        <p class="meta">
          <span>作者：{{ article.author }}</span>
          <span>时间：{{ article.createTime }}</span>
        </p>
      </div>
    <!--TODO:中文乱码，在嵌入的html页面加上<meta charset="UTF-8">     -->
      <iframe :src="article.content" class="content"></iframe>
    </div>
  </div>
</template>
<script setup>
import {onBeforeMount, reactive} from "vue";
import {ElMessage} from "element-plus";
import {doGet} from "@/http/httpRequest.js";
import {useRoute} from "vue-router";

let article = reactive({
  id:'',
});
const route = useRoute();
const params = route.params;
const query = route.query;
onBeforeMount(() => {
  getArticle();
})
const getArticle = () => {
  article.id = params.articleId;
  console.log(article.id);
  doGet("article/"+article.id).then((resp) => {
    if(resp.data.code === 1){
      const tempArticle = resp.data.data;
      Object.assign(article,tempArticle);
    }else{
      ElMessage.error("服务器繁忙");
    }
  })
  article.love = query.love;
  article.collectionCount = query.collectionCount;
  article.isLove = query.isLove;
  article.commentCount = query.commentCount;
  article.isCollection = query.isCollection;
}
</script>
<style scoped>
.article-detail {
  max-width: 800px;
  margin: auto;
}

.header h1 {
  font-size: 2rem;
}

.meta {
  color: #666;
  font-size: 0.8rem;
}

.cover-image {
  width: 100%;
  height: auto;
}

.content {
  line-height: 1.6;
}

</style>
