<template>
  <div class="common-layout">
    <el-container>
      <el-main>
        <div>
          哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈
        </div>
      </el-main>
      <el-aside width="250px">
        <UserMessage :user="user" :color="color" ></UserMessage>
      </el-aside>
    </el-container>
  </div>
</template>

<script setup>
import {inject} from "vue";
import UserMessage from "@/components/dashboard/home/UserMessage.vue";

let user = inject('user')
let color = inject('color')
</script>

<style scoped>

.el-aside{
  margin: 50px 150px 50px 0;
  box-shadow: var(--shadow-color);
}

</style>
