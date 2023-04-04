const ApiUrls = {
    //https://www.jianshu.com/p/8d862d1a5ebb
    // https://blog.csdn.net/qq_37916164/article/details/127750597
    db: {
        getTables: '/api/db/getTables',
        searchTableDetail: '/api/db/searchTableDetail',
        generate: '/api/db/generate',
        preview: '/api/db/preview',
        deletedir: '/api/db/deletedir',
        getfilecontent: '/api/db/getfilecontent',
        queryDbTAbleInfo: '/api/db/queryDbTAbleInfo',
        queryDbTable: '/api/db/queryDbTable',
    },
    user: {
        getThemes: '/api/user/getThemes',
        reloadThemes: '/api/user/reloadThemes'
    }
}


export default ApiUrls