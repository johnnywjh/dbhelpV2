

const Counter = {
    data() {
        return {
            dbList: [{label: "选择数据-0", value: 0}],
            dbKey: '0',
            // 左边的界面
            tableList: [],
            searchTableText: '',
        }
    },
    created() {
        this.reloadDbSelect()
    },
    // 方法集合
    methods: {
        handleFileChange,
        reloadDbSelect,
        selectDbValue,
        reLoadTables,
    }
}
Vue.createApp(Counter).use(antd).mount('#app')

// 上传文件
function handleFileChange(e) {
    if (e.file.status == 'done') {
        var res = e.fileList[0]
        if (res.status == 'done') {
            var data = res.response.data;
            setDb(data);
            this.reloadDbSelect()
        }
    }
}

// 从本地缓存中读取数据库下拉框
function reloadDbSelect() {
    let vm = this;
    var dbList = getDb();
    vm.dbList = [{label: "选择数据-0", value: 0}];
    var count = 0;
    for (let l in dbList) {
        vm.dbList.push({label: l, value: l});
        count++;
    }
    vm.dbList[0].label = '选择数据-' + count
}

// 选择数据库下拉框
function selectDbValue(e) {
    let val = this.dbKey;
    if (val != '0') {
        var db = getDb()[val];
        db.key = val;

        userinfo.db = db;
        setUser(userinfo);

        reLoadTables();
        this.searchTableText = '';
    } else {
        this.tableList = [];
    }
}
// 搜索输入框的回车键事件
function reLoadTables() {
    let vm = this;
    var key = userinfo.db.key;
    if (key == 0 || !key) {
        antd.message.warning('需要选择一个数据源');
        return;
    }
    var list = getTables(key);

    if (list) {
        // vm.filterList(list);
    } else {
        // web.post({
        //     load: true
        //     , url: '/db/getTables'
        //     , data: userinfo.db
        //     , done(res) {
        //         var list = res.data;
        //         setTables(key, list);
        //         vm.filterList(list);
        //     }
        // });
        axios.post('/db/getTables', userinfo.db)
            .then(function (response) {
                console.log(response);
            })
            .catch(function (error) {
                console.log(error);
            });
    }
}