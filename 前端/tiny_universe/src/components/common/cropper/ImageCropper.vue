<template>
  <!--TODO:替换input样式  -->
  <button @click="goFile">
    <img v-if="user.avatar" :src="user.avatar" alt="" class="avatar" style="width: 200px"/>
    <el-icon v-else class="avatar-icon">
      <Plus/>
    </el-icon>
    <input ref="uploadInput" accept="image/jpg,image/jpeg,image/webp,image/png" style="display:none" type="file"
           @change="selectFile">
  </button>
  <el-dialog
      v-model="dialogVisible"
      title="图片裁剪"
      width="500"
  >
    <template #footer>
      <VuePictureCropper
          :boxStyle="{
      width: '100%',
      height: '100%',
      background: '#f8f8f8',
      margin: 'auto',
    }"
          :img="image"
          :options="{
      viewMode: 1,
      //防止修改剪切框大小
      // dragMode: 'move',
      aspectRatio: 1,
      cropBoxResizable: false

    }"
          :presetMode="{
      mode: 'round',
      width: 300,
      height: 300,
    }"
      />
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmImg">
          确认
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import {inject, ref} from 'vue'
import VuePictureCropper, {cropper} from 'vue-picture-cropper'
import {doPostFile} from "@/http/httpRequest.js";
import {ElMessage} from "element-plus";


const dialogVisible = ref(false)
let user = inject('user');
let uploadInput = ref(null);
let image = ref(user.avatar);
const selectFile = (event) => {
  const file = event.target.files[0];
  if (!file.type.includes('image')) {
    return;
  }
  image.value = URL.createObjectURL(file);
  dialogVisible.value = true;
}
const goFile = () => {
  uploadInput.value.click();
}

async function confirmImg() {
  if (!cropper) return
  // const file = await cropper.getFile();
  await cropper.getBlob().then((data) => image.value = data);
  const formData = new FormData();
  formData.append("image", image.value);
  doPostFile("/file/uploadImg", formData).then((resp) => {
    if (resp.data.code === 1) {
      user.avatar = resp.data.data;
      dialogVisible.value = false;
    } else {
      ElMessage.error("服务器繁忙");
      dialogVisible.value = false;
    }
  })
}
</script>
<style scoped>
button {
  padding: 0;
  border: none;
  cursor: pointer;
  background: var(--main-beside-color);
}

.avatar-icon {
  color: var(--text-color);
}

.el-dialog {
  background: #0fc9ee !important;
}

</style>
