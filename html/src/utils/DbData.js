/* 数据库的key*/
let datakey_db = "dbsource";
let datakey_user = "user";
let datakey_tables = "tables";

let DbData = {
    setDb(db) {
        localStorage.setItem(datakey_db, JSON.stringify(db))
    },
    getDb() {
        let valueStr = localStorage.getItem(datakey_db)
        return valueStr ? JSON.parse(valueStr) : null
    },
    cleanDb(dbKey) {
        // localStorage.removeItem(datakey_tables)
        var dbMap = getDbMap()
        if (dbMap) {
            delete dbMap[dbKey]
            localStorage.setItem(datakey_tables, JSON.stringify(dbMap))
        }
    },
    cleanDbAll() {
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
    setTables(dbKey, tableList) {
        var dbMap = getDbMap()
        dbMap = dbMap || {}
        dbMap[dbKey] = tableList
        localStorage.setItem(datakey_tables, JSON.stringify(dbMap))
    },
    /** 获取这个库的表的集合 */
    getTables(dbKey) {
        var dbMap = getDbMap()
        return dbMap ? dbMap[dbKey] : null
    },
    /** 获取表的详情 */
    getTableObj(dbKey, tableName) {
        var list = this.getTables(dbKey)
        if (!list) {
            return null;
        }
        var obj = null
        for (let l of list) {
            if (l.tableName == tableName) {
                obj = l
                break;
            }
        }
        return obj
    },
    setTablesDetail(dbKey, tableName, columns, ddl) {
        var list = this.getTables(dbKey);
        if (list) {
            for (let l of list) {
                if (l.tableName == tableName) {
                    l.columns = columns;
                    l.ddl = ddl;
                    break;
                }
            }
            this.setTables(dbKey, list);
        }

    }
}

function getDbMap() {
    let valueStr = localStorage.getItem(datakey_tables)
    return valueStr ? JSON.parse(valueStr) : null
}

export default DbData