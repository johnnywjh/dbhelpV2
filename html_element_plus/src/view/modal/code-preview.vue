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
          <el-tree :data="treeData" :props="defaultProps" @node-click="selectTreeNode" />
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
import {ref, defineExpose, computed} from 'vue'
import Http from "@/utils/Http";
import ApiUrls from "@/utils/ApiUrls";
import {ElMessage} from 'element-plus'

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
// const previewCode = ref(false)
const loadData = function (data) {
  Http.post(ApiUrls.db.preview, data)
      .then(function (res) {
        dirVo.value = res.data.data.dirVo
        treeData.value = res.data.data.list
        // previewVisible.value = true
        fileContent.value = ''
      })
      .catch(function (error) {
        console.log(error);
      });
}
const previewClose = function () {
  Http.post(ApiUrls.db.deletedir, dirVo.value)
      .then(function (res) {
        ElMessage.success('清除成功')
      })
      .catch(function (error) {
        console.log(error);
      });
}

const selectTreeNode = function (node) {
  if (!node.dir) {
    Http.get(ApiUrls.db.getfilecontent, {path: node.basicData})
        .then(function (res) {
          fileContent.value = ''
          var html = ''
          for (let c of res.data.data) {
            // 全局替换
            // c = c.replace(new RegExp("&", 'gm'), "&amp;").replace(new RegExp("<", 'gm'), "&lt;").replace(new RegExp(">", 'gm'), "&gt;");
            html += c + "\n"
          }
          fileContent.value = html;
        })
        .catch(function (error) {
          console.log(error);
        });
  }
}

</script>