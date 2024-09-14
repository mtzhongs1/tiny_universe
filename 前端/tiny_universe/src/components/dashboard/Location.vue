<template>
  <div>
    <el-menu
        class="el-menu-demo"
        mode="horizontal"
        :ellipsis="false"
        :background-color="color.header_color"
        :text-color="color.text_color"
        active-text-color="#00f0fc"
        router
        :default-active="path"
    >

    <!--@select="handleSelect" -->
      <el-menu-item @click="toUserDetail(user.id)" id="username">
        <h1>{{user.username}}</h1>
      </el-menu-item>

      <div class="flex-grow" />

    <!-- TODO:通过emit子传父机制实现双向3数据绑定 -->
      <SearchInput @updateValue="updateValue" @search="search" ></SearchInput>

      <el-sub-menu class="small-screen">
        <template #title>菜单</template>
        <el-menu-item v-for="(menuItem,index) in menuItems" :key="index" :index="menuItem.path">
          <!--TODO：动态渲染图标-->
          <component class="location-icon" :is="menuItem.icon"></component>
          {{menuItem.name}}
        </el-menu-item>
      </el-sub-menu>

      <el-row class="large-screen">
        <el-col :span="4" v-for="(menuItem,index) in menuItems" :key="index">
          <el-menu-item :index = "menuItem.path">
            <component class="location-icon" :is="menuItem.icon"></component>
            {{menuItem.name}}
          </el-menu-item>
        </el-col>
      </el-row>
    </el-menu>
  </div>
</template>

<script setup>
import {inject, ref} from "vue";
import {newRoute} from "@/util/router.js";
import {useRoute, useRouter} from "vue-router";
import * as ElIcons from '@element-plus/icons-vue';
import SearchInput from "@/components/common/input/SearchInput.vue";
let route = useRoute();
let router = useRouter();
//查询
let content = ref('');
let path = route.path;
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
  //TODO：动态渲染图标
  {name:'首页',path:'/dashboard',icon:ElIcons['HomeFilled']},
  {name:'娱乐',path:'/dashboard/entertainment',icon:ElIcons['Camera']},
  {name:'动态',path:'/dashboard/moment',icon:ElIcons['User']},
  {name:'文章',path:'/dashboard/article',icon:ElIcons['EditPen']},
  {name:'收藏',path:'/dashboard/collection',icon:ElIcons['StarFilled']},
  {name:'其他',path:'/dashboard/other',icon:ElIcons['MoreFilled']},
]
// const activeIndex = menuItems[0].path;
// const router = useRouter();

const toUserDetail = (id) => {
  newRoute('/dashboard/user_detail/shuo_shuo/'+id,router);
}
const updateValue = (value) => {
  content.value = value;
}
const search = () => {
  if(content.value === ''){
    return;
  }
  newRoute('/dashboard/search',router,{content:content.value});
}


let user = inject('user');
let color = inject('color');
</script>

<style scoped>
.flex-grow {
  flex-grow: 1;
}
.el-menu{
  height: 60px;
  text-shadow: var(--shadow-color);
}
.el-menu--horizontal.el-menu {
  border-bottom: none;
}
.menu-sticky {
  position: sticky;
  top: 0;
  z-index: 100;
}
.location-icon{
  width:15px;

}

.small-screen{
  display: none;
}

.el-menu-item {
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
}
@media screen and (max-width: 460px){
  #username{
    display: none;
  }
  .small-screen{
    display: block;
  }
  .large-screen{
    display: none;
  }
}

</style>
