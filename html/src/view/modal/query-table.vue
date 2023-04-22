<template>
  <div>
    <el-dialog
        v-model="visible" draggable
        :width="props.width"
        :title="props.title"
        :close-on-click-modal="false"
    >

      <el-form :inline="true" style="margin-left: 50px">
        <el-row>
          <el-form-item label="选择数据库">
            <el-select v-model="selectKeys" multiple filterable style="width: 700px">
              <el-option
                  v-for="item in dbArr"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-row>
        <el-row>
          <el-form-item label="表名称">
            <el-input v-model="tableName" style="width: 200px"/>
          </el-form-item>
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
            <el-button v-if="tableName && selectKeys.length>0" @click="search">开始搜索</el-button>
            <el-button v-else disabled>搜索</el-button>
            <span style="margin-left: 10px;" v-html="startTitle"></span>
          </el-form-item>
        </el-row>
      </el-form>
      <div>
        <span v-html="startTitle"></span>
      </div>
      <el-row :gutter="20" style="margin-top: 50px">
        <el-col :span="12">
          <span>存在的数据库</span>
          <el-table border
                    :data="tableList1"
                    row-key="index"
                    size="small"
          >
            <el-table-column prop="index" label="序号" width="60"/>
            <el-table-column prop="tableName" label="数据库名">
              <template #default="scope">
                <span>{{ scope.row.dbName }}</span>
                <copy :content="scope.row.dbName" :style-val="{'margin-left':'5px'}"/>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
        <el-col :span="12">
          <span>不存在的数据库</span>
          <el-table border
                    :data="tableList2"
                    row-key="index"
          >
            <el-table-column prop="index" label="序号" width="60"/>
            <el-table-column prop="tableName" label="数据库名">
              <template #default="scope">
                <span>{{ scope.row.dbName }}</span>
                <copy :content="scope.row.dbName" :style-val="{'margin-left':'5px'}"/>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
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
    default: '搜索表名'
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
import {apiGetTables} from "@/api/buss";

const tableName = ref('')
const dbArr = ref([])
const selectKeys = ref([])
const loadData = function (data) {
  dbArr.value = []
  for (let l of props.dbList) {
    if (l.value != 0) {
      dbArr.value.push(l)
    }
  }
}

const diffTypeList = ref([
  {label: "直接用缓存对比--cache", value: 1},
  {label: "重新加载数据", value: 2}
])
const diffTypeVal = ref(1)
const startTitle = ref('')
const tableList1 = ref([])
const tableList2 = ref([])
const search = function () {
  tableList1.value = []
  tableList2.value = []

  for (let dbName of selectKeys.value) {
    if (diffTypeVal.value == 1) {
      let tables = DbData.getTables(dbName)
      if (tables) {
        query(dbName);
      } else {
        reloadTableInfo(dbName)
      }
    } else {
      reloadTableInfo(dbName)
    }
  }
}

function reloadTableInfo(dbName) {
  let key = dbName;
  let dbMap = DbData.getDb();
  let db = dbMap[key];
  startTitle.value = '加载 ' + key + ' 的表结构 ...'
  apiGetTables(db, (res) => {
    var list = res.data.data;
    DbData.setTables(key, list);
    startTitle.value = ''
    query(dbName)
  })
}

function query(dbName) {
  let tables = DbData.getTables(dbName)
  let flag = false
  for (let t of tables) {
    if (t.tableName == tableName.value) {
      flag = true
      break
    }
  }
  var obj = {dbName: dbName}
  if (flag) {
    obj.index = tableList1.value.length + 1
    tableList1.value.push(obj)
  } else {
    obj.index = tableList2.value.length + 1
    tableList2.value.push(obj)
  }
}


</script>
