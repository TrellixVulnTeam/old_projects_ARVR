import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    alias: '/upload',
    name: 'Data Upload',
    component: () => import('../views/DataUpload')
  },
  {
    path: '/search',
    name: 'Data Search',
    component: () => import('../views/DataSearch')
  },
]

const router = createRouter({
  mode: 'history',
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
