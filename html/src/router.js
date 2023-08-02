import { createRouter, createWebHistory } from 'vue-router';
import Index from './view/index.vue';
import Tpl from './view/tpl.vue';

const routes = [
    { path: '/', component: Index },
    { path: '/tpl', component: Tpl }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;