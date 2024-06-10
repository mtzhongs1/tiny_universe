
<template>
  <el-container  :style="{'background' : color.main_beside_color,'color' : color.text_color}">
    <el-header>
      <!-- 头像 -->
      <div class="above">
        <el-avatar :src="user.avatar" :size="100"></el-avatar>
      </div>
      <!-- 社交属性 -->
      <div class="down">

      </div>
    </el-header>
    <el-divider :style="{'background-color':color.main_beside_color}"/>
    <el-main>
      <!--TOOD: 表单对齐-->
      <div class="form-container">
        <div class="form-group" style="margin-top:50px">
          <label for="username">用户名:</label>
          <input :style="{'background' : color.main_beside_color,'color':color.text_color,'box-shadow':color.shadow_color}" type="text" id="username" v-model="user.username">
        </div>
        <div class="form-group">
          <label for="sex">性别:</label>
          <div>
            <el-radio-group v-model="user.sex" :size="20">
              <el-radio :style="{'color':color.text_color}" :value="'1'">男</el-radio>
              <el-radio :style="{'color':color.text_color}" :value="'0'">女</el-radio>
              <el-radio :style="{'color':color.text_color}" :value="'2'">其他</el-radio>
            </el-radio-group>
          </div>
        </div>
        <div class="form-group">
          <label for="avatar">头像:</label>
          <!-- element 上传图片按钮 -->
          <el-upload class="upload-demo" :limit = "1" :show-file-list="false" :http-request="uploadRequest">
            <img :style="{'box-shadow':color.shadow_color}" v-if="user.avatar" :src="user.avatar" class="avatar" style="width: 200px" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </div>
        <div class="form-group">
          <label for="birthday">生日:</label>
          <input :style="{'background' : color.main_beside_color,'color':color.text_color,'box-shadow':color.shadow_color}" type="date" id="birthday" v-model="user.birthday">
        </div>
        <div class="form-group">
          <label for="email">邮箱:</label>
          <input :style="{'background' : color.main_beside_color,'color':color.text_color,'box-shadow':color.shadow_color}" type="email" id="email" v-model="user.email" readonly>
        </div>
        <div class="form-group">
          <label for="description">个性标签:</label>
          <textarea :style="{'background' : color.main_beside_color,'color':color.text_color,'box-shadow':color.shadow_color}" style="resize: none" id="description" v-model="user.description"></textarea>
        </div>
        <div class="form-group">
          <el-button type="primary" @click="saveUser"> 提交 </el-button>
        </div>
      </div>
    </el-main>
  </el-container>

</template>

<script setup>
import {inject, reactive, ref} from "vue";
import {doPost, doPostFile, doPut} from "@/http/httpRequest.js";
import {ElMessage} from "element-plus";
import VueCropper from "vue-cropper/lib/vue-cropper.vue";

let user = inject('user');
let color = inject('color');

const uploadRequest = (options) => {
  const {file} = options;
  const formData = new FormData();
  console.log("uploadRequest");
  formData.append('image', file);
  doPostFile("/file/uploadImg",formData).then((resp) => {
    if(resp.data.code === 1){
      user.avatar = resp.data.data;
    }else{
      ElMessage.error("服务器繁忙");
    }
  });
}
function saveUser(){
  const formData = new FormData();
  formData.append("username",user.username);
  formData.append("sex",user.sex);
  formData.append("birthday",user.birthday);
  formData.append("description",user.description);
  formData.append("avatar",user.avatar);

  doPut("/user/updateMsg",formData).then((resp) => {
    if(resp.data.code === 1){
      ElMessage.success("修改成功");
    }else{
      ElMessage.error("服务器繁忙");
    }
  })
}
</script>
<style scoped>
/*:deep(.el-input__inner) {*/
/*  background: black;*/
/*}*/
.form-container {
  display: flex;
  flex-direction: column;
}

.form-group {
  display: flex;
  align-items: center;
  margin-bottom: 1em;
}

.form-group label {
  width: 120px;
}

.form-group input textarea el-upload{
  flex-grow: 1;
  margin-left: 1em;
}

:deep(.el-radio__inner) {
  width: 20px;
  height: 20px;
}
</style>
