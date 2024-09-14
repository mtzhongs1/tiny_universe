<template>
  <div class="collection">
    <div>
      <svg t="1722880407441" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="6484" width="100" height="100"><path d="M511.9 225.8c8.3 0 15 6.7 15 15v80H812v295l79.9 80h0.1V297.4c0-31.3-25.4-56.6-56.7-56.6H606.9c0-52.5-42.5-95-95-95H227c-52.4 0-95 42.5-95 95v455l80-80v-375c0-8.3 6.7-15 15-15h284.9" fill="#1296db" p-id="6485"></path><path d="M811.9 419.4v314.9H212V419.4h599.9m32.5-80H179.5c-26.2 0-47.5 21.3-47.5 47.5v368.8c0 32.3 26.2 58.6 58.6 58.6H833.8c32.1 0 58.1-26.2 58.1-58.4v-369c0-26.2-21.3-47.5-47.5-47.5z" fill="#1296db" p-id="6486"></path><path d="M527.8 459.9l24.6 49.9c2.6 5.3 7.6 8.9 13.4 9.7l55 7.9c14.6 2.1 20.4 20 9.8 30.4l-39.9 38.7c-4.2 4.1-6.1 9.9-5.1 15.7L595 667c2.5 14.6-12.8 25.5-25.8 18.8L520 659.9c-5.2-2.7-11.4-2.7-16.6 0l-49.2 25.9c-13 6.9-28.2-4.2-25.8-18.8l9.4-54.8c0.9-5.8-0.9-11.6-5.1-15.7l-39.6-38.6c-10.5-10.3-4.7-28.2 9.8-30.4l55-7.9c5.8-0.9 10.8-4.5 13.4-9.7l24.6-49.9c6.6-13.4 25.4-13.4 31.9-0.1z" fill="#1296db" p-id="6487"></path></svg>
    </div>
    <div>
      <CreateCol></CreateCol>
      <ColList v-if="listIsAlive"></ColList>
    </div>
  </div>
</template>

<script setup>
import CreateCol from "@/components/dashboard/collection/CreateCol.vue";
import ColList from "@/components/dashboard/collection/ColList.vue";
import {provide, ref} from "vue";
import {reloadUtil} from "@/util/util.js";
let listIsAlive = ref(true);
const reload = () => {
  reloadUtil(listIsAlive);
}
provide('reload',reload);
</script>
<style scoped>

.collection {
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 20px;
  background: var(--main-beside-color);
  width: 70%;
}
</style>
