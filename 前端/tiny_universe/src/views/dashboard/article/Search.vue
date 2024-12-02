<template>
<div>
  <!--TODO: 不同组件的切换，选择下面的菜单格式-->
  <el-menu
      class="el-menu-demo"
      mode="horizontal"
      :ellipsis="false"
      active-text-color="#00f0fc"
      router
      :default-active="subItemPath[0]"
      style="margin-top: -50px"
  >
    <!--@select="handleSelect" -->
    <el-menu-item :index="subItemPath[0]" id="文章" :route="{path:subItemPath[0],query:{content:content}}">
      <h1>文章</h1>
    </el-menu-item>
    <el-menu-item :index="subItemPath[1]" id="用户" :route="{path:subItemPath[1],query:{content:content}}">
      <h1>用户</h1>
    </el-menu-item>
  </el-menu>
  <router-view :key="route.path"></router-view>
</div>
</template>
<script setup>
import {useRoute, useRouter} from "vue-router";
import {onBeforeMount} from "vue";

let route = useRoute();
let router = useRouter();

//TODO：content=xx&moduleId=xx的参数获取方法（router.resolve传参方式）
const content = route.query.content;
let subItemPath = ['/dashboard/search/article','/dashboard/search/user'];
onBeforeMount(() => {
  router.push({path:subItemPath[0],query:{content:content}})
})

</script>
<style scoped>
.article-list{
  padding: 10px 25px;
  position: relative;
  background: var(--main-beside-color);
  display: flex;
  justify-content: center;
}
</style>
