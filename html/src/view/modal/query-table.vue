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
            <el-select v-model="selectKeys" multiple filterable style="width: 400px">
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
        </el-row>
      </el-form>
      <el-row style="margin-top: 10px">

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
import {apiQueryDbTAbleInfo} from "@/api/buss";
const tableName = ref('')
const dbArr = ref([])
const selectKeys = ref([])
const loadData = function (data) {
  dbArr.value = []
  for(let l of props.dbList){
    if(l.value!=0){
      dbArr.value.push(l)
    }
  }
}


const diffTypeList = ref([
  {label: "直接用缓存对比--cache", value: 1},
  {label: "重新加载数据", value: 2}
])
const diffTypeVal = ref(1)


</script>
