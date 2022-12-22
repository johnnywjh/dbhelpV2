const columns = [
    {title: '序号', dataIndex: 'index', key: 'index'},
    // {title: '操作', dataIndex: 'id', key: 'id'},
    {title: '表名', dataIndex: 'tableNameStr', key: 'tableNameStr'},
    {title: '注释', dataIndex: 'tableNameStr', key: 'tableNameStr'},
]

const Counter = {
    data() {
        return {
            title: 'init',
            dbList: [{label: "选择数据-0", value: 0}],
            dbKey: '0',
            searchTableText: '',
            columns,
            tableList: []


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
        cleanDbCache,
        filterList
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
    this.title = 'load done'
    var list = getTables(key);
    this.tableList = list;
    console.log(title)
    // if (list) {
    //     filterList(list);
    // } else {
    //     axios.post('/db/getTables', userinfo.db)
    //         .then(function (res) {
    //             var list = res.data.data;
    //             setTables(key, list);
    //             filterList(list);
    //         })
    //         .catch(function (error) {
    //             console.log(error);
    //         });
    // }
}

// 过滤搜索的表格数据
function filterList(list) {
    let vm = this;
    if (vm.searchTableText) {
        var contentArr = vm.searchTableText.split(",");
        var arr = [];
        for (let l of list) {
            for (let content of contentArr) {
                //tableName   comment
                let includesFlag = false;
                if (l.tableName.includes(content)) {
                    l.tableNameStr = l.tableName.replace(new RegExp(content, 'g'), `<b class="searchText">${content}</b>`);
                    includesFlag = true;
                }
                if (l.comment) {
                    if (l.comment.includes(content)) {
                        l.commentStr = l.comment.replace(new RegExp(content, 'g'), `<b class="searchText">${content}</b>`);
                        includesFlag = true;
                    }
                }
                if (includesFlag) {
                    arr.push(l)
                }
            }
        }
        list = arr;
    }
    if (list) {
        for (var i = 0; i < list.length; i++) {
            list[i].index = i + 1;
            if (!list[i].tableNameStr) {
                list[i].tableNameStr = list[i].tableName;
            }
            if (!list[i].commentStr) {
                list[i].commentStr = list[i].comment;
            }
            list[i].index = i + 1;
        }
    }
    vm.tableList = list;
    console.log(JSON.stringify(list))
    this.title = 'load done'
}

// 清除数据库缓存
function cleanDbCache() {
    if (this.dbKey == '0') {
        return;
    }
    antd.Modal.confirm({
        title: '确定要清除 ' + this.dbKey + ' 的所有缓存?',
        onOk() {
            localData(datakey_tables, null);
            setTimeout(() => location.reload(), 1000);
        },
        class: 'test',
    });

    // layer.confirm('<span style="color: black">确定要清除 ' + this.dbKey + ' 的所有缓存?</span>', {
    //     icon: 3,
    //     title: '提示'
    // }, function (index) {
    //     layui.data(datakey_tables, null);
    //     layer.close(index);
    //     layer.msg('清除成功', {
    //         time: 1000
    //     }, function () {
    //         location.reload();
    //     });
    // });
}
