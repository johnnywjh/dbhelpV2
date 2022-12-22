// const tableList = [{"tableName":"demo_test","className":"DemoTest","comment":"测试Demo","exJson":null,"columns":null,"ddl":null,"index":1,"tableNameStr":"demo_test","commentStr":"测试Demo"},{"tableName":"sys_menu","className":"SysMenu","comment":"系统菜单表","exJson":null,"columns":null,"ddl":null,"index":2,"tableNameStr":"sys_menu","commentStr":"系统菜单表"},{"tableName":"sys_role","className":"SysRole","comment":"系统角色表","exJson":null,"columns":null,"ddl":null,"index":3,"tableNameStr":"sys_role","commentStr":"系统角色表"},{"tableName":"sys_role_menu","className":"SysRoleMenu","comment":"系统角色菜单关系表","exJson":null,"columns":null,"ddl":null,"index":4,"tableNameStr":"sys_role_menu","commentStr":"系统角色菜单关系表"},{"tableName":"sys_user_login","className":"SysUserLogin","comment":"系统用户登录表","exJson":null,"columns":null,"ddl":null,"index":5,"tableNameStr":"sys_user_login","commentStr":"系统用户登录表"},{"tableName":"sys_user_role","className":"SysUserRole","comment":"系统用户角色关系表","exJson":null,"columns":null,"ddl":null,"index":6,"tableNameStr":"sys_user_role","commentStr":"系统用户角色关系表"}]

const columns = [
    {
        title: 'Name',
        dataIndex: 'name',
    },
    {
        title: 'Cash Assets',
        className: 'column-money',
        dataIndex: 'money',
    },
    {
        title: 'Address',
        dataIndex: 'address',
    },
];

const tableList = [
    {
        key: '1',
        name: 'John Brown',
        money: '￥300,000.00',
        address: 'New York No. 1 Lake Park',
    },
    {
        key: '2',
        name: 'Jim Green',
        money: '￥1,256,000.00',
        address: 'London No. 1 Lake Park',
    },
    {
        key: '3',
        name: 'Joe Black',
        money: '￥120,000.00',
        address: 'Sidney No. 1 Lake Park',
    },
];

const Counter = {
    data() {
        return {
            dbList: [{label: "选择数据-0", value: 0}],
            dbKey: '0',
            tableList,
            searchTableText: '',
            columns,


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
        cleanDbCache
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
        filterList(list);
    } else {
        axios.post('/db/getTables', userinfo.db)
            .then(function (res) {
                var list = res.data.data;
                setTables(key, list);
                filterList(list);
            })
            .catch(function (error) {
                console.log(error);
            });
    }
}

// 过滤搜索的表格数据
const filterList = (list) => {
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
    console.log(JSON.stringify(vm.tableList))
}

// 清除数据库缓存
function cleanDbCache (){
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
