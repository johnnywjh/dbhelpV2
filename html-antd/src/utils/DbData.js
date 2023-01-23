import LocalData from "@/utils/localData";

/* 数据库的key*/
var datakey_db = "dbsource";
var datakey_user = "user";
var datakey_tables = "tables";
var opLeyerMsgTime = 1000;

const DbData = {
    setDb(db) {
        LocalData.data(datakey_db, {key: "db", value: db});
    },
    getDb() {
        return LocalData.data(datakey_db).db;
    },
    cleanDb(key) {
        // LocalData.data(datakey_tables, null)
        LocalData.data(datakey_tables, {key: key, value: null});
    },
    /** 保存用户信息 */
    setUser(user) {
        LocalData.data(datakey_user, {key: "user", value: user});
    },
    /** 获取用户信息 */
    getUser() {
        return LocalData.data(datakey_user).user;
    },
    /** 保存某一个数据库的表的集合 */
    setTables(key, list) {
        LocalData.data(datakey_tables, {key: key, value: list});
    },
    /** 获取这个库的表的集合 */
    getTables(key) {
        var obj = LocalData.data(datakey_tables);
        return obj[key];
    },
    /** 获取表的详情 */
    getTableObj(key, tableName) {
        var list = this.getTables(key);
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
    setTablesDetail(key, tableName, columns, ddl) {
        var list = this.getTables(key);
        if (list) {
            for (let l of list) {
                if (l.tableName == tableName) {
                    l.columns = columns;
                    l.ddl = ddl;
                    break;
                }
            }
            this.setTables(key, list);
        }

    }
}

export default DbData