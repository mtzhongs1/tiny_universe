<template>
  <el-row class="articleDiv">
    <el-col v-for = "(article,index) in articles" :key="index" class="colDiv">
      <div class="article">
<!--        <h2 class="title" @click="toArticleContent(article)">{{article.title}}</h2>-->

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

        <div v-if="reload">
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
            v-model:current-page="pageNum"
            :page-size="pageSize"
            :size="articles.length"
            :total = "total"
            layout="total, prev, pager, next"
            @current-change="handleCurrentChange"
            v-show="articles.length > 0"
        />
    </el-col>
    <!--为UserMessage组件隔开距离-->
    <el-col>
      <br>
    </el-col>
  </el-row>
  <div>
    <el-dialog v-model="dialogVisible" title="请选择收藏夹" width="60%" center>
      <template #footer>
        <CreateCol></CreateCol>
        <ColList v-if="listIsAlive"></ColList>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import {inject, onBeforeMount, provide, ref, watch} from 'vue';
import {doDelete, doDeletexwww, doPostxwww} from "@/http/httpRequest.js";
import {ElMessage} from "element-plus";
import {useRouter} from "vue-router";
import ColList from "@/components/dashboard/collection/ColList.vue";
import CreateCol from "@/components/dashboard/collection/CreateCol.vue";
import {reloadUtil} from "@/util/util.js";

const router = useRouter();
//父传来的数据
let user = inject('user');
//TODO:默认非必须
let props = defineProps(['reload','getArticles','name','isModify','type']);
const type = props.type;
const tag = ref(inject("tag"));
const reload = props.reload;
const name = props.name;
const getArticles = props.getArticles;
//文章数据和参数数据
let articles = ref([]);
const pageSize = ref(5);
const total = ref(0);
let pageNum = ref(1);
let dialogVisible = ref(false);
let article = ref();
let listIsAlive = ref(true);
onBeforeMount(() => {
  getArticles(pageNum,pageSize,total,articles,type,name,tag.value);
  highlightName();
})
//分页相关操作
const handleCurrentChange = (val) => {
  pageNum.value = val;
  getArticles(pageNum,pageSize,total,articles,1);
}

// const toArticleContent = (article) => {
//   router.push("/dashboard/article/content/"+article.id)
// }
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
const deleteArticle = (id) => {
  const ids = [id];
  // const fd = new FormData();
  // fd.append("ids", JSON.stringify(ids));
  // console.log(fd.get("ids"))
  doDelete("/article",JSON.stringify(ids)).then((resp) => {
    if(resp.data.code === 1){
      reload();
      ElMessage.success("删除成功");
    }else{
      ElMessage.error("服务器繁忙");
    }
  })
}
const updateArticle = (id) => {
  router.push("/dashboard/article_editor/"+id);
}

// TODO：子组件获取不到父组件的user，因为父组件请求user信息是异步的，所以子组件采用监听的方式
watch(() => user.id,() => {
  getArticles(pageNum,pageSize,total,articles,type,name);
});
watch(() => tag.value,(tag) => {
  if(tag !== undefined){
    getArticles(pageNum,pageSize,total,articles,type,name,tag);
  }
})
//
const highlightName = () => {

}
const closeColDialog = () => {
  dialogVisible.value = false;
}
provide("article",article);
provide('reload',reloadCol);
provide("closeColDialog",closeColDialog)
</script>
<style scoped>
.article{
  background: var(--main-beside-color);
  padding: 20px 20px;
}
.coverStyle{
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin: 5px;
}
.activeStyle{
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin: 5px;
}
.articleDiv {
  max-height: 1000px;
  overflow-x: hidden;
  overflow-y: auto;
}
.title:hover{
  color: var(--common-color);
  cursor: pointer;
}
.title{
  color: var(--text-color);
  text-decoration: none;
}
.icon-pointer :hover{
  color: var(--common-color);
  cursor: pointer;
}
</style>
