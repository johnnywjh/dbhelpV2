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
      <a-row></a-row>
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
import LocalData from "@/utils/localData"
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
const fieldName = ref('group_type')
const dbName = ref()
const search = function () {
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
      });
}

function query() {
  let key = dbName.value;
  let name = fieldName.value;
  var tables = DbData.getTables(key);
  var arr1 = [];
  var arr2 = [];
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
      arr1.push(obj);
    } else {
      arr2.push(obj);
    }
  }
  console.log(arr1)
  console.log(arr2)
}
</script>
