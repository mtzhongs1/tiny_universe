<template>
  <!-- 个人基本信息 -->
  <div class="own-aside">
    <el-avatar @click="toUser" style="margin: 0 auto" fit="cover" :size="120" :src="user.avatar" />
    <span class="name">{{user.username}}</span>
    <span class="description" style="font-size: 14px">{{user.description}}</span>
    <div style="font-size: 15px">
      <span style="margin-right: 10px" class="UserActiveDiv" @click="toFan">
        粉丝：{{userActive.fans}}</span>
      <span class="UserActiveDiv" @click="toFol">
        关注：{{userActive.follows}}</span>
    </div>
  </div>

</template>
<script setup>
import {inject, ref} from 'vue'
import {useRouter} from "vue-router";
import {newRoute} from "@/util/router.js";
let props = defineProps(['user']);
let user = ref(props.user);
const router = useRouter();
let userActive = inject('userActive')
function toUser(){
  newRoute('/dashboard/user_detail/shuo_shuo/'+user.value.id,router);
}
const toFol = () => {
  newRoute('/dashboard/user_detail/follow/fols/'+user.value.id,router);
}
const toFan = () => {
  newRoute('/dashboard/user_detail/follow/fans/'+user.value.id,router);
}
</script>
<style scoped>
.name{
  font-size: 20px;
}

.el-avatar{
  transition: all 0.5s;
}
.el-avatar:hover {
  cursor: pointer;
  animation: spin 0.3s linear 1;
}

.own-aside{
  text-align: center;
  background: var(--main-beside-color);
  display: flex;
  flex-direction: column;
  place-items: center;
  gap: 20px;
  padding: 30px 10px;
  color: var(--text-color);
  border-radius: 14.06px 14.06px 14.06px 14.06px; /* 转换 rpx 到 px */
  filter: drop-shadow(7.03px 1.87px 13.59px rgba(0, 0, 0, 0.1));
  overflow: hidden;
}

.UserActiveDiv:hover{
  cursor:pointer;
  color:var(--common-color)
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

</style>
