<template>
  <div>
    <el-dialog
        v-model="visible" draggable
        :width="props.width"
        :title="props.title"
    >

      <el-form :inline="true" style="margin-left: 50px">
        <el-row>
          <el-form-item label="表名称">
            <el-input v-model="tableName"/>
          </el-form-item>
          <el-form-item label="选择数据库">
            <el-select v-model="dbName" style="width: 200px">
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
          <el-form-item label="选择数据库">
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
            <el-button v-if="tableName && dbName" @click="search">搜索</el-button>
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
const tableName = ref('')


var startTime;
var endTime;
const search = function () {

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
}


</script>
