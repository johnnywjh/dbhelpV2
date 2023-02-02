import {useLocalStorage} from '@vueuse/core'

/* 数据库的key*/
let datakey_db = "dbsource";
let datakey_user = "user";
let datakey_tables = "tables";
let opLeyerMsgTime = 1000;

let DbData = {
    setDb(db) {
        localStorage.setItem(datakey_db, JSON.stringify(db))
    },
    getDb() {
        let valueStr = localStorage.getItem(datakey_db)
        return valueStr ? JSON.parse(valueStr) : null
    },
    cleanDb(key) {
        localStorage.removeItem(datakey_tables)
    },
    /** 保存用户信息 */
    setUser(user) {
        localStorage.setItem(datakey_user, JSON.stringify(user))
    },
    /** 获取用户信息 */
    getUser() {
        let valueStr = localStorage.getItem(datakey_user)
        return valueStr ? JSON.parse(valueStr) : null
    },
    /** 保存某一个数据库的表的集合 */
    setTables(tableName, tableList) {
        var tableMap = getTablesMap();
        tableMap = tableMap || {};
        tableMap[tableName] = tableList;
        localStorage.setItem(datakey_tables, JSON.stringify(tableMap))
    },
    /** 获取这个库的表的集合 */
    getTables(tableName) {
        var tableMap = getTablesMap();
        return tableMap ? tableMap[tableName] : null
    },
    /** 获取表的详情 */
    getTableObj(key, tableName) {
        // var list = this.getTables(key);
        // if (!list) {
        //     return null;
        // }
        // var obj = null
        // for (let l of list) {
        //     if (l.tableName == tableName) {
        //         obj = l
        //         break;
        //     }
        // }
        // return obj
    },
    setTablesDetail(key, tableName, columns, ddl) {
        // var list = this.getTables(key);
        // if (list) {
        //     for (let l of list) {
        //         if (l.tableName == tableName) {
        //             l.columns = columns;
        //             l.ddl = ddl;
        //             break;
        //         }
        //     }
        //     this.setTables(key, list);
        // }

    }
}

function getTablesMap() {
    let valueStr = localStorage.getItem(datakey_tables)
    return valueStr ? JSON.parse(valueStr) : null
}

export default DbData