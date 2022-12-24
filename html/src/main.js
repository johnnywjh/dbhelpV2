import {createApp} from 'vue';
import Antd from 'ant-design-vue';
import App from './App.vue';
import 'ant-design-vue/dist/antd.dark.css';

// import 'highlight.js/styles/stackoverflow-light.css'
import 'highlight.js/styles/atom-one-dark.css'
import 'highlight.js/lib/common'
import hljsVuePlugin from "@highlightjs/vue-plugin"

const app = createApp(App);

app.use(Antd);
app.use(hljsVuePlugin);
app.mount('#app');