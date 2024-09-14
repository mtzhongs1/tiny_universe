<template>
  <div class="create-all">
    <span class="createBtn" @click="dialogVisible = true">
      + 创建收藏夹
    </span>
    <el-dialog
        v-model="dialogVisible"
        title="创建收藏夹"
        width="300"
        center
    >
      <div style="display: flex;flex-direction: column;gap: 20px">
        <el-input v-model="collection.name" placeholder="请输入收藏夹名称" maxlength="20"></el-input>
        <el-radio-group v-model="collection.isPublic">
          <el-radio :value="1">公开</el-radio>
          <el-radio :value="0">隐私</el-radio>
        </el-radio-group>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">返回</el-button>
          <el-button @click="createCollections">创建
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import {ElMessage} from "element-plus";
import {doPost} from "@/http/httpRequest.js";
import {inject, reactive, ref} from "vue";
import FuncButton from "@/components/common/button/FuncButton.vue";
let user = inject("user");
let reload = inject("reload");
let userId = inject("userId");
let collection = reactive({
  isPublic:1,
  name:""
})
let dialogVisible = ref(false);
const createCollections = () => {
  if(collection.name === ""){
    ElMessage.error("请输入收藏夹名称");
    return;
  }
  const fd = new FormData();
  fd.append("name",collection.name);
  fd.append("isPublic",collection.isPublic);
  fd.append("userId",user.id);
  doPost("/collection/collections",fd).then((resp) => {
    if (resp.data.code === 1){
      ElMessage.success("创建成功");
      collection.name = "";
      dialogVisible.value = false;
      reload();
    }else {
      ElMessage.error(resp.data.msg);
    }
  })
}
</script>
<style scoped>
.create-all{
  position: relative;
}
.createBtn{
  color: var(--common-color);
  cursor: pointer;
}

</style>
