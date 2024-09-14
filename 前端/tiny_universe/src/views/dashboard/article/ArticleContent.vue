<template>
  <div class="outer-div">
    <div class="activeStyle">
      <div>
        <el-icon class="active-icon" :size="27" :color="articleActive.isLove?'#62b9ec':''" @click="doLove(article)"><Sugar /></el-icon>
        <span class="active-num">{{articleActive.love}}</span>
      </div>
      <div>
        <el-icon class="active-icon" :size="27" :color="articleActive.isCollection?'#62b9ec':''" @click="doCollection(article)"><Star /></el-icon>
        <span class="active-num">{{articleActive.collectionCount}}</span>
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
      <div class="comment-detail">
        <Comment v-if="isCommentAlive"></Comment>
      </div>
    </div>
    <div class="UserAndDir">
      <div class="UserMessage">
        <!--名字用article.author属性-->
        <div>
          <el-avatar style="vertical-align: middle;margin-right: 10px;cursor: pointer" @click="toUserDetail(article.userId)" :src="userActive.avatar" :size="70"></el-avatar>
          <span style="display: inline-block;">{{article.author}}</span>
        </div>
        <div style="opacity: 0.5;display: flex;gap: 10px;justify-content: center">
          <span>关注：{{userActive.follows}}</span>
          <span>粉丝：{{userActive.fans}}</span>
        </div>
        <div v-if="user.id !== article.userId">
          <div @click="follow" >
            <el-button style="color:white" v-if="isFollow" class="follow-btn"> 关注 </el-button>
            <el-button v-else class="follow-btn" style="background: #a19999 ;color:black"> 取关 </el-button>
          </div>
        </div>
      </div>

      <div class="directory" v-show="!isEmpty(catalog)">
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
    <el-dialog v-model="dialogVisible" title="请选择收藏夹" width="60%" center>
      <template #footer>
        <CreateCol></CreateCol>
        <ColList v-if="listIsAlive"></ColList>
      </template>
    </el-dialog>
    </div>
</template>
<script setup>
import {inject, onBeforeMount, provide, reactive, ref, watch} from "vue";
import {ElMessage} from "element-plus";
import {doDeletexwww, doGet, doPostxwww} from "@/http/httpRequest.js";
import {useRoute, useRouter} from "vue-router";

import ArticleContentView from "@/components/dashboard/article/ArticleContentView.vue";
import Comment from "@/components/dashboard/article/Comment.vue";
import {newRoute} from "@/util/router.js";
import {isEmpty, reloadUtil} from "@/util/util.js";
import ColList from "@/components/dashboard/collection/ColList.vue";
import CreateCol from "@/components/dashboard/collection/CreateCol.vue";

let user = inject("user");
let article = ref({

})
let articleActive = reactive({});
const route = useRoute();
const router = useRouter();
const params = route.params;
let isFollow = ref();
let userActive = reactive({
  userId:'',fans:'',follows:''
});
let catalog = ref([]);
let isCommentAlive = ref(true);
let dialogVisible = ref(false);
let listIsAlive = ref(true);
onBeforeMount(() => {
  getArticle();
  getUser();

})
const getUser = () => {
  doGet("article/getUser/"+params.articleId).then((resp) => {
    if(resp.data.code === 1){
      Object.assign(userActive,resp.data.data);
    }else{
      ElMessage.error("服务器繁忙");
    }
  })
}
const getArticle = async () => {

  article.value.id = params.articleId;
  const resp = await doGet("article/"+params.articleId)
  if(resp.data.code === 1){
    article.value = resp.data.data;
  }else{
    ElMessage.error("服务器繁忙");
  }

  // 浏览量加一
  doPostxwww("article_active/watch",{articleId:params.articleId});
  getArticleActive();
  getIsFollow();
}
const getArticleActive = () => {
  doGet("/article_active/"+params.articleId).then((resp) => {
    if(resp.data.code === 1){
      const tempArticleActive = resp.data.data;
      Object.assign(articleActive,tempArticleActive);
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
const reloadCol = () => {
  reloadUtil(listIsAlive);
}
const doLove = (article) => {
  doPostxwww("/article_active/love",{articleId:article.id}).then((resp) => {
    if(resp.data.code === 1){
      const isAdd = resp.data.data;
      // 点赞
      if(isAdd){
        articleActive.isLove = true;
        articleActive.love++;
      }
      // 取消点赞
      else{
        articleActive.isLove = false;
        articleActive.love--;
      }
    }else{
      ElMessage.error("服务器繁忙");
    }
  });
}
const doCollection = () => {
  // 取消收藏
  if(articleActive.isCollection){
    doDeletexwww("/article_active/collection",{articleId:params.articleId}).then((resp) => {
      if(resp.data.code === 1){
        articleActive.isCollection = false;
        articleActive.collectionCount--;
        ElMessage.success("取消收藏");
      }else{
        ElMessage.error("服务器繁忙");
      }
    })
  }
  // 收藏
  else{
    dialogVisible.value = true;
  }
}
const getIsFollow = () => {
  doGet("/user_active/isFollow/"+article.value.userId).then((resp) => {
    if(resp.data.code === 1){
      isFollow.value = resp.data.data;
    }else{
      ElMessage.error("服务器繁忙");
    }
  })
}
const reloadComment = () => {
  reloadUtil(isCommentAlive);
}
const follow = () => {
  doPostxwww("/user_active/follow",{
    toUserId:article.value.userId,
    isFollow:isFollow.value,
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
  newRoute('/dashboard/user_detail/shuo_shuo/'+id,router);
}
const closeColDialog = (collections) => {
  collections.value.forEach((collection) => {
    collection.isChoosed = false;
  })
  dialogVisible.value = false;
}
let articleId = ref(null);
watch(() => article.value.id,(newValue) => {
  articleId.value = newValue;
})

provide("articleId",articleId);
provide("article",article);
provide("reloadComment",reloadComment);
provide('reload',reloadCol);
provide("closeColDialog",closeColDialog)
</script>
<style scoped>
.outer-div{
  position: relative;
  font-family: Roboto;
  display: flex;
  font-size: 16px;
  gap:50px;
  min-width: 0;
  overflow: auto;
}

.article-detail,.comment-detail {
  padding: 20px 10px;
  background: var(--main-beside-color);
}


.activeStyle{
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-left: 100px;
  position: relative;
}

.active-num{
  background: #c2e7d1;
  border-radius: 50%;
  padding: 0 12px;
  font-size: 20px;
  color: #664c94;
  position: absolute;
  left: 40px;
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
  min-width: 250px;
  overflow: hidden;
  margin-right: 50px;
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
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 1400px;
  min-width: 1000px;
}

.follow-btn{
  background: #0184e5;
  border:none;
  width:65%
}
/*
@media screen and (max-width: 1050px){
  .article-detail,.comment-detail{
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
  .article-detail,.comment-detail{
    width: 100vw;
    position: relative;
    left: -160px;
  }
}
*/

</style>
