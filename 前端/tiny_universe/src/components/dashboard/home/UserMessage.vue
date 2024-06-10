<template>
  <!-- 个人基本信息 -->
  <div class="own-aside" :style="{'background' : color.main_beside_color}">
    <div class="personal-detail" :style="{'color' : color.text_color}">
      <el-avatar @click="toUser" style="margin: 0 auto;" :size="100" :src="user.avatar" />
      <span class="name" :style="{'text-shadow' : color.shadow_color}">{{user.username}}</span>
      <span class="description" style="font-size: 10px">{{user.description}}</span>
    </div>
  </div>

</template>
<script setup>
import { ref } from 'vue'
import {useRouter} from "vue-router";
const props = defineProps(['user','color'])
let user = ref(props.user);
let color = ref(props.color);
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
.own-aside span,div{
  text-align: center;
}
.personal-detail{
  display: grid;
  grid-template-rows: auto auto auto;
  gap: 20px;
  padding: 40px;
}
.el-avatar{
  transition: all 0.5s;
}
.el-avatar:hover {
  cursor: pointer;
  animation: spin 1s linear infinite;
}

.oew-avatar{
  text-align: center;
}


@media (max-width:900px) {
  .own-aside{
    display: block;
  }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

</style>
