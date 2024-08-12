<template>
  <div class="common-layout">
    <el-container>
      <el-header height="400">
        <div class="user-header" style="font-size: 15px">
          <div class="userMessage">
            <el-avatar fit="cover" :src="user.avatar" :size="100"></el-avatar>
            <div>
              <el-space :direction="'vertical'" alignment="normal" :size="0">
                <p style="font-size: 20px">{{user.username}}</p>
                <p style="opacity: 0.5">{{user.description}}</p>
              </el-space>
            </div>
          </div>
          <el-space :size="20">
            <span>关注: {{userActive.follows}}</span>
            <span>粉丝： {{userActive.fans}}</span>
            <div v-if="user.id === self.id">
              <el-button
                  :key="'plain'"
                  :type="'primary'"
                  text
                  @click="toUserMessage"
              > 设置 </el-button>
            </div>
            <div @click="follow" class="guan" v-else>
                <el-button v-if="!isFollow" class="follow-btn" type="primary">关注</el-button>
                <el-button v-else class="qu-guan" type="success">已关注</el-button>
            </div>
        </el-space>
        </div>
      </el-header>
      <el-main style="width: 80vw">
        <el-menu
            class="el-menu-demo"
            mode="horizontal"
            :ellipsis="false"
            active-text-color="#409eff"
            router
            :default-active="'/dashboard/user_detail/'+params.id"
        >
          <!--      <el-menu-item :index="user.id+'/fols'">关注列表</el-menu-item>-->
          <!--      <el-menu-item :index="user.id+'/fans'">粉丝列表</el-menu-item>-->
          <el-menu-item :index="'/dashboard/user_detail/'+params.id" @click="isFollow=true">动态</el-menu-item>
          <el-menu-item :index="'/dashboard/user_detail/'+params.id" @click="isFollow=false">文章</el-menu-item>
          <el-menu-item :index="'/dashboard/user_detail/'+params.id" @click="isFollow=false">收藏</el-menu-item>
          <el-menu-item :index="'/dashboard/user_detail/'+params.id" @click="isFollow=false">社交</el-menu-item>

        </el-menu>

        <el-tabs type="border-card">
          <el-tab-pane label="收藏">
            <div class="collection">
              <CreateCol></CreateCol>
              <ColList v-if="listIsAlive"></ColList>
            </div>
          </el-tab-pane>
          <el-tab-pane label="社交">
            <Follow></Follow>
          </el-tab-pane>
        </el-tabs>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>

import {inject, onMounted, provide, reactive, ref} from "vue";
import {doGet, doPost, doPostxwww} from "@/http/httpRequest.js";
import {ElMessage} from "element-plus";
import {useRoute, useRouter} from "vue-router";
import {newRoute} from "@/util/router.js";
import ArtilceList from "@/components/dashboard/home/ArticleList.vue";
import {reloadUtil} from "@/util/util.js";
import CreateCol from "@/components/dashboard/collection/CreateCol.vue";
import ColList from "@/components/dashboard/collection/ColList.vue";
import Follow from "@/components/dashboard/user/Follow.vue"
const router = useRouter();
const self = inject("user");
const route = useRoute();
const params = route.params;

let user = reactive({
  id: '',
  avatar: '',
  username: '',
  description: '',
});
let userActive = reactive({
  userId: '',
  fans:'',
  follows: ''
})

let isFollow = ref();
onMounted(() => {
  getUser();
})

const getUser = async () => {
  const resp = await doGet('/user',{userId:params.id})
  if (resp.data.code === 1) {
    Object.assign(user,resp.data.data);
  } else {
    ElMessage.error("服务器繁忙")
  }
  doGet('/user_active/'+user.id).then((resp) => {
    if (resp.data.code === 1) {
      Object.assign(userActive, resp.data.data);
    } else {
      ElMessage.error("服务器繁忙")
    }
  })
  if(self.id !== params.id){
    doGet('/user_active/isFollow/'+params.id).then((resp) => {
      if(resp.data.code === 1){
        isFollow.value = !resp.data.data;
      }else{
        ElMessage.error("服务器繁忙")
      }
    })
  }
}

const toUserMessage = () => {
  newRoute('/dashboard/user',router);
}

const follow = () => {
  doPostxwww("/user_active/follow",{
    toUserId:params.id,
    isFollow:!isFollow.value,
  }).then((resp) => {
    if(resp.data.code === 1){
      //关注成功
      if(isFollow.value){
        ElMessage.success("取关成功");
      }
      //取关成功
      else{
        ElMessage.success("关注成功");
      }
      isFollow.value = !isFollow.value;
    }else{
      ElMessage.error("服务器繁忙");
    }
  })
}

let listIsAlive = ref(true);
const reload = () => {
  reloadUtil(listIsAlive);
}
provide('userId',params.id);
provide('reload',reload);

</script>

<style scoped>
.common-layout{
  display: flex;
  flex-direction: column;
  place-items: center;
}
.userMessage{
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  gap: 20px;
}
.user-header{
  padding: 20px;
  background: var(--main-beside-color);
}
.collection {
  text-align: center;
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
