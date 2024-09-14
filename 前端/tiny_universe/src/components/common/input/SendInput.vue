<template>
  <div class="search">
    <input v-model="content" @input="onInput" placeholder="和我对话..." type="search">
    <button @click="send" type="submit">Go</button>
  </div>
</template>
<script setup>
import {ref} from "vue";

let content = ref("");
let emit = defineEmits(['input']);
const onInput = (event) => {
  emit('updateValue', event.target.value)
}
const send = () => {
  emit("send");
  content.value = "";
}
</script>
<style scoped>
.search {
  position: relative;
  text-align: center;
  display: flex;
  flex-direction: column;
  place-items: center;
}

.search input {
  padding: 10px;
  border: none;
  border-radius: 20px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 10px;
  width: 90%;
}

.search input:focus {
  outline: none;
}

.search button[type="submit"] {
  background-color: #4e99e9;
  border: none;
  color: #fff;
  cursor: pointer;
  padding: 10px 20px;
  border-radius: 20px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  transition: .9s ease;
  margin-left: 10px;
}

.search button[type="submit"]:hover {
  transform: scale(1.1);
  color: rgb(255, 255, 255);
  background-color: blue;
}

</style>
