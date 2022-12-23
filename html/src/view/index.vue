<template>
  <div id="container">
    <div class="topDiv">
      <a-row :gutter="16">
        <a-col :span="4">
          <a-button class="topBut">Default Button</a-button>
        </a-col>
        <a-col :span="3">
          <a-upload
              :action="updateUrl"
              name="file"
              accept=".json"
              @change="handleFileChange">
            <a-button>
              解析数据源
            </a-button>
          </a-upload>
        </a-col>
        <a-col :span="16">
          <a-space>
            <a-select
                v-model:value="dbKey"
                style="width: 200px"
                :options="dbList"
                @change="selectDbValue"
            ></a-select>
            <a-popconfirm
                :title="'确定要清除 ' + dbKey + ' 的所有缓存?'"
                @confirm="cleanDbCache"
                v-if="dbKey"
            >
              <a-button>清除当前DB缓存</a-button>
            </a-popconfirm>
            <a-button v-else>选择数据库</a-button>

            <a-button type="primary">对比数据库</a-button>
            <a-button type="primary">搜索字段</a-button>
          </a-space>

        </a-col>
        <!--
                        <a-col :span="4">
                            <div class="gutter-box">col-6</div>
                        </a-col>
        -->

      </a-row>
    </div>
    <div class="content">

      <a-input-search style="width: 400px"
                      v-model:value="searchTableText"
                      placeholder="输入表名或注释"
                      enter-button
                      @search="reLoadTables"
      />

      <a-tabs v-model:activeKey="activeKey">
        <a-tab-pane key="1">
          <template #tab>
            列表
            <a-tag color="red">{{ table_size }}</a-tag>
          </template>
          <a-table :data-source="tableList" :columns="columns"
                   :pagination="false" size="small"
                   class="ant-table-striped"
                   :row-class-name="(_record, index) => (index % 2 === 1 ? 'table-striped' : null)"
          >
            <template #bodyCell="{ column, text, record }">
              <template v-if="column.dataIndex === 'operation'">
                <a-tag class="but" color="green" @click="detailLayerClick(record)">详情</a-tag>

                <a-tag v-if="record.selected" color="bule">已选</a-tag>
                <a-tag v-else>选择</a-tag>

                <a-tag class="but" v-if="record.columns" @click="cleanCache(record)" color="orange">清除缓存</a-tag>
                <a-tag v-else>清除缓存</a-tag>
              </template>
              <template v-else-if="column.dataIndex === 'tableNameStr'">
                <p v-html="record.tableNameStr"></p>
              </template>
              <template v-else-if="column.dataIndex === 'commentStr'">
                <p v-html="record.commentStr"></p>
              </template>
            </template>
          </a-table>
        </a-tab-pane>
        <a-tab-pane key="2">
          <template #tab>
            代码
            <a-tag color="red">{{ table_size }}</a-tag>
          </template>
        </a-tab-pane>

      </a-tabs>
    </div>

    <!--    模块框 -->
    <a-modal v-model:visible="detailLayerVisible" width="800px" :title="detailLayerTitle">
      <DetailPage :detailData="detailData"/>
    </a-modal>
  </div>
</template>

<script setup>
import {ref, reactive, computed, onMounted} from "vue";
import DbData from '@/utils/DbData'
import {message, Modal} from 'ant-design-vue';
import Http from '@/utils/Http'
import LocalData from "@/utils/localData";

import DetailPage from '@/view/detail.vue'

// 页面初始加载
onMounted(() => {
  reloadDbSelect()
});

const updateUrl = ref('/api/user/readDbCofig')
const dbKey = ref('')
const table_size = ref(0)
const code_count = ref(0)
const dbList = ref([{label: "选择数据-0", value: 0}])
const tableList = ref([])
const searchTableText = ref('')
const userinfo = ref({db: {}})
const activeKey = ref("1")
const columns = ref([
  {title: '序号', dataIndex: 'index', width: '100px'},
  {title: '操作', dataIndex: 'operation', width: '200px'},
  {title: '表名', dataIndex: 'tableNameStr',},
  {title: '注释', dataIndex: 'commentStr'},
])

// -------------------
// ------- 界面上的方法
// -------------------
const handleFileChange = function (e) {
  if (e.file.status == 'done') {
    var res = e.fileList[0]
    if (res.status == 'done') {
      var data = res.response.data;
      DbData.setDb(data)
      reloadDbSelect()
    }
  }
}
// 从本地缓存中读取数据库下拉框
const reloadDbSelect = function () {
  var arr = [{label: "选择数据", value: 0}];
  var count = 0;
  for (let l in DbData.getDb()) {
    arr.push({label: l, value: l});
    count++;
  }
  // arr[0].label = '选择数据-' + count
  dbList.value = arr
}

const selectDbValue = function () {
  let val = dbKey.value
  if (val != '0') {
    searchTableText.value = '';
    var db = DbData.getDb()[val];
    db.key = val;

    userinfo.value.db = db;
    DbData.setUser(userinfo.value);

    reLoadTables();
  } else {
    tableList.value = [];
  }
}

const filterList = function (list) {
  if (searchTableText.value) {
    var contentArr = searchTableText.value.split(",");
    var arr = [];
    for (let l of list) {
      for (let content of contentArr) {
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
  tableList.value = list;
  table_size.value = 0
  code_count.value = 0
  activeKey.value = '1'
}

// 搜索输入框的回车键事件
const reLoadTables = function () {
  var key = userinfo.value.db.key;
  if (key == 0 || !key) {
    message.warning('需要选择一个数据源');
    return;
  }
  var list = DbData.getTables(key);
  tableList.value = list;

  if (list) {
    filterList(list);
  } else {
    Http.post('/api/db/getTables', userinfo.value.db)
        .then(function (res) {
          var list = res.data.data;
          DbData.setTables(key, list);
          filterList(list);
        })
        .catch(function (error) {
          console.log(error);
        });
  }
}

// 清除数据库缓存
const cleanDbCache = function () {
  // DbData.cleanDb()
  // setTimeout(() => location.reload(), 1000);
  DbData.cleanDb(dbKey.value)
  message.success({
    content: '清除成功',
    duration: 1,
    onClose: function () {
      location.reload()
    }
  });
}
const cleanCache = function (row) {
  row.columns = null;
  DbData.setTablesDetail(userinfo.value.db.key, row.tableName, null);
}

/*界面详情按钮*/
function detailLayerClick(row) {
  var queryTable = DbData.getTablesDetail(dbKey.value, row.tableName);
  if (!queryTable) {
    var queryData = userinfo.value.db;
    queryData.tableName = row.tableName;
    Http.post('/api/db/searchTableDetail', queryData)
        .then(function (res) {
          let table = res.data.data;
          row.columns = table.columns
          row.ddl = table.ddl ? table.ddl.replace(new RegExp("\n", 'gm'), "</br>") : ""
          DbData.setTablesDetail(dbKey.value, row.tableName, row);
          initcolumns(row);
        })
        .catch(function (error) {
          console.log(error);
        });
  } else {
    initcolumns(queryTable);
    row.columns = queryTable.columns;
  }
}

// ------------- 弹出层
const detailLayerVisible = ref(false)
const detailLayerTitle = ref('xxx:xxx')
const detailData = ref({columns: [], ddl: ''})  // 选中的表的数据

// 列表加载
function initcolumns(queryTable) {
  detailLayerVisible.value = true
  detailLayerTitle.value = queryTable.comment + " : " + queryTable.tableName
  detailData.value = queryTable;
}

</script>

<style>
#container {
  padding: 20px 50px;
}

.topDiv {
  margin-left: 10px;
}

.content {
  margin-top: 20px;
}

.content .topSpan {
  margin-left: 20px;
}

.searchText {
  color: #fe7300;
}

.ant-table-striped :deep(.table-striped) td {
  background-color: #fafafa;
}

.but {
  cursor: pointer;
}

</style>