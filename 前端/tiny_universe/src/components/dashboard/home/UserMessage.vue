<template>
  <!-- 个人基本信息 -->
  <div class="own-aside">
      <el-avatar @click="toUser" style="margin: 0 auto" fit="cover" :size="120" :src="user.avatar" />
      <span class="name">{{user.username}}</span>
      <span class="description" style="font-size: 10px">{{user.description}}</span>
  </div>

</template>
<script setup>
import { ref } from 'vue'
import {useRouter} from "vue-router";
let props = defineProps(['user']);
let user = ref(props.user);
const router = useRouter();
function toUser(){
  const routerUrl = router.resolve({
    path: '/dashboard/user',
  })
  window.open(routerUrl.href,'_blank');
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
  padding: 30px 20px;

  color: var(--text-color);
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

</style>
