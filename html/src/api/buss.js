import Http from '@/utils/Http'
import ApiUrls from '@/utils/ApiUrls'
import {sm2,sm4} from 'sm-crypto'

let key = '46cace210248dc61d0a0a4b57115bbe8'
function encrypt(data){
    return sm4.encrypt(data, key)
}
function decrypt(data){
    return sm4.decrypt(data, key)
}

function reqParamsUpdate(params){
    params.name = encrypt(params.name)
    params.pwd = encrypt(params.pwd)
    params.url = encrypt(params.url)
}
function reqParamsOld(params){
    params.name = decrypt(params.name)
    params.pwd = decrypt(params.pwd)
    params.url = decrypt(params.url)
}

export function apiGetTables(params,fun){
    reqParamsUpdate(params)
    Http.post(ApiUrls.db.getTables, params)
        .then(function (res) {
            reqParamsOld(params)
            fun(res)
        })
        .catch(function (error) {
            console.log(error);
        });
}

export function apiSearchTableDetail(params,fun){
    reqParamsUpdate(params)
    Http.post(ApiUrls.db.searchTableDetail, params)
        .then(function (res) {
            reqParamsOld(params)
            fun(res)
        })
        .catch(function (error) {
            console.log(error);
        });
}

export function apiGenerate(params,fun){
    reqParamsUpdate(params)
    Http.post(ApiUrls.db.generate, params, {responseType: 'blob'})
        .then(function (res) {
            reqParamsOld(params)
            fun(res)
        })
        .catch(function (error) {
            console.log(error);
        });
}

export function apiPreview(params,fun){
    reqParamsUpdate(params)
    Http.post(ApiUrls.db.preview, params)
        .then(function (res) {
            reqParamsOld(params)
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
    reqParamsUpdate(params)
    Http.post(ApiUrls.db.queryDbTAbleInfo, params)
        .then(function (res) {
            reqParamsOld(params)
            fun(res)
        })
        .catch(function (error) {
            console.log(error);
        });
}

export function apiQueryDbTable(params,fun){
    reqParamsUpdate(params)
    Http.post(ApiUrls.db.queryDbTable, params)
        .then(function (res) {
            reqParamsOld(params)
            fun(res)
        })
        .catch(function (error) {
            console.log(error);
        });
}

export function apiGetThemes(fun){
    Http.post(ApiUrls.user.getThemes, null)
        .then(function (res) {
            fun(res)
        })
        .catch(function (error) {
            console.log(error);
        });
}