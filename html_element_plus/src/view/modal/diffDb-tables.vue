<!-- 对比数据库 -->
<template>
  <div>
    <el-dialog
        v-model="visible" draggable
        :width="props.width"
        :title="props.title"
    >
<!--      :close-on-click-modal="false"-->
      <el-row>
        <el-checkbox-group v-model="selectKeys">
          <el-checkbox v-for="item in dbList" :label="item" />
        </el-checkbox-group>
      </el-row>
      <el-row style="margin-top: 10px">
        <el-space>
          <el-select v-model="diffTypeVal" style="width: 200px">
            <el-option
                v-for="item in diffTypeList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
          <el-button v-if="selectKeys.length==2" @click="startDiff">开始对比</el-button>
          <el-button v-else-if="selectKeys.length>2" disabled>只能选择两个</el-button>
          <el-button v-else disabled>开始对比</el-button>
          <span v-html="startTitle"></span>
        </el-space>
      </el-row>
      <el-row :gutter="20" style="margin-top: 50px">
        <el-col :span="12">
          <span>{{ tableTitle1 }}</span>
          <el-table border
                    :data="diffList1"
                    row-key="index"
                    size="small"
          >
            <el-table-column prop="index" label="序号" width="60"/>
            <el-table-column prop="tableName" label="表名">
              <template #default="scope">
                <span>{{scope.row.tableName}}</span>
                <copy :content="scope.row.tableName" :style-val="{'margin-left':'5px'}" />
              </template>
            </el-table-column>
            <el-table-column prop="comment" label="注释"/>
          </el-table>
        </el-col>
        <el-col :span="12">
          <span>{{ tableTitle2 }}</span>
          <el-table border
                    :data="diffList2"
                    row-key="index"
          >
            <el-table-column prop="index" label="序号" width="60"/>
            <el-table-column prop="tableName" label="表名">
              <template #default="scope">
                <span>{{scope.row.tableName}}</span>
                <copy :content="scope.row.tableName" :style-val="{'margin-left':'5px'}" />
              </template>
            </el-table-column>
            <el-table-column prop="comment" label="注释"/>
          </el-table>
        </el-col>
      </el-row>

    </el-dialog>
  </div>
</template>

<script setup>
import {ref, defineExpose} from 'vue'

const visible = ref(false)
const props = defineProps({
  title: {
    type: String,
    default: '选择两个数据库进行对比'
  },
  width: {
    type: String,
    default: '800px'
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
const dbList = ref([])
const loadData = function (data) {
  dbList.value = data
}

import ApiUrls from '@/utils/ApiUrls'
import {ElMessage} from 'element-plus'
import DbData from "@/utils/DbData";
import Http from "@/utils/Http";

const startTitle = ref('')
const selectKeys = ref([])
const diffTypeList = ref([
  {label: "直接用缓存对比--cache", value: 1},
  {label: "重新加载数据", value: 2}
])
const diffTypeVal = ref(1)

const tableTitle1 = ref('')
const tableTitle2 = ref('')
const diffList1 = ref([])
const diffList2 = ref([])
const diffColumns = ref([
  {title: '序号', dataIndex: 'index'},
  {title: '表名', dataIndex: 'tableName'},
  {title: '注释', dataIndex: 'comment'},
])

const startDiff = function () {
  let dbKey1 = selectKeys.value[0]
  let dbKey2 = selectKeys.value[1]
  let dbMap = DbData.getDb();
  let db1 = dbMap[dbKey1];
  let db2 = dbMap[dbKey2];
  // console.log(`dbKey1 : ${dbKey1} . dbKey2 : ${dbKey2}`)

  if (db1 && db2) {
    loadDbAndDiff(dbKey1, dbKey2, db1, db2)
  } else {
    ElMessage.warning('数据源获取失败');
  }

}

var startTime;
var endTime;

// 加载并比较数据库
function loadDbAndDiff(dbKey1, dbKey2, db1, db2) {
  startTime = new Date().getTime();
  var list1 = [];
  var list2 = [];
  if (diffTypeVal.value == 1) {
    list1 = DbData.getTables(dbKey1);
    list2 = DbData.getTables(dbKey2);
  } else {
    // console.log("diff ok")
    list1 = [];
    list2 = [];
  }
  getTable(dbKey1, db1, list1, function (resList1) {
    list1 = resList1
    getTable(dbKey2, db2, list2, function (resList2) {
      list2 = resList2
      diffDbData(dbKey1, dbKey2, list1, list2)
    })
  })
}

function getTable(key, db, list, fun) {
  if (list && list.length > 0) {
    fun(list)
  } else {
    startTitle.value = '加载 ' + key + ' 的表结构 ...'
    Http.post(ApiUrls.db.getTables, db)
        .then(function (res) {
          var list = res.data.data;
          DbData.setTables(key, list);
          startTitle.value = ''
          fun(list)
        })
        .catch(function (error) {
          console.log(error);
          startTitle.value = '加载 ' + key + ' 的表结构 ==> 失败'
        });
  }
}

// 对比数据库
function diffDbData(dbKey1, dbKey2, list1, list2) {
  var arr1 = [];
  var arr2 = [];
  for (let l1 of list1) {
    var flag = true;
    for (let l2 of list2) {
      if (l1.tableName == l2.tableName) {
        flag = false;
        break;
      }
    }
    if (flag) {
      arr1.push(l1);
    }
  }
  for (let l2 of list2) {
    var flag = true;
    for (let l1 of list1) {
      if (l1.tableName == l2.tableName) {
        flag = false;
        break;
      }
    }
    if (flag) {
      arr2.push(l2);
    }
  }
  for (let i = 0; i < arr1.length; i++) {
    arr1[i].index = i + 1
  }
  for (let i = 0; i < arr2.length; i++) {
    arr2[i].index = i + 1
  }
  diffList1.value = arr1
  diffList2.value = arr2

  tableTitle1.value = dbKey1 + '多出来的表'
  tableTitle2.value = dbKey2 + '多出来的表'

  endTime = new Date().getTime();
  startTitle.value = `${dbKey1} <span style="color: red"> ${list1.length}</span> 个表 , ${dbKey2} <span style="color: red"> ${list2.length} </span> 个表 对比结束,耗时` + ((endTime - startTime) / 1000) + ' 秒'
  // console.log(dbKey1)
  // console.log(arr1)
  // console.log('================')
  // console.log(dbKey2)
  // console.log(arr2)
}

</script>
