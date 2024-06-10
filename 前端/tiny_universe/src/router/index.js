import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/login/LoginView.vue'

import Dashboard from '@/views/dashboard/Dashboard.vue'
import Home from '@/views/dashboard/Home.vue'
import Entertainment from '@/views/dashboard/Entertainment.vue'
import Moment from '@/views/dashboard/Moment.vue'
import Work from '@/views/dashboard/Work.vue'
import Collection from '@/views/dashboard/Collection.vue'
import Other from '@/views/dashboard/Other.vue'
import User from '@/views/user/User.vue'

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
        {path: 'work',name: 'Work',component:Work},
        {path: 'collection',name: 'Collection',component:Collection},
        {path: 'other',name: 'Other',component:Other},
        {path: 'user',name:'User',component:User},
      ]
    }
  ]
})

export default router
