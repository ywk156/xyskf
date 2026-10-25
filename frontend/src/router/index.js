import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/',
    component: () => import('../components/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '数据总览' }
      },
      {
        path: 'health',
        name: 'Health',
        component: () => import('../views/HealthRecord.vue'),
        meta: { title: '健康数据' }
      },
      {
        path: 'exercise',
        name: 'Exercise',
        component: () => import('../views/ExerciseRecord.vue'),
        meta: { title: '运动记录' }
      },
      {
        path: 'goal',
        name: 'Goal',
        component: () => import('../views/HealthGoal.vue'),
        meta: { title: '健康目标' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/Profile.vue'),
        meta: { title: '个人中心' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.path !== '/login' && to.path !== '/register' && !userStore.token) {
    next('/login')
  } else {
    next()
  }
})

export default router
