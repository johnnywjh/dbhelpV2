<template>
  <div>
    <a-modal
        v-model:visible="visible"
        :footer="false"
        :width="props.width"
        :title="props.title"
    >

      <a-form style="margin-left: 50px"
              layout="inline"
      >
        <a-row>
          <a-form-item label="字段名称">
            <a-input v-model:value="fieldName"/>
          </a-form-item>
          <a-form-item label="选择数据库">
            <a-select
                v-model:value="dbName"
                style="width: 200px"
                :options="props.dbList"
            />
          </a-form-item>
        </a-row>
        <a-row style="margin-top: 10px">
          <a-form-item label="选择数据库">
            <a-select
                v-model:value="diffTypeVal"
                style="width: 200px"
                :options="diffTypeList"
            ></a-select>
          </a-form-item>

          <a-form-item>
            <a-button v-if="fieldName && dbName" @click="search">搜索</a-button>
            <a-button v-else disabled>搜索</a-button>
            <span v-html="startTitle"></span>
          </a-form-item>
        </a-row>
      </a-form>
      <a-row style="margin-top: 10px">
        <a-tabs v-model:activeKey="activeKey">
          <a-tab-pane key="1">
            <template #tab>
              存在的表
              <a-tag color="red">{{ tableList1.length }}</a-tag>
            </template>
            <a-table :data-source="tableList1" :columns="columns"
                     :pagination="false" size="small"
            >
              <template #bodyCell="{ column, text, record }">
                <template v-if="column.dataIndex === 'tableName'">
                  <a-typography-text copyable>{{ record.tableName }}</a-typography-text>
                </template>
              </template>
            </a-table>
          </a-tab-pane>
          <a-tab-pane key="2">
            <template #tab>
              不存在的表
              <a-tag color="red">{{ tableList2.length }}</a-tag>
            </template>
            <a-table :data-source="tableList2" :columns="columns"
                     :pagination="false" size="small"
            >
              <template #bodyCell="{ column, text, record }">
                <template v-if="column.dataIndex === 'tableName'">
                  <a-typography-text copyable>{{ record.tableName }}</a-typography-text>
                </template>
              </template>
            </a-table>
          </a-tab-pane>
        </a-tabs>
      </a-row>
    </a-modal>
  </div>
</template>

<script setup>
import {ref, defineExpose} from 'vue'

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
import Http from '@/utils/Http'
import ApiUrls from '@/utils/ApiUrls'
import DbData from "@/utils/DbData";

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
  Http.post(ApiUrls.db.queryDbTAbleInfo, db)
      .then(function (res) {
        var list = res.data.data;
        DbData.setTables(key, list);
        startTitle.value = ''
        query()
      })
      .catch(function (error) {
        console.log(error);
        startTitle.value = '加载 ' + key + ' 的表结构 ==> 失败'
      });
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
const columns = ref([
  {title: '序号', dataIndex: 'index'},
  {title: '表名', dataIndex: 'tableName'},
  {title: '注释', dataIndex: 'comment'},
  {title: '个数', dataIndex: 'count'},
])
</script>
