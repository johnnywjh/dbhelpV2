import {createRouter, createWebHistory} from 'vue-router';
import Index from './view/index.vue';
import Tpl from './view/tpl.vue';
import globalMenu from './view/global/global_menu.vue';

const routes = [
    {name: 'index', path: '/', component: Index}
    , {name: 'tpl', path: '/tpl', component: Tpl}
    , {name: 'menu', path: '/menu', component: globalMenu}
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;