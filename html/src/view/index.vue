<template>
  <div id="container">
    <div class="topDiv">

      <el-row :gutter="2">
        <el-col :span="3">
          <el-button plain @click="fileStyleShow">数据源格式</el-button>
        </el-col>
        <el-col :span="3">
          <el-upload
              ref="upload"
              :action="updateUrl"
              name="file"
              accept=".json"
              :on-success="handleFileChange"
          >
            <el-button>解析数据源</el-button>
          </el-upload>
        </el-col>
        <el-col :span="14">
          <el-space>
            <el-select v-model="dbKey" filterable @change="selectDbValue" :placeholder="dbSelectTitle">
              <el-option
                  v-for="item in dbList"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
              />
            </el-select>
            <!--            <el-popconfirm-->
            <!--                v-if="dbKey"-->
            <!--                :title="'确定要清除 ' + dbKey + ' 的所有缓存?'"-->
            <!--                @confirm="cleanDbCache"-->
            <!--            >-->
            <!--              <template #reference>-->
            <!--                <el-button>清除当前DB缓存</el-button>-->
            <!--              </template>-->
            <!--            </el-popconfirm>-->
            <el-popconfirm
                v-if="dbKey"
                :title="'确定重新加载 ' + dbKey + ' 的缓存?'"
                @confirm="reLoadDbCache"
            >
              <template #reference>
                <el-button>重载当前DB缓存</el-button>
              </template>
            </el-popconfirm>
            <el-button v-else disabled>选择数据库</el-button>

            <el-popconfirm
                :title="'确定要清除全部数据库的缓存?'"
                @confirm="cleanDbCacheAll"
            >
              <template #reference>
                <el-button>清除全部DB缓存</el-button>
              </template>
            </el-popconfirm>

            <el-button type="primary" v-if="dbList.length>2" @click="showDiffDb" plain>对比数据库</el-button>
            <el-button type="primary" v-else disabled plain>对比数据库</el-button>

            <el-button type="success" v-if="dbList.length>1" @click="showQueryFieid" plain>搜索字段</el-button>
            <el-button type="success" v-else disabled plain>搜索字段</el-button>

            <el-button type="success" v-if="dbList.length>1" @click="showQueryTable" plain>搜索表名</el-button>
            <el-button type="success" v-else disabled plain>搜索表名</el-button>

          </el-space>
        </el-col>
        <el-col :span="4">
          <el-space>
            <switch-theme/>
            <el-tag type="success">v.2.35</el-tag>
            <el-button size="small" plain @click="reloadTheme">重现加载模板</el-button>
          </el-space>
        </el-col>
      </el-row>

    </div>
    <div class="content">
      <el-input
          style="width: 400px"
          v-model="searchTableText"
          placeholder="输入表名或注释"
          @change="reLoadTables"
      >
        <template #append>
          <el-button @click="reLoadTables" :icon="Search"/>
        </template>
      </el-input>

      <el-tabs v-model="activeKey" style="margin-top: 5px">
        <el-tab-pane name="1">
          <template #label>
            <span>
              <span>列表 <el-tag type="danger">{{ tableList ? tableList.length : -1 }}</el-tag></span>
            </span>
          </template>
          <el-table :data="tableList" stripe style="width: 100%">
            <el-table-column prop="index" align="right" label="序号" width="100"/>
            <el-table-column prop="operation" label="操作" width="220">
              <template #default="scope">
                <el-space>
                  <el-tag class="but" type="success" @click="detailLayerClick(scope.row)">详情</el-tag>

                  <el-tag v-if="scope.row.selected" type="info">已选</el-tag>
                  <el-tag v-else class="but" @click="selectClick(scope.row)" type="danger">选择</el-tag>

                  <el-tag class="but" type="warning" v-if="scope.row.columns"
                          @click="cleanCache(scope.row)">清除缓存
                  </el-tag>
                  <el-tag v-else type="info">无缓存</el-tag>
                </el-space>
              </template>
            </el-table-column>
            <el-table-column prop="tableNameStr" label="表名">
              <template #default="scope">
                <span v-html="scope.row.tableNameStr"></span>
                <copy :content="scope.row.tableName" :style-val="{'margin-left':'5px'}"/>
              </template>
            </el-table-column>
            <el-table-column prop="commentStr" label="注释">
              <template #default="scope">
                <span v-html="scope.row.commentStr"></span>
                <copy :content="scope.row.comment" :style-val="{'margin-left':'5px'}"/>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="代码" name="2">
          <template #label>
            <span>
              <span>列表 <el-tag type="danger">{{ selectTable.length }}</el-tag></span>
            </span>
          </template>
          <div>
            <el-form :inline="true" style="margin-left: 50px">
              <el-row>
                <el-form-item label="packagePath">
                  <el-input style="width: 300px" v-model="userinfo.packagePath"/>
                </el-form-item>
                <el-form-item label="modelName">
                  <el-input v-model="userinfo.modelName"/>
                </el-form-item>
              </el-row>
              <el-row>
                <el-form-item label="代码模板">
                  <el-select v-model="userinfo.fkType" :placeholder="themeTitle" style="width: 200px">
                    <el-option
                        v-for="item in themeList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item label="按表名分组">
                  <el-switch v-model="tableNameGruop"/>
                </el-form-item>
                <el-form-item>
                  <el-button v-if="subformShow" @click="showCodePreview">预览</el-button>
                  <el-button v-if="subformShow" @click="subform" type="primary">提交</el-button>

                  <el-button v-if="!subformShow">提交 and 预览</el-button>
                </el-form-item>
              </el-row>
            </el-form>
            <div style="margin-top: 20px">
              <el-row :gutter="16">
                <el-col :span="14">
                  <el-table :data="selectTable" border size="small">
                    <el-table-column prop="operation" label="操作" width="100">
                      <template #default="scope">
                        <el-tag type="danger" class="but" @click="selectTableDel(scope.row)">删除</el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column prop="tableName" label="表名" width="170"/>
                    <el-table-column prop="className" label="类名" width="170">
                      <template #default="scope">
                        <el-input v-model="scope.row.className"/>
                      </template>
                    </el-table-column>
                    <el-table-column prop="dir1" label="目录1" width="100">
                      <template #default="scope">
                        <el-input v-model="scope.row.dir1"/>
                      </template>
                    </el-table-column>
                    <el-table-column prop="dir2" label="目录2" width="100">
                      <template #default="scope">
                        <el-input v-model="scope.row.dir2"/>
                      </template>
                    </el-table-column>
                    <el-table-column prop="remarkVal" label="注释" width="150">
                      <template #default="scope">
                        <el-input v-model="scope.row.remarkVal"/>
                      </template>
                    </el-table-column>
                  </el-table>
                </el-col>
                <el-col :span="10">
                  <el-space>
                    扩展区域
                    <el-button @click="exAddClick">添加字段</el-button>
                    模板中要判断是否为空 如:
                    <span>
                      ${key!'defaultVal'}
                      <copy content="${key!'defaultVal'}" :style-val="{}"/>
                    </span>
                  </el-space>
                  <div class="add_div">
                    <el-form>
                      <el-form-item v-for="item in exAddList">
                        <el-space>
                          <el-input class="add_input" v-model="item.key" placeholder="模板中的 ${key}"/>
                          <el-input class="add_input" v-model="item.value" placeholder="value"/>

                          <el-icon class="but" @click="exDelClick(item.id)" style="font-size: 20px;color: red;">
                            <Delete/>
                          </el-icon>
                        </el-space>
                      </el-form-item>
                    </el-form>
                  </div>
                </el-col>
              </el-row>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>

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
    <diff-db-tables ref="diffDbTableRef" :db-list="dbList"/>

    <!--    模块框=>搜索字段 -->
    <query-fieid ref="queryFieidRef" :db-list="dbList"/>

    <!--    模块框=>搜索表名 -->
    <query-table ref="queryTableRef" :db-list="dbList" @done="catTableDetail"/>

  </div>

</template>

<script setup>
import {ref, reactive, computed, onMounted} from 'vue'

import DbData from '@/utils/DbData'
import {apiGenerate, apiGetTables, apiGetThemes, apiRelaodThemes, apiSearchTableDetail} from '@/api/buss'
import {ElMessage} from 'element-plus'
import {Search, Delete} from '@element-plus/icons-vue'
import dayjs from 'dayjs';

import SwitchTheme from "@/view/theme/switch-theme.vue"
import TableDetail from '@/view/modal/table-detail.vue'
import CodePreview from '@/view/modal/code-preview.vue'
import DiffDbTables from '@/view/modal/diffDb-tables.vue'
import FileStyle from '@/view/modal/file-style.vue'
import QueryFieid from '@/view/modal/query-fieid.vue'
import QueryTable from '@/view/modal/query-table.vue'

// 页面初始加载
onMounted(() => {
  reloadDbSelect()
  getTheme()
  let userVo = DbData.getUser()
  if (userVo) {
    userinfo.db = userVo.db ? userVo.db : {}
    userinfo.fkType = userVo.fkType ? userVo.fkType : undefined
    exAddList.value = userVo.exList ? userVo.exList : [{id: guid(), key: '', value: ''}]
    userinfo.packagePath = userVo.packagePath ? userVo.packagePath : 'com.deme'
    userinfo.modelName = userVo.modelName ? userVo.modelName : ''
  } else {
    exAddList.value = [{id: guid(), key: '', value: ''}]
  }
});

const fileStyleRef = ref()
const fileStyleShow = function () {
  fileStyleRef.value.show();// 调用子组件的弹出方法
}

const upload = ref()
const updateUrl = ref('/api/user/readDbCofig')
const dbKey = ref(undefined)
const dbList = ref([{label: "选择数据-0", value: 0}])
const tableList = ref([])
const searchTableText = ref('')
const userinfo = reactive({
  db: {
    name: '', url: '', pwd: '', key: ''
  },
  fkType: undefined,
  exList: [],
  packagePath: 'com.demo.xx.yy',
  modelName: ''
})
const exAddList = ref([])
const activeKey = ref("1")

// 选择的数据
const selectTable = ref([])

// -------------------
// ------- 界面上的方法
// -------------------
const handleFileChange = (response, uploadFile) => {
  if (response.success) {
    let data = response.data
    DbData.setDb(data)
    cleanDbCacheAll()
    reloadDbSelect()
  }
}
const dbSelectTitle = ref()
// 从本地缓存中读取数据库下拉框
const reloadDbSelect = function () {
  var arr = [{label: "空", value: 0}]
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
      let includesFlag = false;
      for (let content of contentArr) {
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
      }
      if (includesFlag) {
        arr.push(l)
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
    ElMessage.warning('需要选择一个数据源');
    return;
  }
  var list = DbData.getTables(key);
  tableList.value = list;

  if (list) {
    filterList(list);
  } else {
    apiGetTables(userinfo.db, (res) => {
      var list = res.data.data;
      DbData.setTables(key, list);
      filterList(list);
    })
  }
}

// 重载当前数据源的表
const reLoadDbCache = function () {
  DbData.cleanDb(dbKey.value)
  const loading = ElLoading.service({
    lock: true,
    text: 'Loading',
    background: 'rgba(0, 0, 0, 0.7)',
  })
  apiGetTables(userinfo.db, (res) => {
    ElMessage.success('重载成功')
    var list = res.data.data;
    DbData.setTables(dbKey.value, list);
    filterList(list);
    loading.close()
  })
}

// 清除数据库缓存
const cleanDbCache = function () {
  // DbData.cleanDb()
  // setTimeout(() => location.reload(), 1000);
  DbData.cleanDb(dbKey.value)
  ElMessage({
    message: '清除成功',
    type: 'success',
    duration: 1000,
    onClose: function () {
      location.reload()
    }
  })
}
const cleanDbCacheAll = function () {
  DbData.cleanDbAll()
  ElMessage({
    message: '全部清除成功',
    type: 'success',
    duration: 1000,
    onClose: function () {
      location.reload()
    }
  })
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
    apiSearchTableDetail(queryData, (res) => {
      let table = res.data.data;
      row.columns = table.columns
      row.ddl = table.ddl
      DbData.setTablesDetail(dbKey.value, row.tableName, row.columns, row.ddl);
      showTableDetail(row);
    })
  }
}

// 子组件:弹出表格详情 -- start
const tableDetailRef = ref()
const tableDetailTitle = ref('xxx:xxx')
const detailData = ref({columns: [], ddl: ''})  // 选中的表的数据
function showTableDetail(queryTable) {

  tableDetailRef.value.show({db: DbData.getUser().db, tableName: queryTable.tableName});// 调用子组件的弹出方法
  // tableDetailTitle.value = queryTable.comment + " : " + queryTable.tableName
  tableDetailTitle.value = '表详情'
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
  apiGetThemes((res) => {
    let list = res.data.data
    var arr = [{label: "空", value: 0}]
    for (let l of list) {
      arr.push({label: l, value: l})
    }
    themeList.value = arr
    themeTitle.value = '选择模板--' + arr.length
  })
}

// 选择
const selectClick = function (row) {
  row.selected = true
  row.dir1 = 'aaa'
  row.dir2 = 'bbb'
  if (row.comment) {
    // row.remarkVal = row.comment.replace(new RegExp('表', 'g'), ``)
    row.remarkVal = row.comment.replace('表', ``)
  } else {
    row.remarkVal = 'xx'
  }
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

  // console.log(dayjs().format())
  // console.log(dayjs().format('YYYYMMDD_HHmmss'))
  let fileName = data.fkType + '_' + dayjs().format('YYYYMMDD_HHmmss')
  apiGenerate(data, (res) => {
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
  if (exAddList.value) {
    for (let ex of exAddList.value) {
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
    packagePath: userinfo.packagePath,
    modelName: userinfo.modelName,
    tables: tables,
    tableNameGruop: tableNameGruop.value,
    exMap: exMap
  }
  userinfo.exList = exAddList.value
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
  exAddList.value.push({id: guid(), key: '', value: ''})
}
const exDelClick = function (id) {
  var arr = []
  for (let item of exAddList.value) {
    if (id != item.id) {
      arr.push(item)
    }
  }
  exAddList.value = arr
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
  let data = {}
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

// 子组件:搜索表名 -- start
const queryTableRef = ref()

function showQueryTable() {
  let data = {}
  queryTableRef.value.show(data);// 调用子组件的弹出方法
}

// 子组件:搜索表名 -- end

import {ElLoading} from 'element-plus'
// 重新加载
const reloadTheme = function () {
  const loading = ElLoading.service({
    lock: true,
    text: 'Loading',
    background: 'rgba(0, 0, 0, 0.7)',
  })
  // setTimeout(() => {
  //   loading.close()
  // }, 2000)
  apiRelaodThemes(() => {
    ElMessage({
      message: '加载成功',
      type: 'success',
      duration: 800,
      onClose: function () {
        loading.close()
        getTheme()
      }
    })
  })
}

const catTableDetail = (tableName, dbName) => {
  searchTableText.value = tableName
  dbKey.value = dbName

  let val = dbKey.value
  var db = DbData.getDb()[val];
  db.key = val;

  userinfo.db = db
  DbData.setUser(userinfo)
  reLoadTables();
}

</script>
<style>
#container {
  padding: 5px 50px;
}

.topDiv {
  margin-left: 10px;
}

.content {
  margin-top: 10px;
}

.content .topSpan {
  margin-left: 20px;
}

.searchText {
  color: #fe7300;
}

.but {
  cursor: pointer;
}

.liInfo_div_ex {
  border: solid 1px #CCC;
  height: auto;
  position: relative;
}

.liInfo_div {
  border: solid 1px #CCC;
  padding: 5px;
  margin-top: 5px;
  height: auto;
  position: relative;
}

.add_div {
  margin: 30px;
  width: 600px;
}

.add_input {
  width: 200px;
}

</style>