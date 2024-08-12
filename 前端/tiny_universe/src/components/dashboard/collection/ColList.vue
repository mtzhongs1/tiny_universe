<template>
<!--给一个设置按钮， 弹出对话框设置删除编辑 -->
  <div style="width: 80%;margin: 80px auto">
    <div v-if="collections && collections.length > 0">
      <el-collapse v-for="(collection,index) in collections" :key="index" @change="getChild(collection)"
                   accordion class="collapse-item">
        <el-collapse-item>
          <template #title>
            <div class="col-detail">
              <span v-if="article" style="margin: 10px 10px 0 0;">
                <el-checkbox v-model=collection.isChoosed />
              </span>
              <p>{{collection.name}}</p>
              <span v-if="collection.isPublic === 0" style="margin: 15px 0 0 2px;">
                <el-icon><Lock /></el-icon>
              </span>
              <p style="position:absolute;right: 10px">{{collection.createTime}}</p>
              <el-icon class="icon" size="20" @click="edit(collection)"><Edit /></el-icon>
              <el-icon class="icon" size="20" @click="deleteCols(collection)"><Delete /></el-icon>
            </div>
          </template>
          <el-space direction="vertical" v-if="collection.childs && collection.childs.length > 0" style="font-size: 16px">
            <el-space v-for="(child,cIndex) in collection.childs" :key="cIndex">
              <p style="cursor: pointer;" @click="toArticle(child)">{{child.name}}</p>
              <p>{{child.createTime}}</p>
            </el-space>
          </el-space>
          <div v-else style="opacity: 0.5">当前收藏夹暂无数据 Σ(°ロ°)</div>
        </el-collapse-item>
      </el-collapse>
      <el-space :size="50">
        <el-pagination
            v-model:current-page="page.pageNum"
            :page-size="page.pageSize"
            :size="collections.length"
            :total = "total"
            layout="total, prev, pager, next"
            @current-change="getCollectionos"
            style="margin:50px 0"
        />
        <el-button v-if="article" @click="doCollection">确定</el-button>
      </el-space>
    </div>
    <div v-else>
      <el-empty description="空空空空空如也~" />
    </div>
    <el-dialog
        v-model="dialogVisible"
          title="修改"
          width="300"
          center
      >
        <div style="display: flex;flex-direction: column;gap: 20px">
          <el-input v-model="collection.name" placeholder="请输入收藏夹名称" maxlength="20"></el-input>
          <el-radio-group v-model="collection.isPublic">
            <el-radio :value="1">公开</el-radio>
            <el-radio :value="0">隐私</el-radio>
          </el-radio-group>
        </div>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="dialogVisible = false">返回</el-button>
            <el-button @click="editCols">修改
            </el-button>
          </div>
        </template>
      </el-dialog>
  </div>
</template>
<script setup>
import {inject, onMounted, reactive, ref} from "vue";
import {doDelete, doGet, doPost, doPostxwww, doPut} from "@/http/httpRequest.js";
import {ElMessage} from "element-plus";
import {confirm} from "@/util/util.js";
import axios from "axios";
import {useRouter} from "vue-router";
import {newRoute} from "@/util/router.js";
let collections = ref();
let total = ref(0);
let dialogVisible = ref(false);
let closeColDialog = inject("closeColDialog");
let reload = inject("reload");
let collection = reactive({id:'',name:'',isPublic:''});
let article = inject("article");
let page = reactive({
  pageSize:5,
  pageNum:1
})
let router = useRouter();
onMounted(() => {
  getCollectionos();
})
const getCollectionos = () => {
  doGet("/collection/collections/"+page.pageSize+"/"+page.pageNum).then((resp) => {
    if(resp.data.code === 1){
      collections.value = resp.data.data.records;
      total.value = resp.data.data.total;
    }else{
      ElMessage.error("服务器繁忙");
    }
  })
}
const getChild = (collection) => {
  doGet("/collection/collection/"+page.pageSize+"/"+page.pageNum+"/"+collection.id).then((resp) => {
    if(resp.data.code === 1){
      collection.childs = resp.data.data.records;
    }else{
      ElMessage.error("服务器繁忙");
    }
  })
}
const toArticle = (tempArt) => {
  newRoute("/dashboard/article/content/"+tempArt.articleId,router);
}
const edit = (tempCols) => {
  collection.name = tempCols.name;
  collection.isPublic = tempCols.isPublic;
  collection.id = tempCols.id;
  dialogVisible.value = true;
}
const editCols = () => {
  if(collection.name === ""){
    ElMessage.error("请输入收藏夹名称");
    return;
  }
  const fd = new FormData();
  fd.append("name",collection.name);
  fd.append("isPublic",collection.isPublic);
  fd.append("id",collection.id)
  doPut("/collection",fd).then((resp) => {
    if (resp.data.code === 1){
      ElMessage.success("创建成功");
      dialogVisible.value = false;
      reload();
    }else {
      ElMessage.error(resp.data.msg);
    }
  })
}
const deleteCols = (collection) => {
  confirm().then(() => {
    doDelete("/collection/collections/"+collection.id).then((resp) => {
      if(resp.data.code === 1){
        ElMessage.success("删除成功");
        reload();
      }else{
        ElMessage.error("服务器繁忙");
      }
    })
  });
}
const doCollection = () => {
  //TODO:前端怎么传递数组作为后端参数对象的一个属性
  var choosedCols = [];
  collections.value.forEach((collection) => {
    if(collection.isChoosed){
      choosedCols.push(collection.id);
    }
  })
  const fd = new FormData();
  fd.append("articleId",article.value.id);
  fd.append("parentIds",JSON.stringify(choosedCols));
  fd.append("name",article.value.title);
  doPost("/article_active/collection",fd).then((resp) => {
    if(resp.data.code === 1){
      article.value.collectionCount++;
      article.value.isCollection = true;
      closeColDialog();
      ElMessage.success("收藏成功");
    }else{
      ElMessage.error("服务器繁忙");
    }
  })
}


</script>
<style scoped>
:deep(.el-table__body-wrapper .el-scrollbar){
  width: 100%;
}
.collapse-item :hover{
  opacity: 0.8;
  .icon{
    display: block;
  }
}
.col-detail{
  display: flex;
  width: 100%;
  position: relative;
  overflow: hidden;
  z-index: 1;
}
/* 解决标题太长问题 */
:deep(.el-collapse-item__header){
  line-height: 30px;
  height: auto;
  padding: 10px 0;
}
.icon{
  display: none;
  z-index: 2;
}

</style>
