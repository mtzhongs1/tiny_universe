<template>
  <!-- 个人基本信息 -->
  <div class="own-aside">
    <el-avatar @click="toUser" style="margin: 0 auto" fit="cover" :size="120" :src="user.avatar" />
    <span class="name">{{user.username}}</span>
    <span class="description" style="font-size: 10px">{{user.description}}</span>
    <div style="font-size: 15px">
      <span style="margin-right: 10px" class="UserActiveDiv">粉丝：{{userActive.fans}}</span>
      <span class="UserActiveDiv">关注：{{userActive.follows}}</span>
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
  newRoute("/dashboard/user_detail/"+user.value.id,router);
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
