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
import Search from "@/views/dashboard/article/Search.vue";
import ArticleView from "@/views/dashboard/user/ArticleView.vue";
import ShuoShuo from "@/views/dashboard/user/ShuoShuo.vue";
import Fols from "@/views/dashboard/user/follow/Fols.vue";
import Fans from "@/views/dashboard/user/follow/Fans.vue";
import Follow from "@/views/dashboard/user/Follow.vue";
import CollectionView from "@/views/dashboard/user/CollectionView.vue";
import ArticleList from "@/views/dashboard/article/ArticleList.vue";
import SearchArticle from "@/views/dashboard/search/SearchArticle.vue";
import SearchUser from "@/views/dashboard/search/SearchUser.vue";
import BottleView from "@/views/dashboard/other/BottleView.vue";
import SSEClassRoom from "@/views/dashboard/other/SSEClassRoom.vue";

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
        {
          path: 'article',
          name: 'Article',
          component:Article,
          children: [
            {path: 'list/:type',name:'ArticleList',component: ArticleList}
          ]
        },
        {path: 'collection',name: 'Collection',component:Collection},
        {
          path: 'other',
          name: 'Other',
          component: Other,
          children:[
            {path: 'bottle',name: 'bottle',component:BottleView},
            // {path: 'class_room',name: 'ClassRoom',component:ClassRoom}
            {path: 'class_room',name: 'SSEClassRoom',component:SSEClassRoom}
          ]
        },
        {path: 'user',name:'User',component:User},
        {
          path: 'user_detail',
          name:'UserDetail',
          component:UserDetail,
          children:[
            {path: 'article_view/:userId',name:'ArticleView',component:ArticleView},
            {path: 'collection_view/:id',name:'CollectionView',component:CollectionView},
            {path: 'follow',name:'Follow',component:Follow,
              children: [
                {path: 'fols/:id',name:'Fols',component:Fols},
                {path: 'fans/:id',name:'Fans',component:Fans}
              ]
            },
          ]
        },
        {path: 'article_editor/:articleId',name:'ArticleEditor',component:ArticleEditor},
        {path: 'article/content/:articleId',name:'ArticleContent',component:ArticleContent},
        {
          path: 'search',
          name:'Search',
          component:Search,
          children: [
            {path: 'article',name:'SearchArticle',component:SearchArticle},
            {path: 'user',name:'UserArticle',component:SearchUser}
          ]
        },
        {path: 'collection',name: 'Collection',component:Collection},
      ]
    },
  ]
})

export default router
