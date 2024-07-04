import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/login/LoginView.vue'

import Dashboard from '@/views/dashboard/Dashboard.vue'
import Home from '@/views/dashboard/home/Home.vue'
import Entertainment from '@/views/dashboard/entertainment/Entertainment.vue'
import Moment from '@/views/dashboard/moment/Moment.vue'
import Article from '@/views/dashboard/article/Article.vue'
import Collection from '@/views/dashboard/collection/Collection.vue'
import Other from '@/views/dashboard/other/Other.vue'
import User from '@/views/dashboard/home/User.vue'
import ArticleEditor from '@/views/dashboard/article/ArticleEditor.vue'
import ArticleContent from "@/views/dashboard/article/ArticleContent.vue";
import UserDetail from "@/views/dashboard/module/user/UserDetail.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'LoginView',
      component: LoginView
    },
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: Dashboard,
      children: [
        {path: '',name: 'Home',component:Home},
        {path: 'entertainment',name: 'Entertainment',component:Entertainment},
        {path: 'moment',name: 'Moment',component:Moment},
        {path: 'article',name: 'Article',component:Article},
        {path: 'collection',name: 'Collection',component:Collection},
        {path: 'other',name: 'Other',component:Other},
        {path: 'user',name:'User',component:User},
        {path: 'user_detail/:id',name:'UserDetail',component:UserDetail},
        {path: 'article_editor/:articleId',name:'ArticleEditor',component:ArticleEditor},
        {path: 'article/content/:articleId',name:'ArticleContent',component:ArticleContent},
      ]
    },
  ]
})

export default router
