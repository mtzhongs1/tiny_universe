<template>
  <div class="outer">
    <p>用户列表</p>
    <div class="menu">
      <el-menu
          class="el-menu-demo"
          mode="horizontal"
          :ellipsis="false"
          active-text-color="#409eff"
          router
      >
        <!--      <el-menu-item :index="user.id+'/fols'">关注列表</el-menu-item>-->
        <!--      <el-menu-item :index="user.id+'/fans'">粉丝列表</el-menu-item>-->
        <el-menu-item :index="'/dashboard/user_detail/follow/fols/'+params.id">关注列表</el-menu-item>
        <el-menu-item :index="'/dashboard/user_detail/follow/fans/'+params.id">粉丝列表</el-menu-item>
      </el-menu>
    </div>
    <router-view></router-view>
  </div>
</template>
<script setup>
import {useRoute} from "vue-router";
const route = useRoute();
let params = route.params;
</script>

<style scoped>
.folOrFan a:hover{
 color: var(--common-color);
}
.folOrFan a:focus{
  color: var(--common-color);
}
.outer{
  position: relative;
  background: var(--main-beside-color);
  display: flex;
  flex-direction: column;
  gap: 20px;
  p{
    margin: 30px;
  }
}
.menu{
  position: absolute;
  right: 0;
}

</style>
