<template>
  <button>
  <span class="button_top">{{message}}
  </span>
  </button>
</template>
<script setup>
// 父组件传递内容
let props = defineProps(['message'])
</script>
<style scoped>
button {
  /* Variables */
  --button_radius: 0.75em;
  --button_color: var(--main-beside-color);
  --button_outline_color: var(--text-color);
  font-size: 17px;
  font-weight: bold;
  border: none;
  padding: 1px;
  border-radius: var(--button_radius);
  background: var(--button_outline_color);
  cursor:pointer;
}

.button_top {
  display: block;
  box-sizing: border-box;
  border: 2px solid var(--button_outline_color);
  border-radius: var(--button_radius);
  padding: 0.75em 1.5em;
  background: var(--button_color);
  color: var(--button_outline_color);
  transform: translateY(-0.2em);
  transition: transform 0.1s ease;
}

button:hover .button_top {
  /* Pull the button upwards when hovered */
  transform: translateY(-0.33em);
}

button:active .button_top {
  /* Push the button downwards when pressed */
  transform: translateY(0);
}
</style>
