/**
 * axios 实例
 */
import axios from 'axios';
import {ElMessage} from "element-plus";

const service = axios.create({
    timeout: 5000,
});

/**
 * 添加请求拦截器
 */
// service.interceptors.request.use(
//   (config) => {
//     // 添加 token 到 header
//     const token = getToken();
//     if (token && config.headers) {
//       config.headers.common[TOKEN_HEADER_NAME] = token;
//     }
//     return config;
//   },
//   (error) => {
//     return Promise.reject(error);
//   }
// );

/**
 * 添加响应拦截器
 */
service.interceptors.response.use(
    (res) => {
        if (res.data.success) {
            return res;
        } else {
            console.log(res.data.message)
            ElMessage.error(res.data.message);
            return Promise.reject(new Error(res.data.message));
        }
    },
    (error) => {
        return Promise.reject(error);
    }
);

const Http = {
    get(url, params, _object = {}) {
        return service.get(url, {params, ..._object})
    },
    post(url, params, _object = {}) {
        return service.post(url, params, _object)
    }
}

export default Http;
