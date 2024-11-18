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
        <div>
          <el-button
              key="primary"
              type="primary"
              text
              @click="updateArticle(article.id)"
          >
            修改
          </el-button>
          <el-button
              key="danger"
              type="danger"
              text
              @click = "deleteArticle(article.id)"
          >
            删除
          </el-button>
        </div>
      </div>
      <br>
    </el-col>
    <el-col v-if="articles.length <= 0">
      <el-empty :image-size="275" description="这里什么都没有，快来施展你的才华！" />
    </el-col>
    <el-col>
      <el-pagination style="padding-left: 45%"
                     v-model:current-page="page.pageNum"
                     :page-size="page.pageSize"
                     :size="articles.length"
                     :total = "page.total"
                     layout="total, prev, pager, next"
                     @current-change="handleCurrentChange"
                     v-show="!isEmpty(articles)"
      />
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
import {onBeforeMount, provide, reactive, ref} from 'vue';
import {doDelete, doDeletexwww, doGet, doPostxwww} from "@/http/httpRequest.js";
import {ElMessage} from "element-plus";
import ColList from "@/components/dashboard/collection/ColList.vue";
import CreateCol from "@/components/dashboard/collection/CreateCol.vue";
import {isEmpty, reloadUtil} from "@/util/util.js";
import {useRoute, useRouter} from "vue-router";

//文章数据和参数数据
let route = useRoute();
let router = useRouter();
let emit = defineEmits(['reload']);
let params = route.params;
let articles = ref([]);
let page = reactive({
  pageSize: 5,
  total: 0,
  pageNum: 1,
})
let article = ref();

let dialogVisible = ref(false);
let listIsAlive = ref(true);
onBeforeMount(() => {
  if(isEmpty(params.userId)){
    return;
  }
  getArticles();
})
//分页相关操作
const handleCurrentChange = (val) => {
  page.pageNum = val;
  getArticles();
}
const getArticles = () => {
  // 一页显示五条数据
  doGet("/article/page/", {
    pageNum: page.pageNum,
    pageSize: page.pageSize,
    userId: params.userId,
    type: 1
  }).then((resp) => {
    if (resp.data.code === 1) {
      articles.value = resp.data.data.records;
      page.total = resp.data.data.total;
      articles.value.forEach((article) => {
        const cover = isEmpty(article.cover) ? [] : article.cover.split(",");
        article.cover = cover;
      })
    } else {
      ElMessage.error("服务器繁忙");
    }
  })
}
// 文章活动属性相关操作
const doLove = (article) => {
  doPostxwww("/article_active/love", {articleId: article.id}).then((resp) => {
    if (resp.data.code === 1) {
      const isAdd = resp.data.data;
      // 点赞
      if (isAdd) {
        article.isLove = true;
        article.love++;
      }
      // 取消点赞
      else {
        article.isLove = false;
        article.love--;
      }
    } else {
      ElMessage.error("服务器繁忙");
    }
  });
}
const doCollection = (tempArt) => {
  // 取消收藏
  if (tempArt.isCollection) {
    doDeletexwww("/article_active/collection", {articleId: tempArt.id}).then((resp) => {
      if (resp.data.code === 1) {
        tempArt.isCollection = false;
        tempArt.collectionCount--;
        ElMessage.success("取消收藏");
      } else {
        ElMessage.error("服务器繁忙");
      }
    })
  }
  // 收藏
  else {
    article.value = tempArt;
    dialogVisible.value = true;
  }
}
const reloadCol = () => {
  reloadUtil(listIsAlive);
}
const closeColDialog = () => {
  reloadCol();
  dialogVisible.value = false;
}
const deleteArticle = (id) => {
  const ids = [id];
  doDelete("/article",JSON.stringify(ids)).then((resp) => {
    if(resp.data.code === 1){
      ElMessage.success("删除成功");
      emit('reload');
    }else{
      ElMessage.error("服务器繁忙");
    }
  })
}
const updateArticle = (id) => {
  router.push("/dashboard/article_editor/"+id);
}
provide("article", article);
provide('reload', reloadCol);
provide("closeColDialog", closeColDialog)
</script>
<style scoped>
.article {
  border-radius: 14.06px 14.06px 14.06px 14.06px; /* 转换 rpx 到 px */
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
  max-height: 1000px;
  overflow-x: hidden;
  overflow-y: auto;
  border-radius: 14.06px 14.06px 14.06px 14.06px; /* 转换 rpx 到 px */
  margin-bottom: 20px;
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
