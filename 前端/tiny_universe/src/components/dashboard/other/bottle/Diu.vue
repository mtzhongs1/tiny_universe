<template>
  <div style="color: #014452;font-size:20px;">
    <div style="margin: 5px 0 10px 150px">标题</div>
    <el-input class="one-area" v-model="bottle.title" :minlength="1" :maxlength="20" show-word-limit type="textarea"></el-input>
    <div style="margin: 5px 0 10px 150px">内容</div>
    <el-input class="two-area" v-model="bottle.content" :minlength="1" :maxlength="100" show-word-limit type="textarea"></el-input>
    <div style="margin: 10px 0;text-align: center">
      匿名 <input type="radio" v-model="bottle.isPublic" :value="'0'">
      公开 <input type="radio" v-model="bottle.isPublic" :value="'1'">
    </div>
    <el-space style="margin: 20px 0 10px 125px">
      <CommonButton :message="'抛出'" @click="throwBot"></CommonButton>
    </el-space>
  </div>

</template>
<script setup>
import {inject, reactive} from "vue";
import CommonButton from "@/components/common/button/CommonButton.vue";
import {doPost} from "@/http/httpRequest.js";
import {ElMessage} from "element-plus";

let user = inject("user");
let bottle = reactive({
  content:'',
  isPublic:0,
  title:'',
});

const throwBot = () => {
  if(!bottle.title || bottle.title === ''){
    ElMessage.error("请输入标题");
    return;
  }
  if(!bottle.content || bottle.content === ''){
    ElMessage.error("请输入内容");
    return;
  }
  const fd = new FormData();
  fd.append("content",bottle.content);
  fd.append("userId",user.id);
  fd.append("isPublic",bottle.isPublic);
  fd.append("title",bottle.title);
  doPost('/bottle',fd).then((resp) => {
    if(resp.data.code === 1){
      ElMessage.success("抛出成功");
      bottle.content = '';
      bottle.isPublic = 0;
      bottle.title = '';
    }else{
      ElMessage.error("服务器繁忙");
    }
  })
}
</script>
<style scoped>
:deep(.el-textarea__inner){
  background: white;
  color: var(--common-color);
  font-size: 16px;
}
:deep(.el-input__wrapper){
  padding: 0;
}
:deep(.el-textarea .el-input__count){
  background: transparent;
  font-size: 16px;
}
:deep(.el-input .el-input__count){
  margin: 0 10px 0 0;
}
:deep(.el-input__wrapper){
  background: white;
}
:deep(.two-area textarea){
  height: 150px;
}
:deep(.one-area textarea){
  height: 35px;
}

</style>
