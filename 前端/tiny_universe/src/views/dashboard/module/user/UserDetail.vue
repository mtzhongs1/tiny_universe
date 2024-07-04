<template>

  <div class="common-layout">
    <el-container>
      <el-header height="400">
        <div class="user-header" style="font-size: 15px">
          <div class="userMessage">
            <el-avatar fit="cover" :src="user.avatar" :size="100"></el-avatar>
            <div>
              <el-space :direction="'vertical'" alignment="normal" :size="0">
                <p>{{user.username}}</p>
                <p style="opacity: 0.5">{{user.description}}</p>
              </el-space>
            </div>
          </div>
          <el-space :size="20">
            <span>关注: {{userActive.follows}}</span>
            <span>粉丝： {{userActive.fans}}</span>
            <span v-if="user.id === self.id">
              <el-button
                  :key="'plain'"
                  :type="'primary'"
                  text
                  @click="toUserMessage"
              > 设置 </el-button>
            </span>
            <span v-else>
              <el-button
                  :key="'plain'"
                  :type="'primary'"
                  text
                  @click="follow"
              > {{isFollow?'已关注':'关注'}} </el-button>
            </span>
        </el-space>
        </div>
      </el-header>
      <el-main style="width: 80vw">
        <el-tabs type="border-card">
          <el-tab-pane label="动态">动态</el-tab-pane>
          <el-tab-pane label="文章">文章</el-tab-pane>
          <el-tab-pane label="收藏">收藏</el-tab-pane>
          <el-tab-pane label="社交">社交</el-tab-pane>
        </el-tabs>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>

import {inject, onMounted, reactive, ref} from "vue";
import {doGet, doPost} from "@/http/httpRequest.js";
import {ElMessage} from "element-plus";
import {useRouter} from "vue-router";
import {newRoute} from "@/util/router.js";

const router = useRouter();
const self = inject("user");

let props = defineProps("id");
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
  const resp = await doGet('/user',{userId:props.id})
  if (resp.data.code === 1) {
    Object.assign(user,resp.data.data);
    console.log(user.avatar);
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
}

const toUserMessage = () => {
  newRoute('/dashboard/user',router);
}

const follow = () => {
  const fd = new FormData();
  fd.append("userId",self.id);
  fd.append("username",self.username);
  fd.append("avatar",self.avatar);
  fd.append("toUserId",user.id);
  fd.append("toUsername",user.username);
  fd.append("toAvatar",user.avatar);
  doPost("/user_active/follow",fd).then((resp) => {
    if(resp.data.code === 1){
      isFollow.value = !isFollow.value;
    }else{
      ElMessage.error("服务器繁忙")
    }
  })
}

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
</style>
