<template>
<div>
  <div v-if="!isEmpty(users)">
    <div class="followers" v-for="(user,index) in users" :key="index" >
      <el-space style="margin: 20px">
        <el-avatar @click="toUser(user.id)" fit="cover" :size="80" :src="user.avatar" />
        <p>{{user.username}}</p>
      </el-space>

      <div @click="follow(user)" class="guan">
        <el-button v-if="user.isFollow" class="follow-btn" type="primary">关注</el-button>
        <el-button v-else class="qu-guan" type="success">已关注</el-button>
      </div>
    </div>
    <el-pagination style="padding-left: 45%"
                   v-model:current-page="page.pageNum"
                   :page-size="page.pageSize"
                   :size="users.length"
                   :total = "page.total"
                   layout="total, prev, pager, next"
                   @current-change="handleCurrentChange"
                   v-show="!isEmpty(users)"
    />
  </div>
  <div v-else style="height: 400px;display:flex;justify-content: center;align-items: center">
    <el-empty description="没有该用户哦~" />
  </div>
</div>
</template>
<script setup>
import {useRoute, useRouter} from "vue-router";
import {onBeforeMount, reactive, ref} from "vue";
import {doGet, doPostxwww} from "@/http/httpRequest.js";
import {ElMessage} from "element-plus";
import {isEmpty} from "@/util/util.js";
import {newRoute} from "@/util/router.js";

let route = useRoute();
let router = useRouter();
const params = route.query;
let users = ref([]);
let page = reactive({
  pageSize:10,
  total:0,
  pageNum:1,
})
onBeforeMount(() => {
  getUsers();
})
const toUser = (id) => {
  newRoute('/dashboard/user_detail/shuo_shuo/'+id,router);
}
const handleCurrentChange = (val) => {
  page.pageNum = val;
  getUsers();
}
const getUsers = () => {
  doGet("/user/search",{
    pageNum:page.pageNum,
    pageSize:page.pageSize,
    content:params.content
  }).then((resp) => {
    if(resp.data.code === 1){
      page.total = resp.data.data.total;
      users.value = resp.data.data.records;
    }else{
      ElMessage.error('服务器繁忙');
    }
  })
}
const follow = (user) => {
  doPostxwww("/user_active/follow",{
    toUserId:user.id,
    isFollow:!!user.isFollow,
  }).then((resp) => {
    if(resp.data.code === 1){
      user.isFollow = !user.isFollow;
      //关注成功
      if(user.isFollow){
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
</script>
<style scoped>
.UserDiv {
  max-height: 1000px;
  overflow-x: hidden;
  overflow-y: auto;
  border-radius: 14.06px 14.06px 14.06px 14.06px; /* 转换 rpx 到 px */
  background: var(--article-bg-color);
  margin-bottom: 20px;
}
.followers{
  position: relative;
}
.guan{
  position: absolute;
  right: 100px;
  top: 50%;
}
.qu-guan,.follow-btn{
  color: #ffffff;
}
.qu-guan:hover{
  color: #ffffff;
}
.follow-btn:hover{
  color: #ffffff;
  background: var(--common-color);
}
</style>
