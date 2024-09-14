<template>
  <el-row class="articleDiv">
    <el-col v-for = "(article,index) in articles" :key="index" class="colDiv">
      <div class="article">
        <router-link class="title" :to="{
         path:'/dashboard/article/content/'+article.id,
        }">
          <p v-html="article.title"></p>
        </router-link>

        <p style="opacity: 0.4;font-size: 18px">发表于{{article.createTime}}</p>
        <p v-if="article.description" v-html="article.description"></p>
        <p v-else v-html="article.content"></p>
        <div class="coverStyle">
          <img style="height: 200px" v-for = "(cover,index) in article.cover" :key="index" :src="cover" alt=""/>
        </div>
        <div class="activeStyle">
          <span>
            <el-icon class="icon-pointer" :color="article.isLove?'#62b9ec':''" @click="doLove(article)"><Sugar /></el-icon>{{article.love}}
          </span>
          <span>
            <el-icon><View /></el-icon>{{article.watch}}
          </span>
          <span>
            <el-icon><ChatSquare /></el-icon>{{article.commentCount}}
          </span>
          <span>
            <el-icon class="icon-pointer" :color="article.isCollection?'#62b9ec':''" @click="doCollection(article)"><Star /></el-icon>{{article.collectionCount}}
          </span>
        </div>
      </div>
      <br>
    </el-col>
    <el-col v-if="articles.length <= 0">
      <el-empty :image-size="275" description="这里什么都没有，快来施展你的才华！" />
    </el-col>
    <el-col>
<!--        <el-pagination style="padding-left: 45%"-->
<!--            v-model:current-page="page.pageNum"-->
<!--            :page-size="page.pageSize"-->
<!--            :size="articles.length"-->
<!--            :total = "page.total"-->
<!--            layout="total, prev, pager, next"-->
<!--            @current-change="handleCurrentChange"-->
<!--            v-show="!isEmpty(articles)"-->
<!--        />-->
      <div v-if="loading" class="loading-spinner">Loading...</div>
    </el-col>
    <!--为UserMessage组件隔开距离-->
    <el-col>
      <br>
    </el-col>
  </el-row>
  <div>
    <el-dialog @close="closeColDialog" v-model="dialogVisible" title="请选择收藏夹" width="60%" center>
      <template #footer>
        <CreateCol></CreateCol>
        <ColList v-if="listIsAlive"></ColList>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import {inject, onBeforeMount, onUnmounted, provide, reactive, ref, watch} from 'vue';
import {doDeletexwww, doGet, doPostxwww} from "@/http/httpRequest.js";
import {ElMessage} from "element-plus";
import {useRoute} from "vue-router";
import ColList from "@/components/dashboard/collection/ColList.vue";
import CreateCol from "@/components/dashboard/collection/CreateCol.vue";
import {isEmpty, reloadUtil} from "@/util/util.js";

const route = useRoute();
const params = route.params;
const tag = ref(inject("tag"));
let props = defineProps(['reloadArticleList']);
//文章数据和参数数据
let articles = ref([]);
let page = reactive({
  pageSize:10,
  total:0,
  pageNum:1,
})
let article = ref();
let dialogVisible = ref(false);
let listIsAlive = ref(true);
let loading = ref(false);
onBeforeMount(() => {
  window.addEventListener('scroll', handleScroll);
  getArticles();
})
onUnmounted(() => {
  window.removeEventListener('scroll',handleScroll);
})
//分页相关操作
const handleScroll = () => {
  if (
      window.innerHeight + document.documentElement.scrollTop !==
      document.documentElement.offsetHeight
  ) {
    return;
  }
  loading.value = true;
  getArticles();
}
const handleCurrentChange = (val) => {
  page.pageNum = val;
  getArticles();
}
const getArticles = () => {
  // 一页显示五条数据
  doGet("/article/page/",{
    pageNum:page.pageNum,
    pageSize:page.pageSize,
    type:params.type,
    tag:tag.value
  }).then((resp) => {
    if (resp.data.code === 1) {
      loading.value = false;
      articles.value = resp.data.data.records;
      page.total = resp.data.data.total;
      articles.value.forEach((article) => {
        const cover = isEmpty(article.cover) ? [] : article.cover.split(",");
        article.cover = cover;
      })
      page.pageNum++;
    } else {
      ElMessage.error("服务器繁忙");
    }
  })
}
// 文章活动属性相关操作
const doLove = (article) => {
  doPostxwww("/article_active/love",{articleId:article.id}).then((resp) => {
    if(resp.data.code === 1){
      const isAdd = resp.data.data;
      // 点赞
      if(isAdd){
        article.isLove = true;
        article.love++;
      }
      // 取消点赞
      else{
        article.isLove = false;
        article.love--;
      }
    }else{
      ElMessage.error("服务器繁忙");
    }
  });
}
const doCollection = (tempArt) => {
  // 取消收藏
  if(tempArt.isCollection){
    doDeletexwww("/article_active/collection",{articleId:tempArt.id}).then((resp) => {
      if(resp.data.code === 1){
        tempArt.isCollection = false;
        tempArt.collectionCount--;
        ElMessage.success("取消收藏");
      }else{
        ElMessage.error("服务器繁忙");
      }
    })
  }
  // 收藏
  else{
    article.value = tempArt;
    dialogVisible.value = true;
  }
}
const reloadCol = () => {
  reloadUtil(listIsAlive);
}
watch(() => tag.value,(tag) => {
  if(!isEmpty(tag)){
    props.reloadArticleList();
  }
})
const closeColDialog = () => {
  reloadCol();
  dialogVisible.value = false;
}
provide('reload',reloadCol);
provide("closeColDialog",closeColDialog);
provide("article",article);
</script>
<style scoped>
.loading-spinner {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 50px;
  /* 其他样式... */
}

.article {
  background: var(--main-beside-color);
  padding: 20px 20px;
}

.coverStyle {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin: 5px;
}

.activeStyle {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin: 5px;
}

.articleDiv {
  border-radius: 14.06px 14.06px 14.06px 14.06px; /* 转换 rpx 到 px */
  background: var(--main-beside-color);
}

.title:hover {
  color: var(--common-color);
  cursor: pointer;
}

.title {
  color: var(--text-color);
  text-decoration: none;
}

.icon-pointer :hover {
  color: var(--common-color);
  cursor: pointer;
}
</style>
