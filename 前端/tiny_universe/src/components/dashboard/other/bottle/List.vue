<template>
  <div>
    <el-tabs class="demo-tabs" v-model="type" @tab-change="getBottles">
      <el-tab-pane label="抛出的" :name="1">
        <div v-for="(bottle,index) in bottles" :key="index" style="font-size: 20px">
          <p @click="triggerLao2(bottle)" class="bottle-title">瓶子{{index+1}} : {{bottle.title}}</p>
        </div>
      </el-tab-pane>
      <el-tab-pane label="捞到的" :name="0">
        <div v-for="(bottle,index) in bottles" :key="index" style="font-size: 20px">
          <p @click="triggerLao2(bottle)" class="bottle-title">瓶子{{index+1}} : {{bottle.title}}</p>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
  <el-dialog width="400" v-model="isAlive">
    <Lao2></Lao2>
  </el-dialog>
</template>
<script setup>
import {doGet} from "@/http/httpRequest.js";
import {ElMessage} from "element-plus";
import {onMounted, provide, ref} from "vue";
import Lao2 from "@/components/dashboard/other/bottle/Lao2.vue";

let bottles = ref();
let tbottle = ref();
let type = ref(1);
let isAlive = ref(false);
onMounted(() => {
  getBottles();
})
const triggerLao2 = (tempBot) => {
  tbottle.value = tempBot
  isAlive.value = true;

}
const getBottles = () => {
  doGet('/bottle/bottles',{type:type.value}).then((resp) => {
    if(resp.data.code === 1){
      bottles.value = resp.data.data;
    }else{
      ElMessage.error("服务器繁忙")
    }
  })
}
provide("bottle",tbottle);
</script>
<style scoped>
:deep(.el-tabs__item){
  color: #02604c;
}
.bottle-title{
  color: rgba(0, 0, 0, 0.50);
}
.bottle-title:hover{
  color: var(--common-color);
  cursor: pointer;
}
</style>
