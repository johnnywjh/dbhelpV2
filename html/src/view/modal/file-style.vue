<template>
  <div>
    <el-dialog
        v-model="visible"
        draggable
        :width="props.width"
        :title="props.title"
    >
      <div class="liInfo_div_ex">
        <copy :content="layerFileText" />
        <highlightjs language="json" :code="layerFileText"/>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref} from 'vue'

const visible = ref(false)
const props = defineProps({
  title: {
    type: String,
    default: '数据源格式'
  },
  width: {
    type: String,
    default: '600px'
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
    '            "url": "jdbc:mysql://localhost:3306/xxx?useUnicode=true&characterEncoding=UTF-8&useSSL=false",\n' +
    '            "name": "root",\n' +
    '            "pwd": ""\n' +
    '        }\n' +
    '        ,{...}\n' +
    '    }';

</script>
