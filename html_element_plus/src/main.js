import {createApp} from 'vue';
import App from './App.vue';

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// import 'highlight.js/styles/stackoverflow-light.css'
import 'highlight.js/styles/atom-one-dark.css'
import 'highlight.js/lib/common'
import hljsVuePlugin from "@highlightjs/vue-plugin"

import './styles/index.scss'

const app = createApp(App);

// 全局注册复制标签
import Copy from '@/components/copy/index.vue'
app.component('copy', Copy)

app.use(ElementPlus);
app.use(hljsVuePlugin);
app.mount('#app');