/* 数据库的key*/
var datakey_db = "dbsource";
var datakey_user = "user";
var datakey_tables = "tables";
var opLeyerMsgTime = 1000;

function setDb(db) {
    localData(datakey_db, {key: "db", value: db});
}

function getDb() {
    return localData(datakey_db).db;
}

var userinfo = {
    db: {}
    , basePackageName: ''
    , author: ''
}

/** 保存用户信息 */
function setUser(user) {
    userinfo = user;
    localData(datakey_user, {key: "user", value: user});
}

/** 获取用户信息 */
function getUser() {
    return localData(datakey_user).user;
}

/** 保存某一个数据库的表的集合 */
function setTables(key, list) {
    localData(datakey_tables, {key: key, value: list});
}

/** 获取这个库的表的集合 */
function getTables(key) {
    var obj = localData(datakey_tables);
    return obj[key];
}

/** 获取表的详情 */
function getTablesDetail(key, tableName) {
    var list = getTables(key);
    if (!list) {
        return null;
    }
    for (let l of list) {
        if (l.tableName == tableName) {
            return l.columns;
            break;
        }
    }
}

function setTablesDetail(key, tableName, columns) {
    var list = getTables(key);
    if (list) {
        for (let l of list) {
            if (l.tableName == tableName) {
                l.columns = columns;
                break;
            }
        }
        setTables(key, list);
    }

}