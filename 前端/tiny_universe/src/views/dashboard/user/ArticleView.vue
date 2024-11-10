<template>
  <UserArticleList v-if="myArticleListIsAlive" :userId = "params.userId" @reload="reloadMyArticleList"></UserArticleList>
</template>
<script setup>
import {useRoute} from "vue-router";
import UserArticleList from "@/components/dashboard/article/UserArticleList.vue";
import {ref} from "vue";
import {reloadUtil} from "@/util/util.js";
const route = useRoute();
let params = route.params;
let myArticleListIsAlive = ref(true);
const reloadMyArticleList = () => {
  reloadUtil(myArticleListIsAlive);
}
</script>
<style scoped>
</style>
