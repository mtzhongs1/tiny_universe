<template>
  <div class="lao-div">
    <p style="text-align: center;">
      抛瓶者：
      {{bottle.userId !== user.id ? bottle.username : user.username}}
    </p>
    <p>
      标题：{{bottle.title}}
    </p>
    <p>
      内容：{{bottle.content}}
    </p>
    <div v-if="bottle.userId !== user.id" style="text-align: center;margin-top: 10px">
      <CommonButton :message="'回信'" @click="replyAlive = true"></CommonButton>
    </div>
    <div v-else>
      <p>回信:</p>
      <div v-if="replyContents.length > 0">
        <el-space size="20" v-for="(replyContent,index) in replyContents" :key="index">
          <p>{{replyContent.username}}</p>
          <p>:</p>
          <p>{{replyContent.content}}</p>
        </el-space>
      </div>
      <div v-else>
        <p style="text-align: center;">暂无回信</p>
      </div>
    </div>
    <el-dialog v-model="replyAlive" width="400" @closed="content = ''">
      <el-input class="two-area" v-model="content" :minlength="1" :maxlength="100" show-word-limit type="textarea"></el-input>
      <div style="text-align: center;margin-top: 10px">
        <CommonButton :message="'发送'" @click="reply"></CommonButton>
      </div>
    </el-dialog>
  </div>

</template>
<script setup>
import {inject, onMounted, reactive, ref, watch} from "vue";
import {doGet, doPost} from "@/http/httpRequest.js";
import {ElMessage} from "element-plus";
import CommonButton from "@/components/common/button/CommonButton.vue";
let replyAlive = ref(false);
let bottleDiv = inject("bottleDiv");
let bottle = inject("bottle");
let content = ref('');
let user = inject("user");
let replyContents = ref([]);
onMounted(() => {
  getReply();
})
const getReply = () => {
  if(bottle.value.userId === user.id){
    doGet("/bottle/reply",{parentId:bottle.value.id}).then((resp) => {
      if(resp.data.code === 1){
        replyContents.value = resp.data.data;
      }else{
        ElMessage.error("服务器繁忙");
      }
    })
  }
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
watch(() => bottle.value,() => {
  getReply();
})
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
