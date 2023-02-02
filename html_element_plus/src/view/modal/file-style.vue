<template>
  <div>
    <el-dialog
        v-model="visible"
        draggable
        :width="props.width"
        :title="props.title"
    >
      <div class="liInfo_div_ex">
        <highlightjs language="json" :code="layerFileText"/>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, defineExpose} from 'vue'

const visible = ref(false)
const props = defineProps({
  title: {
    type: String,
    default: '数据源格式'
  },
  width: {
    type: String,
    default: '400px'
  }
})

// 子组件的弹出方法 ==> 父组件调用
const show = (data) => {
  visible.value = true
}
// **重点！！这里需要使用defineExpose暴露出去**
defineExpose({show})

//=================
// 界面的逻辑
//=================
const layerFileText = '    {\n' +
    '        "dbName":{\n' +
    '            "url": "jdbc:mysql://",\n' +
    '            "name": "root",\n' +
    '            "pwd": ""\n' +
    '        }\n' +
    '        ,{...}\n' +
    '    }';

</script>
