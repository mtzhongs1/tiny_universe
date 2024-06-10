<template>
  <el-menu
      :default-active="activeIndex"
      class="el-menu-demo"
      mode="horizontal"
      :ellipsis="false"
      :background-color="color.header_color"
      :text-color="color.text_color"
      active-text-color="#00f0fc"
      :style="{'text-shadow':color.shadow_color}"
      router
  >
  <!--@select="handleSelect" -->
    <el-menu-item>
      <h1>{{user.username}}</h1>
    </el-menu-item>

    <div class="flex-grow" />

    <el-menu-item v-for="(menuItem,index) in menuItems" :index = "menuItem.path">
      {{menuItem.name}}
    </el-menu-item>
  </el-menu>
</template>

<script setup>
import { useRouter } from 'vue-router';
import {inject, ref} from "vue";

//TODO：监听事件
//节流函数
function throttle(func, limit) {
  let inThrottle;
  return function() {
    const args = arguments;
    if (!inThrottle) {
      func.apply(this, args);
      inThrottle = true;
      setTimeout(() => inThrottle = false, limit);
    }
  };
}
let lastScrollTop = 0;
const handleScroll = throttle(() => {
  const scrollTop = window.scrollY;
  const elMenu = document.querySelector('.el-menu');

  if (scrollTop > lastScrollTop) {
    // 向下滚动
    elMenu.classList.remove('menu-sticky');
  } else {
    // 向上滚动
    elMenu.classList.add('menu-sticky');
  }

  lastScrollTop = scrollTop;
}, 100); // 调用间隔为 100ms

window.addEventListener('scroll', handleScroll);

const menuItems = [
  {name:'首页',path:'/dashboard'},
  {name:'娱乐',path:'/dashboard/entertainment'},
  {name:'动态',path:'/dashboard/moment'},
  {name:'文章',path:'/dashboard/work'},
  {name:'收藏',path:'/dashboard/collection'},
  {name:'其他',path:'/dashboard/other'},
]
const activeIndex = menuItems[0].path;
const router = useRouter();

let user = inject('user');
let color = inject('color');
</script>

<style scoped>
.flex-grow {
  flex-grow: 1;
}
.el-menu{

  height: 60px;
}
.el-menu--horizontal.el-menu {
    border-bottom: none;
}
.menu-sticky {
  position: sticky;
  top: 0;
  z-index: 100;
}
</style>
