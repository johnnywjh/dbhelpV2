<template>
  <div>
    <el-dialog
        v-model="visible" draggable
        :width="props.width"
        :title="props.title"
    >

      <el-form :inline="true" style="margin-left: 50px">
        <el-row>
          <el-checkbox-group v-model="selectKeys">
            <el-checkbox v-for="item in props.dbList" :label="item" />
          </el-checkbox-group>
        </el-row>
        <el-row>
          <el-form-item label="表名称">
            <el-input v-model="tableName"/>
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
    default: '搜索字段'
  },
  width: {
    type: String,
    default: '900px'
  },
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
const dbList = ref([])
const selectKeys = ref([])
const loadData = function (data) {
  dbList.value = data
}



</script>
