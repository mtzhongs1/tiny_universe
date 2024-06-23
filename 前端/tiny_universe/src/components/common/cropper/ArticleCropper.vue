<template>
  <!--TODO:替换input样式  -->
  <div class="cover">
      <img @click="goFile(index)" v-for="(image,index) in images"
           :src="image" class="avatar" alt="" style="cursor: pointer"/>
    <el-icon style="cursor: pointer" v-if="index < 3" class="avatar-icon" @click="goFile">
      <Plus/>
    </el-icon>
    <input style="display:none" ref="uploadInput" type="file"
           accept="image/jpg,image/jpeg,image/webp,image/png" @change="selectFile">
  </div>
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
          :presetMode="{
      mode: 'fixedSize',
      height: 500,
    }"
          :img="tempImage"
          :options="{
      viewMode: 1,
      //防止修改剪切框大小
      dragMode: 'move',
      aspectRatio: 1,
      cropBoxResizable: false

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
import {reactive, ref} from 'vue'
import VuePictureCropper, { cropper } from 'vue-picture-cropper'
import {doPostFile} from "@/http/httpRequest.js";
import {ElMessage} from "element-plus";

const props = defineProps({
  func: {
    type: Function,
  }
})
const func = props.func;

const dialogVisible = ref(false)
let images = reactive([]);
let index = 0;
let tempImage = ref();
let curr;
let uploadInput = ref(null);
const selectFile = (event) => {
  const file = event.target.files[0];
  if (!file.type.includes('image')) {
    return;
  }
  tempImage.value = URL.createObjectURL(file);
  dialogVisible.value = true;
}

const goFile = (tempCurr) => {
  if(tempCurr >= 0 && tempCurr < images.length){
    curr = tempCurr;
  }else{
    if(index >= 3){
      ElMessage.error('封面最多三张');
      return;
    }
    curr = index;
  }

  uploadInput.value.click();
}
async function confirmImg(){
  if (!cropper) return
  // const file = await cropper.getFile();
  await cropper.getBlob().then((data) => tempImage.value = data);
  const formData = new FormData();
  formData.append("image",tempImage.value);
  doPostFile("/file/uploadImg",formData).then((resp) => {
    if(resp.data.code === 1){
      if(curr === index){
        images[index++] = resp.data.data;
        func(resp.data.data);
      }else{
        images[curr] = resp.data.data;
      }
      dialogVisible.value = false;
    }else{
      ElMessage.error("服务器繁忙");
      dialogVisible.value = false;
    }
  })
}
</script>
<style scoped>
button {
  padding: 0;border: none;cursor: pointer;
  background: var(--main-beside-color);
}
.avatar{
  width: 200px;
}
.avatar-icon{
  color: var(--text-color);
}
.el-dialog {
  background: #0fc9ee !important;
}

.cover{
  display: flex;
  align-items: center;
  gap: 100px;
}

</style>
