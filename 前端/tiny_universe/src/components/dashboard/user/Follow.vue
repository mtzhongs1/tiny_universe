<template>
  <div class="outer">
    <p>{{ isFollow ? "关注列表" : "粉丝列表" }}</p>
    <div class="menu">
      <el-menu
          class="el-menu-demo"
          mode="horizontal"
          :ellipsis="false"
          active-text-color="#409eff"
          router
          :default-active="'/dashboard/user_detail/'+userId+'/fols'"
      >
        <!--      <el-menu-item :index="user.id+'/fols'">关注列表</el-menu-item>-->
        <!--      <el-menu-item :index="user.id+'/fans'">粉丝列表</el-menu-item>-->
        <el-menu-item :index="'/dashboard/user_detail/'+userId+'/fols'" @click="isFollow=true">关注列表</el-menu-item>
        <el-menu-item :index="'/dashboard/user_detail/'+userId+'/fans'" @click="isFollow=false">粉丝列表</el-menu-item>
      </el-menu>
    </div>
    <router-view></router-view>
  </div>
</template>
<script setup>
import {inject, ref} from "vue";

let isFollow = ref(true);
let userId = inject("userId");

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
}
.menu{
  position: absolute;
  right: 0;
  top: -20px;
}

</style>
