<template>
  <div>
    <el-dialog
        v-model="visible" draggable
        :width="props.width"
        :title="props.title"
    >

      <el-form :inline="true" style="margin-left: 50px">
        <el-row>
          <el-form-item label="字段名称">
            <el-input v-model="fieldName"/>
          </el-form-item>
          <el-form-item label="选择数据库">
            <el-select v-model="dbName" filterable style="width: 200px">
              <el-option
                  v-for="item in props.dbList"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-row>
        <el-row style="margin-top: 10px">
          <el-form-item label="数据源方式">
            <el-select v-model="diffTypeVal" style="width: 200px">
              <el-option
                  v-for="item in diffTypeList"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button v-if="fieldName && dbName" @click="search">搜索</el-button>
            <el-button v-else disabled>搜索</el-button>
            <span v-html="startTitle"></span>
          </el-form-item>
        </el-row>
      </el-form>
      <el-row style="margin-top: 10px">
        <el-tabs v-model="activeKey">
          <el-tab-pane key="1">
            <template #label>
              存在的表
              <el-tag type="danger">{{ tableList1.length }}</el-tag>
            </template>
            <el-table border
                      :data="tableList1"
                      row-key="index"
            >
              <el-table-column prop="index" label="序号" width="60"/>
              <el-table-column prop="tableName" label="表名" width="200">
                <template #default="scope">
                  <span>{{ scope.row.tableName }}</span>
                  <copy :content="scope.row.tableName" :style-val="{'margin-left':'5px'}"/>
                </template>
              </el-table-column>
              <el-table-column prop="comment" label="注释" width="200"/>
              <el-table-column prop="count" label="个数"/>
            </el-table>
          </el-tab-pane>
          <el-tab-pane key="2">
            <template #label>
              不存在的表
              <el-tag type="danger">{{ tableList2.length }}</el-tag>
            </template>
            <el-table border
                      :data="tableList2"
                      row-key="index"
            >
              <el-table-column prop="index" label="序号" width="60"/>
              <el-table-column prop="tableName" label="表名" width="200">
                <template #default="scope">
                  <span>{{ scope.row.tableName }}</span>
                  <copy :content="scope.row.tableName" :style-val="{'margin-left':'5px'}"/>
                </template>
              </el-table-column>
              <el-table-column prop="comment" label="注释" width="200"/>
              <el-table-column prop="count" label="个数"/>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </el-row>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref} from 'vue'

const visible = ref(false)
const props = defineProps({
  title: {
    type: String,
    default: '搜索字段'
  },
  width: {
    type: String,
    default: '900px'
  },
  dbList: {
    type: Array,
    default: []
  }
})

// 子组件的弹出方法 ==> 父组件调用
const show = (data) => {
  visible.value = true
  loadData(data)
}
// **重点！！这里需要使用defineExpose暴露出去**
defineExpose({show})

//=================
// 界面的逻辑
//=================
import DbData from "@/utils/DbData";
import {apiQueryDbTAbleInfo} from "@/api/buss";

const loadData = function (data) {
  dbName.value = data.dbKey
}
const diffTypeList = ref([
  {label: "直接用缓存对比--cache", value: 1},
  {label: "重新加载数据", value: 2}
])
const startTitle = ref('')
const diffTypeVal = ref(1)
const fieldName = ref('')
const dbName = ref()

var startTime;
var endTime;
const search = function () {
  tableList1.value = []
  tableList2.value = []
  startTime = new Date().getTime();
  if (diffTypeVal.value == 1) {
    let tables = DbData.getTables(dbName.value)
    if (tables) {
      var flg = true;
      for (let l of tables) {
        if (!flg) {
          break;
        }
        flg = flg && l.columns;
      }
      if (flg) {
        query();
      } else {
        reloadTableInfo();
      }
    } else {
      reloadTableInfo()
    }
  } else {
    reloadTableInfo()
  }
}

function reloadTableInfo() {
  let key = dbName.value;
  let dbMap = DbData.getDb();
  let db = dbMap[key];
  startTitle.value = '加载 ' + key + ' 的表结构 ...'
  apiQueryDbTAbleInfo(db,(res)=>{
    var list = res.data.data;
    DbData.setTables(key, list);
    startTitle.value = ''
    query()
  })
  // Http.post(ApiUrls.db.queryDbTAbleInfo, db)
  //     .then(function (res) {
  //
  //     })
  //     .catch(function (error) {
  //       console.log(error);
  //       startTitle.value = '加载 ' + key + ' 的表结构 ==> 失败'
  //     });
}

const tableList1 = ref([])
const tableList2 = ref([])

function query() {
  let key = dbName.value;
  let name = fieldName.value;
  var tables = DbData.getTables(key);
  var arr1 = [];
  var arr2 = [];
  let x = 1
  let y = 1
  for (let t of tables) {
    var count = 0;

    var columns = t.columns;
    if (columns) {
      for (let c of t.columns) {
        if (name == c.name) {
          count++;
        }
      }
    }
    var obj = {
      tableName: t.tableName
      , comment: t.comment
      , count: count
    };

    if (count > 0) {
      obj.index = x;
      x++
      arr1.push(obj);
    } else {
      obj.index = y;
      y++
      arr2.push(obj);
    }
  }
  tableList1.value = arr1
  tableList2.value = arr2
  endTime = new Date().getTime();
  startTitle.value = `搜索结束,耗时` + ((endTime - startTime) / 1000) + ' 秒'
}

const activeKey = ref('1')

</script>
