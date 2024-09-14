<template>
  <div class="collection">
    <div>
      <CreateCol v-if="user.id == userId" style="margin-top: 50px"></CreateCol>
      <ColList v-if="listIsAlive"></ColList>
    </div>
  </div>
</template>

<script setup>
import CreateCol from "@/components/dashboard/collection/CreateCol.vue";
import ColList from "@/components/dashboard/collection/ColList.vue";
import {provide, ref, inject} from "vue";
import {reloadUtil} from "@/util/util.js";
let listIsAlive = ref(true);
let userId = inject('userId');
let user = inject('user');
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
}
</style>
