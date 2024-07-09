<template>
  <div class="outer-div">
    <div>
      <div class="activeStyle">
        <div>
          <el-icon class="active-icon" :size="27" :color="articleActive.loveColor" @click="doLove(article)"><Sugar /></el-icon>
          <span class="active-num">{{articleActive.love}}</span>
        </div>
        <div>
          <el-icon class="active-icon" :size="27" :color="articleActive.collectionColor" @click="doCollection(article)"><Star /></el-icon>
          <span class="active-num">{{articleActive.collectionCount}}</span>
        </div>
      </div>
    </div>
    <div class="center-div">
      <div class="article-detail">
        <div class="header">
          <h1>{{ article.title }}</h1>
          <p class="meta">
            <span>{{ article.author }}</span>
            <span>{{ article.createTime }}</span>
            <span><el-icon><View /></el-icon>{{articleActive.watch}}</span>
          </p>
        </div>
        <article-content-view :makeDirectory="makeDirectory" :content="article.content"></article-content-view>
      </div>
      <!--评论区域-->
      <div>
        <Comment></Comment>
      </div>
    </div>
    <div>
      <div class="UserAndDir">
        <div class="UserMessage">
          <!--名字用article.author属性-->
          <div>
            <el-avatar style="vertical-align: middle;margin-right: 10px" @click="toUserDetail(article.userId)" :src="userActive.avatar" :size="70"></el-avatar>
            <span style="display: inline-block;">{{article.author}}</span>
          </div>
          <div style="opacity: 0.5;display: flex;gap: 10px;justify-content: center">
            <span>关注：{{userActive.follows}}</span>
            <span>粉丝：{{userActive.fans}}</span>
          </div>
          <div v-if="user.id === article.userId">
            <div @click="follow" >
              <el-button style="color:white" v-if="isFollow" class="follow-btn"> 关注 </el-button>
              <el-button v-else class="follow-btn" style="background: #a19999 ;color:black"> 取关 </el-button>
            </div>
          </div>
        </div>

        <div class="directory" v-show="catalog.length > 0">
          <h3 style="text-align: center;">目录</h3>
          <el-anchor :offset="70">
            <!--通过id选择器指定锚点-->
            <el-anchor-link v-for="(item, index) in catalog"
                            :key="index" :href="'#' + item.id">
              <span :style="{'margin-left':`${item.level}px`}"></span>
              {{item.title}}
            </el-anchor-link>
          </el-anchor>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import {inject, onBeforeMount, reactive, ref} from "vue";
import {ElMessage} from "element-plus";
import {doGet, doPostxwww} from "@/http/httpRequest.js";
import {useRoute, useRouter} from "vue-router";

import ArticleContentView from "@/components/dashboard/article/ArticleContentView.vue";
import Comment from "@/components/dashboard/article/Comment.vue";
import {newRoute} from "@/util/router.js";

let user = inject("user");
let article = reactive({
  id:'',
});
let articleActive = reactive({});
const route = useRoute();
const router = useRouter();
const params = route.params;
let isFollow = ref();
let userActive = reactive({
  userId:'',fans:'',follows:''
});
let catalog = ref([]);
onBeforeMount(() => {
  getArticle();
  getUser();

})
const getUser = () => {
  doGet("article/getUser/"+article.id).then((resp) => {
    if(resp.data.code === 1){
      console.log(resp.data.data);
      Object.assign(userActive,resp.data.data);
    }else{
      ElMessage.error("服务器繁忙");
    }
  })
}
const getArticle = async () => {
  article.id = params.articleId;
  const resp = await doGet("article/"+article.id)
    if(resp.data.code === 1){
      const tempArticle = resp.data.data;
      Object.assign(article,tempArticle);
    }else{
      ElMessage.error("服务器繁忙");
    }
  // 浏览量加一
  doPostxwww("article_active/watch",{articleId:article.id});
  getArticleActive();
  getIsFollow();
}
const getArticleActive = () => {
  doGet("/article_active/"+params.articleId).then((resp) => {
    if(resp.data.code === 1){
      const tempArticleActive = resp.data.data;
      Object.assign(articleActive,tempArticleActive);
      if(articleActive.isLove){
        articleActive.loveColor = "#62b9ec";
      }
      if(articleActive.isCollection){
        articleActive.collectionColor = "#62b9ec";
      }
    }
    else{
      ElMessage.error("服务端繁忙");
    }
  })
}
const makeDirectory = (contentRef) => {
  const nodes = ["H1","H2","H3","H4","H5","H6"];
  let titles = [];
  contentRef.value.childNodes.forEach((e, index) => {
    if (nodes.includes(e.nodeName)) {
      const id = "header-" + index;
      e.setAttribute("id", id);
      titles.push({
        id: id,
        title: e.innerHTML,
        level: Number(e.nodeName.substring(1, 2))*10,
        nodeName: e.nodeName
      });
    }
    catalog.value = titles;
  });
}

const doLove = (article) => {
  doPostxwww("/article_active/love",{articleId:article.id}).then((resp) => {
    if(resp.data.code === 1){
      const isAdd = resp.data.data;
      // 点赞
      if(isAdd){
        articleActive.loveColor = "#62b9ec";
        articleActive.love++;
      }
      // 取消点赞
      else{
        articleActive.loveColor = "";
        articleActive.love--;
      }
    }else{
      ElMessage.error("服务器繁忙");
    }
  });
}
const doCollection = (article) => {
  doPostxwww("/article_active/collection",{articleId:article.id}).then((resp) => {
    if(resp.data.code === 1){
      const isAdd = resp.data.data;
      // 收藏
      if(isAdd){
        articleActive.collectionColor = "#62b9ec";
        articleActive.collectionCount++;
      }
      // 取消收藏
      else{
        articleActive.collectionColor = "";
        articleActive.collectionCount--;
      }
    }else{
      ElMessage.error("服务器繁忙");
    }
  })
}
const getIsFollow = () => {
  doGet("/user_active/isFollow/"+article.userId).then((resp) => {
    if(resp.data.code === 1){
      isFollow.value = resp.data.data;
    }else{
      ElMessage.error("服务器繁忙");
    }
  })
}

const follow = () => {
  doPostxwww("/user_active/follow",{
    toUserId:article.userId,
    isFollow:!isFollow.value,
  }).then((resp) => {
    if(resp.data.code === 1){
      isFollow.value = !isFollow.value;
      //关注成功
      if(isFollow.value){
        ElMessage.success("取关成功");
      }
      //取关成功
      else{
        ElMessage.success("关注成功");
      }
    }else{
      ElMessage.error("服务器繁忙");
    }
  })
}
const toUserDetail = (id) => {
  newRoute('/dashboard/user_detail/'+id,router);
}
</script>
<style scoped>
.outer-div{
  position: relative;
  font-family: Roboto;
  display: flex;
  font-size: 16px;
  gap:60px;
}

.article-detail {
  width: 60vw;
  padding: 20px 10px;
  background: var(--main-beside-color);
}

.activeStyle{
  display: flex;
  flex-direction: column;
  gap: 20px;
  position: fixed;
  margin: 0 10px;
  top:30%;
}

.active-num{
  background: #c2e7d1;
  border-radius: 50%;
  padding: 0 12px;
  font-size: 20px;
  color: #0d5b0d;
  position: absolute;
  left: 50px;
}

.active-icon{
  background: var(--main-beside-color);
  border-radius: 50%;
  padding: 20px;
  cursor: pointer;
}

.active-icon :hover{
  color: var(--common-color);
}

.UserAndDir{
  overflow: auto;
  position: fixed;
  width: 230px;
}

.UserMessage{
  text-align: center;
  font-size: 18px;
  padding: 20px 10px;
  background: var(--main-beside-color);
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 10px;
}

.directory{
  color: var(--text-color);
  background: var(--main-beside-color);
  padding: 20px 0;
  font-size: 18px;
  /*max-height: 200px;*/
  overflow: auto;
  max-height:300px;
}

:deep(.el-anchor__link) {
  font-size: 18px;
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

.center-div{
  margin-left: 40px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.follow-btn{
  background: #0184e5;
  border:none;
  width:65%
}

@media screen and (max-width: 1050px){
  .UserAndDir{
    display: none;
  }
  .article-detail{
    width: 85vw;
  }
}

@media screen and (max-width: 800px){
  .activeStyle{
    display: none;
  }
  .outer-div{
    justify-content: center;
  }
}
</style>
