import Http from '@/utils/Http'
import ApiUrls from '@/utils/ApiUrls'


export function apiGetTables(params,fun){
    Http.post(ApiUrls.db.getTables, params)
        .then(function (res) {
            fun(res)
        })
        .catch(function (error) {
            console.log(error);
        });
}

export function apiSearchTableDetail(params,fun){
    Http.post(ApiUrls.db.searchTableDetail, params)
        .then(function (res) {
            fun(res)
        })
        .catch(function (error) {
            console.log(error);
        });
}

export function apiGenerate(params,fun){
    Http.post(ApiUrls.db.generate, params, {responseType: 'blob'})
        .then(function (res) {
            fun(res)
        })
        .catch(function (error) {
            console.log(error);
        });
}

export function apiPreview(params,fun){
    Http.post(ApiUrls.db.preview, params)
        .then(function (res) {
            fun(res)
        })
        .catch(function (error) {
            console.log(error);
        });
}

export function apiDeletedir(params,fun){
    Http.post(ApiUrls.db.deletedir, params)
        .then(function (res) {
            fun(res)
        })
        .catch(function (error) {
            console.log(error);
        });
}

export function apiGetfilecontent(params,fun){
    Http.get(ApiUrls.db.getfilecontent, params)
        .then(function (res) {
            fun(res)
        })
        .catch(function (error) {
            console.log(error);
        });
}

export function apiQueryDbTAbleInfo(params,fun){
    Http.post(ApiUrls.db.queryDbTAbleInfo, params)
        .then(function (res) {
            fun(res)
        })
        .catch(function (error) {
            console.log(error);
        });
}

export function apiQueryDbTable(params,fun){
    Http.post(ApiUrls.db.queryDbTable, params)
        .then(function (res) {
            fun(res)
        })
        .catch(function (error) {
            console.log(error);
        });
}

export function apiGetThemes(params,fun){
    Http.post(ApiUrls.db.getThemes, params)
        .then(function (res) {
            fun(res)
        })
        .catch(function (error) {
            console.log(error);
        });
}