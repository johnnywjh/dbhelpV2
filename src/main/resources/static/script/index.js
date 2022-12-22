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

const data = [
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
            count: 1,
            title: 'hello word',
            data,
            columns,
        }
    },
    created() {
        console.log('init')
    }
}
Vue.createApp(Counter).use(antd).mount('#app')