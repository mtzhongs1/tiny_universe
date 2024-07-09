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
            <el-icon :color="article.loveColor" @click="doLove(article)"><Sugar /></el-icon>{{article.love}}
          </span>
          <span>
            <el-icon><View /></el-icon>{{article.watch}}
          </span>
          <span>
            <el-icon><ChatSquare /></el-icon>{{article.commentCount}}
          </span>
          <span>
            <el-icon :color="article.collectionColor" @click="doCollection(article)"><Star /></el-icon>{{article.collectionCount}}
          </span>
        </div>

        <div v-if="isModify">
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
    <el-col style="margin-top: 20px">
      <div class="demo-pagination-block">
        <el-pagination
            v-model:current-page="pageNum"
            :page-size="pageSize"
            :size="articles.length"
            :total = "total"
            layout="total, prev, pager, next"
            @current-change="handleCurrentChange"
            v-show="articles.length > 0"
        />
      </div>
    </el-col>
    <!--为UserMessage组件隔开距离-->
    <el-col>
      <br>
    </el-col>
  </el-row>
</template>
<script setup>
import {inject, onBeforeMount, ref, watch} from 'vue';
import {doDelete, doPostxwww} from "@/http/httpRequest.js";
import {ElMessage} from "element-plus";
import {useRouter} from "vue-router";

const router = useRouter();
//父传来的数据
let user = inject('user');
//TODO:默认非必须
let props = defineProps(['reload','getArticles','name','isModify']);
const reload = props.reload;
const name = props.name;
const getArticles = props.getArticles;
const isModify = props.isModify;
//文章数据和参数数据
let articles = ref([]);
const pageSize = ref(5);
const total = ref(0);
let pageNum = ref(1);
onBeforeMount(() => {
  getArticles(pageNum,pageSize,total,articles,1,name);
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
        article.loveColor = "#62b9ec";
        article.love++;
      }
      // 取消点赞
      else{
        article.loveColor = "";
        article.love--;
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
        article.collectionColor = "#62b9ec";
        article.collectionCount++;
      }
      // 取消收藏
      else{
        article.collectionColor = "";
        article.collectionCount--;
      }
    }else{
      ElMessage.error("服务器繁忙");
    }
  })
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
  // 如果用户id不等于underfined
  getArticles(pageNum,pageSize,total,articles,1,name);
});

//
const highlightName = () => {

}

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
.activeStyle > span :hover{
  cursor: pointer;
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
</style>
