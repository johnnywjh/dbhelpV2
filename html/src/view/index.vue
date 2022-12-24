<template>
  <div id="container">
    <div class="topDiv">
      <a-row :gutter="16">
        <a-col :span="4">
          <a-button @click="layerFileVisible=true">数据源格式</a-button>
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
          <!--          -->
          <a-space>
            <a-select :placeholder="dbSelectTitle"
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
            <a-tag color="red">{{ tableList ? tableList.length : -1 }}</a-tag>
          </template>
          <a-table :data-source="tableList" :columns="columns"
                   :pagination="false" size="small"
                   class="ant-table-striped"
                   :row-class-name="(_record, index) => (index % 2 === 1 ? 'table-striped' : null)"
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
                    autocomplete="off"
            >
              <a-form-item label="代码模板">
                <a-select :placeholder="themeTitle"
                          v-model:value="selectThemeValue"
                          style="width: 200px"
                          :options="themeList"
                ></a-select>
              </a-form-item>
              <a-form-item label="按表名分组">
                <a-switch v-model:checked="tableNameGruop"/>
              </a-form-item>
              <a-form-item>
                <a-button v-if="subformShow" @click="preview">预览</a-button>
                <a-button v-if="subformShow" @click="subform" type="primary">提交</a-button>

                <a-button v-if="!subformShow">提交 and 预览</a-button>
              </a-form-item>
            </a-form>
            <div style="margin-top: 20px">
              <a-row :gutter="16">
                <a-col :span="2"></a-col>
                <a-col :span="10">
                  <a-table :data-source="selectTable" :columns="selectTableColumns"
                           :pagination="false" size="small"
                           class="ant-table-striped"
                           :row-class-name="(_record, index) => (index % 2 === 1 ? 'table-striped' : null)"
                  >
                    <template #bodyCell="{ column, text, record }">
                      <template v-if="column.dataIndex === 'operation'">
                        <a-tag class="but" color="red" @click="selectTableDel(record)">删除</a-tag>
                      </template>
                      <template v-else-if="column.dataIndex === 'className'">
                        <a-input v-model:value="record.className"/>
                      </template>
                    </template>
                  </a-table>
                </a-col>
                <a-col :span="10">
                </a-col>
              </a-row>
            </div>
          </div>
        </a-tab-pane>

      </a-tabs>
    </div>
    <!--    模块框=>表格详情 -->
    <a-modal v-model:visible="layerFileVisible" :footer="false" width="400px" title="数据源格式">
      <a-typography-paragraph code="true" copyable class="liInfo_div">
<pre>
    {
        "dbName":{
            "url": "jdbc:mysql://",
            "name": "root",
            "pwd": ""
        }
        ,{...}
    }
</pre>
      </a-typography-paragraph>

    </a-modal>
    <!--    模块框=>表格详情 -->
    <a-modal v-model:visible="detailLayerVisible" :footer="false" width="800px" :title="detailLayerTitle">
      <DetailPage :detailData="detailData"/>
    </a-modal>
    <!--    模块框=>代码预览 -->
    <a-modal v-model:visible="previewVisible" :footer="false" width="1300px" title="预览" @cancel="previewClose">
      <a-row :gutter="16">
        <a-col :span="6">
          <a-directory-tree
              multiple
              :tree-data="treeData"
              auto-expand-parent="true"
              @select="selectTreeNode"
          ></a-directory-tree>
        </a-col>
        <a-col :span="18">
          <a-space>
            代码样式
            <a-switch v-model:checked="previewCode" checked-children="开" un-checked-children="关"/>
          </a-space>

          <a-typography-paragraph :code="previewCode" copyable class="liInfo_div">
          <pre>
<span v-html="fileContent"></span>
          </pre>
          </a-typography-paragraph>
        </a-col>
      </a-row>
    </a-modal>
  </div>
</template>

<script setup>
import {ref, reactive, computed, onMounted} from "vue";
import DbData from '@/utils/DbData'
import {message, Modal} from 'ant-design-vue';
import Http from '@/utils/Http'
import LocalData from "@/utils/localData"
import ApiUrls from '@/utils/ApiUrls'

import DetailPage from '@/view/detail.vue'

// 页面初始加载
onMounted(() => {
  reloadDbSelect()
  getTheme()
});

const layerFileVisible = ref(false)

const updateUrl = ref('/api/user/readDbCofig')
const dbKey = ref(undefined)
const dbList = ref([{label: "选择数据-0", value: 0}])
const tableList = ref([])
const searchTableText = ref('')
const userinfo = ref({db: {}})
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
    Http.post(ApiUrls.db.getTables, userinfo.value.db)
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
    Http.post(ApiUrls.db.searchTableDetail, queryData)
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

// -------------------
// 生成代码
// -------------------
const tableNameGruop = ref(false)
const selectThemeValue = ref(undefined)
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
  {title: '表名', dataIndex: 'tableName', width: '200px'},
  {title: '类名', dataIndex: 'className', width: '300px'},
])
const selectClick = function (row) {
  row.selected = true
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
  return selectThemeValue.value && selectTable.value.length > 0
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
      comment: l.comment
    })
  }
  // var user = DbData.getUser();
  var user = userinfo.value;
  var data = {
    url: user.db.url,
    name: user.db.name,
    pwd: user.db.pwd,
    fkType: selectThemeValue.value,
    tables: tables,
    tableNameGruop: tableNameGruop.value
  };
  return data;
}

// 预览
const dirVo = ref()
const treeData = ref()
const previewVisible = ref(false)
const fileContent = ref()
const previewCode = ref(true)
const preview = function () {
  var data = getSubmitdata();
  Http.post(ApiUrls.db.preview, data)
      .then(function (res) {
        dirVo.value = res.data.data.dirVo
        treeData.value = res.data.data.list
        previewVisible.value = true
        fileContent.value = ''
      })
      .catch(function (error) {
        console.log(error);
      });
}
const previewClose = function () {
  Http.post(ApiUrls.db.deletedir, dirVo.value)
      .then(function (res) {
        message.success('清除成功')
      })
      .catch(function (error) {
        console.log(error);
      });
}

const selectTreeNode = function (selectedKeys, e) {
  if (!e.node.dir) {
    Http.get(ApiUrls.db.getfilecontent, {path: e.node.basicData})
        .then(function (res) {
          fileContent.value = ''
          var html = ''
          for (let c of res.data.data) {
            // 全局替换
            // c = c.replace(new RegExp("&", 'gm'), "&amp;").replace(new RegExp("<", 'gm'), "&lt;").replace(new RegExp(">", 'gm'), "&gt;");
            html += c + "</br>"
          }
          fileContent.value = html;
        })
        .catch(function (error) {
          console.log(error);
        });
  }
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

.liInfo_div {
  border: solid 1px #CCC;
  padding: 10px;
  margin-top: 10px;
  height: auto;
}
</style>