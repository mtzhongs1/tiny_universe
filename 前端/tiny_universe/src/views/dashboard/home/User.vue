<template>
  <div style="padding: 50px">
    <div class="form-container">
      <!--TOOD: 表单对齐-->
      <div style="position: relative">
        <div class="form-group" style="margin-left: 50%;margin-top: 40px;">
          修改信息
        </div>
        <el-divider style="margin:50px 0 50px 0"></el-divider>
        <div class="form-group">
          <label for="username">用户名:</label>
          <input v-model="user.username" type="text" name="text" class="custom-input"/>
        </div>
        <div class="form-group">
          <label for="sex">性别:</label>
          <div>
            <el-radio-group v-model="user.sex">
              <el-radio :value="'1'">男</el-radio>
              <el-radio :value="'0'">女</el-radio>
              <el-radio :value="'2'">其他</el-radio>
            </el-radio-group>
          </div>
        </div>
        <div class="form-group">
          <label for="avatar">头像:</label>
          <AvatarCropper></AvatarCropper>
        </div>
        <div class="form-group">
          <label for="birthday">生日:</label>
          <el-date-picker
              v-model="user.birthday"
              type="date"
              placeholder="请选择日期"
          />
        </div>
        <div class="form-group">
          <label for="email">邮箱:</label>
          <input type="email" id="email" v-model="user.email" readonly class="custom-input">
        </div>
        <div class="form-group">
          <label for="description">个性标签:</label>
          <textarea style="resize: none" id="description" v-model="user.description" class="custom-input"></textarea>
        </div>
        <div class="form-group">
          <FuncButton @click="saveUser" :message="'提交'"></FuncButton>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {inject} from "vue";
import {doPut} from "@/http/httpRequest.js";
import {ElMessage} from "element-plus";
import FuncButton from "@/components/common/button/FuncButton.vue";
import AvatarCropper from "@/components/common/cropper/ImageCropper.vue";

let user = inject('user');

function saveUser() {
  if(user.username.length < 2){
    ElMessage.error("用户名长度必须大于等于2")
    return;
  }
  const formData = new FormData();
  formData.append("username", user.username);
  formData.append("sex", user.sex);
  formData.append("birthday", user.birthday);
  formData.append("description", user.description);
  formData.append("avatar", user.avatar);

  doPut("/user/updateMsg", formData).then((resp) => {
    if (resp.data.code === 1) {
      ElMessage.success("修改成功");
    } else {
      ElMessage.error("服务器繁忙");
    }
  })
}
</script>
<style scoped>
@import "@/views/css/funcInput.css";
/*:deep(.el-input__inner) {*/
/*  background: black;*/
/*}*/
.form-container {
  display: flex;
  position: relative;
  flex-direction: column;
  background: var(--main-beside-color);
  padding: 20px;
  color: var(--text-color);
  overflow: auto;

  /*align-items: center;*/
}


.form-group {
  display: flex;
  align-items: center;
  margin-bottom: 1em;
}

.form-group label {
  width: 120px;
}

.form-group div input textarea p {
  flex-grow: 1;
  margin-left: 1em;
  background: var(--main-beside-color);
  box-shadow: var(--shadow-color);
  width: 200px;
}

:deep(.el-radio__inner) {
  width: 20px;
  height: 20px;
}

:deep(.el-input__wrapper) {
  background-color: var(--main-color);
}

:deep(.el-input__inner) {
  color: var(--text-color);
}

:deep(.el-divider--horizontal) {
  border-top: 1px solid var(--text-color);
}

.el-radio {
  color: var(--text-color);
  border-color: var(--text-color);
}
</style>
