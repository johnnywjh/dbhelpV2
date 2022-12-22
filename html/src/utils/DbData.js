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
    userinfo: {
        db: {}
        , basePackageName: ''
        , author: ''
    }
}

export default DbData