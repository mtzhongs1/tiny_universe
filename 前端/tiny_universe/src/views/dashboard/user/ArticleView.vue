<template>
  <UserArticleLIst :userId = "params.userId"></UserArticleLIst>
</template>
<script setup>
import {useRoute} from "vue-router";
import UserArticleLIst from "@/components/dashboard/article/UserArticleLIst.vue";
const route = useRoute();
let params = route.params;
</script>
<style scoped>
</style>
