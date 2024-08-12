<template>
</template>
<script setup>
// 提供查询当前用户文章的操作
import {ElMessage} from "element-plus";
let route = useRoute();
let params = route.params
const getArticles = (pageNum, pageSize, total, articles, type, name) => {
    // 一页显示五条数据
    doGet("/article/page",{
      userId:user.id,
      pageNum:pageNum.value,
      pageSize:pageSize.value,
      name:name
    }).then((resp) => {
      if(resp.data.code === 1){
        articles.value = resp.data.data.records;
        total.value = resp.data.data.total;
        articles.value.forEach((article) => {
          // TODO:优化
          const cover = article.cover === null || article.cover === "" ? [] : article.cover.split(",");
          article.cover=cover;
        })

      }else{
        ElMessage.error("服务器繁忙");
      }
    })
}
</script>
<style scoped>
</style>
