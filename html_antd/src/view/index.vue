<template>
  <div id="container">
    <div class="topDiv">
      <a-row :gutter="16">
        <a-col :span="3">
          <a-button @click="fileStyleShow">数据源格式</a-button>
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
        <a-col :span="10">
          <!--          -->
          <a-space>
            <a-select :placeholder="dbSelectTitle"
                      v-model:value="dbKey"
                      style="width: 150px"
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
            <a-button v-else disabled>选择数据库</a-button>

            <a-button type="primary" v-if="dbList.length>2" @click="showDiffDb">对比数据库</a-button>
            <a-button type="primary" v-else disabled>对比数据库</a-button>

            <a-button type="primary" v-if="dbList.length>2" @click="showQueryFieid">搜索字段</a-button>
            <a-button type="primary" v-else disabled>搜索字段</a-button>
          </a-space>
        </a-col>
        <a-col :span="8">
          <switch-theme style="margin-left: 20px;" />
          <a-tag style="margin-left: 20px">v.2.5</a-tag>
          <a href="/index.html">el</a>
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
            <a-tag color="red">{{ tableList ? tableList.length : -1 }}</a-tag>
          </template>
          <a-table :data-source="tableList" :columns="columns"
                   :pagination="false" size="small"
          >
            <template #bodyCell="{ column, text, record }">
              <template v-if="column.dataIndex === 'operation'">
                <a-tag class="but" color="green" @click="detailLayerClick(record)">详情</a-tag>

                <a-tag v-if="record.selected" color="purple">已选</a-tag>
                <a-tag v-else class="but" color="cyan" @click="selectClick(record)">选择</a-tag>

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
            <a-tag color="red">{{ selectTable.length }}</a-tag>
          </template>
          <div>
            <a-form style="margin-left: 50px"
                    layout="inline"
            >
              <a-form-item label="代码模板">
                <a-select :placeholder="themeTitle"
                          v-model:value="userinfo.fkType"
                          style="width: 200px"
                          :options="themeList"
                ></a-select>
              </a-form-item>
              <a-form-item label="按表名分组">
                <a-switch v-model:checked="tableNameGruop"/>
              </a-form-item>
              <a-form-item>
                <a-button v-if="subformShow" @click="showCodePreview">预览</a-button>
                <a-button v-if="subformShow" @click="subform" type="primary">提交</a-button>

                <a-button v-if="!subformShow">提交 and 预览</a-button>
              </a-form-item>
            </a-form>
            <div style="margin-top: 20px">
              <a-row :gutter="20">
                <!--                <a-col :span="2"></a-col>-->
                <a-col :span="14">
                  <a-table :data-source="selectTable" :columns="selectTableColumns"
                           :pagination="false" size="small"
                  >
                    <template #bodyCell="{ column, text, record }">
                      <template v-if="column.dataIndex === 'operation'">
                        <a-tag class="but" color="red" @click="selectTableDel(record)">删除</a-tag>
                      </template>
                      <template v-else-if="column.dataIndex === 'className'">
                        <a-input v-model:value="record.className"/>
                      </template>
                      <template v-else-if="column.dataIndex === 'dir1'">
                        <a-input v-model:value="record.dir1"/>
                      </template>
                      <template v-else-if="column.dataIndex === 'dir2'">
                        <a-input v-model:value="record.dir2"/>
                      </template>
                      <template v-else-if="column.dataIndex === 'remarkVal'">
                        <a-input v-model:value="record.remarkVal"/>
                      </template>
                    </template>
                  </a-table>
                </a-col>
                <a-col :span="10">
                  <a-space>
                    扩展区域
                    <a-button @click="exAddClick">添加字段</a-button>
                    模板中要判断是否为空 如:
                    <a-typography-paragraph code copyable>
                      ${key!'defaultVal'}
                    </a-typography-paragraph>
                  </a-space>
                  <div class="add_div">
                    <a-form>
                      <a-form-item v-for="item in userinfo.exList">
                        <a-space>
                          <a-input class="add_input" v-model:value="item.key" placeholder="模板中的 ${key}"/>
                          <a-input class="add_input" v-model:value="item.value" placeholder="value"/>
                          <!--                          <delete-outlined style="font-size: 20px" />-->
                          <delete-two-tone class="but" @click="exDelClick(item.id)" style="font-size: 20px"/>
                        </a-space>
                      </a-form-item>

                    </a-form>
                  </div>
                </a-col>
              </a-row>
            </div>
          </div>
        </a-tab-pane>

      </a-tabs>
    </div>
    <!--    模块框=>数据源格式 -->
    <file-style ref="fileStyleRef"/>

    <!--    模块框=>表格详情 -->
    <table-detail
        ref="tableDetailRef"
        :detail-data="detailData"
        :title="tableDetailTitle"
    />

    <!--    模块框=>代码预览 -->
    <code-preview ref="codePreviewRef"/>

    <!--    模块框=>表对比 -->
    <diff-db-tables ref="diffDbTableRef"/>

    <!--    模块框=>搜索字段 -->
    <query-fieid ref="queryFieidRef" :db-list="dbList"/>

  </div>
</template>

<script setup>
import {ref, reactive, computed, onMounted} from "vue";
import DbData from '@/utils/DbData'
import {message, Modal} from 'ant-design-vue'
import {DeleteOutlined, DeleteTwoTone} from '@ant-design/icons-vue';
import Http from '@/utils/Http'
import ApiUrls from '@/utils/ApiUrls'

import SwitchTheme from "@/view/theme/switch-theme.vue"
import TableDetail from '@/view/modal/table-detail.vue'
import CodePreview from '@/view/modal/code-preview.vue'
import DiffDbTables from '@/view/modal/diffDb-tables.vue'
import FileStyle from '@/view/modal/file-style.vue'
import QueryFieid from '@/view/modal/query-fieid.vue'

// 页面初始加载
onMounted(() => {
  reloadDbSelect()
  getTheme()
  let userVo = DbData.getUser()
  if (userVo) {
    userinfo.db = userVo.db ? userVo.db : {}
    userinfo.fkType = userVo.fkType ? userVo.fkType : undefined
    userinfo.exList = userVo.exList ? userVo.exList : [{id: guid(), key: '', value: ''}]
  } else {
    userinfo.exList = [{id: guid(), key: '', value: ''}]
  }
});

const fileStyleRef = ref()
const fileStyleShow = function () {
  fileStyleRef.value.show();// 调用子组件的弹出方法
}

const updateUrl = ref('/api/user/readDbCofig')
const dbKey = ref(undefined)
const dbList = ref([{label: "选择数据-0", value: 0}])
const tableList = ref([])
const searchTableText = ref('')
const userinfo = reactive({
  db: {
    name: '', url: '', pwd: ''
  },
  fkType: undefined,
  exList: []
})
// const exAddList = ref([])
const activeKey = ref("1")
const columns = ref([
  {title: '序号', dataIndex: 'index', align: 'right', width: '100px'},
  {title: '操作', dataIndex: 'operation', width: '200px'},
  {title: '表名', dataIndex: 'tableNameStr',},
  {title: '注释', dataIndex: 'commentStr'},
])
// 选择的数据
const selectTable = ref([])

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
const dbSelectTitle = ref()
// 从本地缓存中读取数据库下拉框
const reloadDbSelect = function () {
  var arr = [{label: "空", value: null}]
  for (let l in DbData.getDb()) {
    arr.push({label: l, value: l})
  }
  dbList.value = arr
  dbSelectTitle.value = '请选择数据--' + arr.length
}

const selectDbValue = function () {
  let val = dbKey.value
  if (val && val != '0') {
    searchTableText.value = '';
    var db = DbData.getDb()[val];
    db.key = val;

    userinfo.db = db
    DbData.setUser(userinfo)
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
  activeKey.value = '1'
}

// 搜索输入框的回车键事件
const reLoadTables = function () {
  var key = userinfo.db.key;
  if (key == 0 || !key) {
    message.warning('需要选择一个数据源');
    return;
  }
  var list = DbData.getTables(key);
  tableList.value = list;

  if (list) {
    filterList(list);
  } else {
    Http.post(ApiUrls.db.getTables, userinfo.db)
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
  DbData.setTablesDetail(userinfo.db.key, row.tableName, null, null);
}

/*界面详情按钮*/
function detailLayerClick(row) {
  var queryTable = DbData.getTableObj(dbKey.value, row.tableName);

  if (queryTable && queryTable.columns) {
    showTableDetail(queryTable);
    row.columns = queryTable.columns;
  } else {
    var queryData = userinfo.db;
    queryData.tableName = row.tableName;
    Http.post(ApiUrls.db.searchTableDetail, queryData)
        .then(function (res) {
          let table = res.data.data;
          row.columns = table.columns
          row.ddl = table.ddl
          DbData.setTablesDetail(dbKey.value, row.tableName, row.columns, row.ddl);
          showTableDetail(row);
        })
        .catch(function (error) {
          console.log(error);
        });
  }
}

// 子组件:弹出表格详情 -- start
const tableDetailRef = ref()
const tableDetailTitle = ref('xxx:xxx')
const detailData = ref({columns: [], ddl: ''})  // 选中的表的数据
function showTableDetail(queryTable) {
  tableDetailRef.value.show();// 调用子组件的弹出方法
  tableDetailTitle.value = queryTable.comment + " : " + queryTable.tableName
  detailData.value = queryTable;
}

// 子组件:弹出表格详情 -- end

// -------------------
// 生成代码
// -------------------
const tableNameGruop = ref(false)
const themeList = ref([])
const themeTitle = ref()
const getTheme = function () {
  Http.post(ApiUrls.user.getThemes, null)
      .then(function (res) {
        let list = res.data.data
        var arr = [{label: "空", value: null}]
        for (let l of list) {
          arr.push({label: l, value: l})
        }
        themeList.value = arr
        themeTitle.value = '选择模板--' + arr.length
      })
      .catch(function (error) {
        console.log(error);
      });
}

// 选择
const selectTableColumns = ref([
  {title: '操作', dataIndex: 'operation', width: '100px'},
  {title: '表名', dataIndex: 'tableName', width: '170px'},
  {title: '类名', dataIndex: 'className', width: '170px'},
  {title: '目录1', dataIndex: 'dir1', width: '100px'},
  {title: '目录2', dataIndex: 'dir2', width: '100px'},
  {title: '注释', dataIndex: 'remarkVal', width: '100px'},
])
const selectClick = function (row) {
  row.selected = true
  row.dir1 = 'aaa'
  row.dir2 = 'bbb'
  row.remarkVal = 'xx'
  selectTable.value.push(row)
}
const selectTableDel = function (row) {
  row.selected = false
  var arr = []
  for (let t of selectTable.value) {
    if (t.tableName != row.tableName) {
      arr.push(t)
    }
  }
  selectTable.value = arr
}
const subformShow = computed(() => {
  return userinfo.fkType && selectTable.value.length > 0
})
// 提交
const subform = function () {
  var data = getSubmitdata();
  // var form = document.createElement("form");
  // form.action = ApiUrls.db.generate;
  // form.method = 'post';
  // form.target = '_blank';
  // form.style = "display: none";
  //
  // var inputUrl = document.createElement("input");
  // inputUrl.name = 'url'
  // inputUrl.value = data.url
  // inputUrl.type = "hidden"
  // form.appendChild(inputUrl);
  //
  // var inputName = document.createElement("input");
  // inputName.name = 'name'
  // inputName.value = data.name
  // inputName.type = "hidden"
  // form.appendChild(inputName);
  //
  // var inputPwd = document.createElement("input");
  // inputPwd.name = 'pwd'
  // inputPwd.value = data.pwd
  // inputPwd.type = "hidden"
  // form.appendChild(inputPwd);
  //
  // var inputfkType = document.createElement("input");
  // inputfkType.name = 'fkType'
  // inputfkType.value = data.fkType
  // inputfkType.type = "hidden"
  // form.appendChild(inputfkType);
  //
  // var i = 0;
  // for (let t of data.tables) {
  //   for (let c in t) {
  //     var input = document.createElement("input");
  //     input.name = `tables[${i}].${c}`
  //     input.value = `${t[c]}`
  //     input.type = "hidden"
  //     form.appendChild(input);
  //   }
  //   i++;
  // }
  //
  // document.body.appendChild(form);
  // form.submit();

  Http.post(ApiUrls.db.generate, data, {responseType: 'blob'})
      .then(res => {
        const {data, headers} = res
        const fileName = headers['content-disposition'].replace(/\w+;filename=(.*)/, '$1').split('=')[1]
        // 此处当返回json文件时需要先对data进行JSON.stringify处理，其他类型文件不用做处理
        //const blob = new Blob([JSON.stringify(data)], ...)
        const blob = new Blob([data], {type: headers['content-type']})
        let dom = document.createElement('a')
        let url = window.URL.createObjectURL(blob)
        dom.href = url
        dom.download = decodeURI(fileName)
        dom.style.display = 'none'
        document.body.appendChild(dom)
        dom.click()
        dom.parentNode.removeChild(dom)
        window.URL.revokeObjectURL(url)
      })
}

function getSubmitdata() {
  var tables = [];
  for (let l of selectTable.value) {
    tables.push({
      tableName: l.tableName,
      className: l.className,
      comment: l.comment,
      dir1: l.dir1,
      dir2: l.dir2,
      remarkVal: l.remarkVal,
    })
  }
  var exMap = {}
  if (userinfo.exList) {
    for (let ex of userinfo.exList) {
      if (ex.key) {
        exMap[ex.key] = ex.value
      }
    }
  }
  // var user = DbData.getUser();
  var data = {
    url: userinfo.db.url,
    name: userinfo.db.name,
    pwd: userinfo.db.pwd,
    fkType: userinfo.fkType,
    tables: tables,
    tableNameGruop: tableNameGruop.value,
    exMap: exMap
  }
  // userinfo.exList = exAddList.value
  DbData.setUser(userinfo)
  return data;
}

// 子组件:代码预览 -- start
const codePreviewRef = ref()

function showCodePreview() {
  let data = getSubmitdata()// 预览提交的数据
  codePreviewRef.value.show(data);// 调用子组件的弹出方法
}

// 子组件:代码预览 -- end


// ==============================
// 扩展区域
// ==============================
const exAddClick = function () {
  userinfo.exList.push({id: guid(), key: '', value: ''})
}
const exDelClick = function (id) {
  var arr = []
  for (let item of userinfo.exList) {
    if (id != item.id) {
      arr.push(item)
    }
  }
  userinfo.exList = arr
}


function guid() {
  return S4() + S4() + S4() + S4() + S4() + S4() + S4() + S4();
}

//用于生成uuid
function S4() {
  return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
}


// 子组件:对比数据库 -- start
const diffDbTableRef = ref()

function showDiffDb() {
  let data = []
  for (let db of dbList.value) {
    if (db.value) {
      data.push(db.value)
    }
  }
  diffDbTableRef.value.show(data);// 调用子组件的弹出方法
}

// 子组件:对比数据库 -- end

// 子组件:搜索字段 -- start
const queryFieidRef = ref()

function showQueryFieid() {
  let data = {dbKey: dbKey.value}
  queryFieidRef.value.show(data);// 调用子组件的弹出方法
}

// 子组件:搜索字段 -- end

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

.liInfo_div_ex {
  border: solid 1px #CCC;
  height: auto;
}

.liInfo_div {
  border: solid 1px #CCC;
  padding: 5px;
  margin-top: 5px;
  height: auto;
}

.add_div {
  margin: 30px;
  width: 600px;
}

.add_input {
  width: 200px;
}

</style>