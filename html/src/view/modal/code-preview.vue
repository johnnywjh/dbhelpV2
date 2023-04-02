<!-- 代码预览弹出层 -->
<template>
  <div>
    <el-dialog
        v-model="visible" draggable
        :width="props.width"
        :title="props.title"
        @cancel="previewClose">
      <el-row :gutter="16">
        <el-col :span="6">
          <el-tree
              node-key="key"
              :data="treeData"
              :default-expanded-keys="defaultExpandedKeys"
              :props="defaultProps"
              @node-click="selectTreeNode"
          />
        </el-col>
        <el-col :span="18">
          <div class="liInfo_div">
            <copy :content="fileContent"/>
            <highlightjs language="java" :code="fileContent"/>
          </div>
        </el-col>
      </el-row>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, computed} from 'vue'

import {ElMessage} from 'element-plus'
import {apiDeletedir, apiGetfilecontent, apiPreview} from "@/api/buss";

const visible = ref(false)
const props = defineProps({
  title: {
    type: String,
    default: '预览'
  },
  width: {
    type: String,
    default: '1300px'
  }
})

// 子组件的弹出方法 ==> 父组件调用
const show = (data) => {
  visible.value = true
  loadData(data)  // 加载数据
}
// **重点！！这里需要使用defineExpose暴露出去**
defineExpose({show})

//=================
// 界面的逻辑
//=================
const defaultProps = {
  children: 'children',
  label: 'title',
}

const dirVo = ref()
const treeData = ref()
// const previewVisible = ref(false)
const fileContent = ref('')
const defaultExpandedKeys = ref()
const loadData = function (data) {
  apiPreview(data, (res) => {
    dirVo.value = res.data.data.dirVo
    treeData.value = res.data.data.list
    var expandedKeys2 = []  // 计算出要展开的节点
    var expandedKeys3 = []  // 计算出要展开的节点
    var level = 3;// 找第三层的
    for (let t1 of res.data.data.list) {
      if (t1.children) {
        for (let t2 of t1.children) {
          expandedKeys2.push(t2.key)
          if (t2.children) {
            for (let t3 of t2.children) {
              expandedKeys3.push(t3.key)
            }
          }
        }
      }
    }
    if (expandedKeys3.length > 0) {
      defaultExpandedKeys.value = expandedKeys3
    } else {
      defaultExpandedKeys.value = expandedKeys2
    }

    // previewVisible.value = true
    fileContent.value = ''
  })
}
const previewClose = function () {
  apiDeletedir(dirVo.value, (res) => {
    ElMessage.success('清除成功')
  })
}

const selectTreeNode = function (node) {
  if (!node.dir) {
    apiGetfilecontent({path: node.basicData}, (res) => {
      fileContent.value = ''
      var html = ''
      for (let c of res.data.data) {
        // 全局替换
        // c = c.replace(new RegExp("&", 'gm'), "&amp;").replace(new RegExp("<", 'gm'), "&lt;").replace(new RegExp(">", 'gm'), "&gt;");
        html += c + "\n"
      }
      fileContent.value = html;
    })
  }
}

</script>