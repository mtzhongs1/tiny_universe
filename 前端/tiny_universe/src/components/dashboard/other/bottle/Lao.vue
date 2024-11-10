<template>
  <div v-if="!isEmpty(bottle)" class="lao-div">
    <p style="text-align: center;">
      抛瓶者：
      {{bottle.username}}
    </p>
    <p>
      标题：{{bottle.title}}
    </p>
    <p>
      内容：{{bottle.content}}
    </p>
    <div style="text-align: center;margin-top: 10px">
      <CommonButton :message="'回信'" @click="replyAlive = true"></CommonButton>
    </div>
    <el-dialog v-model="replyAlive" width="400" @closed="content = ''">
      <el-input class="two-area" v-model="content" :minlength="1" :maxlength="100" show-word-limit type="textarea"></el-input>
      <div style="text-align: center;margin-top: 10px">
        <CommonButton :message="'发送'" @click="reply"></CommonButton>
      </div>
    </el-dialog>
  </div>
  <div v-else style="text-align: center;color: var(--common-color)">
    已经没有漂流瓶了
  </div>

</template>
<script setup>
import {inject, onMounted, reactive, ref} from "vue";
import {doGet, doPost} from "@/http/httpRequest.js";
import {ElMessage} from "element-plus";
import CommonButton from "@/components/common/button/CommonButton.vue";
import {isEmpty} from "@/util/util.js";
let replyAlive = ref(false);
let bottleDiv = inject("bottleDiv");
let bottle = ref(null);
let content = ref('');
let user = inject("user");
onMounted(() => {
  getBottle();
})
const getBottle = () =>{
  doGet("/bottle").then((resp) => {
    if(resp.data.code === 1){
      //获取标题，内容
      // bottle.value = resp.data.data;
      bottle.value = resp.data.data;
    }else{
      ElMessage.error("服务器繁忙");
    }
  })
}
const reply = () => {
  if(content.value === ''){
    ElMessage.error("请输入内容");
    return;
  }
  const fd = new FormData();
  fd.append("content",content.value);
  fd.append("userId",user.id);
  fd.append("parentId",bottle.value.id);
  fd.append("isPublic",1);
  doPost("/bottle",fd).then((resp) => {
    if(resp.data.code === 1){
      ElMessage.success("发送成功");
      replyAlive.value = false;
      bottleDiv.alive = false;
    }else{
      ElMessage.error("服务器繁忙");
    }
  })
}
</script>
<style scoped>
.lao-div{
  padding: 20px;
  color: #014452;
  font-size: 16px;
  border: none;
  border-radius: 10px;
}
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
:deep(.el-textarea.is-disabled .el-textarea__inner){
  background: white;
}
:deep(.two-area textarea){
  height: 130px;
}

</style>
