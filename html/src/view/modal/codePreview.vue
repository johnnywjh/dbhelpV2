<!-- 代码预览弹出层 -->
<template>
  <div>
    <a-modal
        v-model:visible="visible" :footer="false"
        :width="props.width"
        :title="props.title"
        @cancel="previewClose">
      <a-row :gutter="16">
        <a-col :span="6">
          <a-directory-tree
              multiple
              :tree-data="treeData"
              @select="selectTreeNode"
          ></a-directory-tree>
        </a-col>
        <a-col :span="18">
          <!--          <a-space>-->
          <!--            代码样式-->
          <!--            <a-switch v-model:checked="previewCode" checked-children="开" un-checked-children="关"/>-->
          <!--          </a-space>-->

          <a-typography-paragraph :code="previewCode" copyable class="liInfo_div">
            <!--          <pre>-->
            <!--<span v-html="fileContent"></span>-->
            <!--          </pre>-->
            <highlightjs language="java" :code="fileContent"/>
          </a-typography-paragraph>
        </a-col>
      </a-row>
    </a-modal>
  </div>
</template>

<script setup>
import {ref, defineExpose, computed} from 'vue'
import Http from "@/utils/Http";
import ApiUrls from "@/utils/ApiUrls";
import {message} from "ant-design-vue";

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

const dirVo = ref()
const treeData = ref()
const previewVisible = ref(false)
const fileContent = ref('')
const previewCode = ref(false)
const loadData = function (data) {
  Http.post(ApiUrls.db.preview, data)
      .then(function (res) {
        dirVo.value = res.data.data.dirVo
        treeData.value = res.data.data.list
        previewVisible.value = true
        fileContent.value = ''
      })
      .catch(function (error) {
        console.log(error);
      });
}
const previewClose = function () {
  Http.post(ApiUrls.db.deletedir, dirVo.value)
      .then(function (res) {
        message.success('清除成功')
      })
      .catch(function (error) {
        console.log(error);
      });
}

const selectTreeNode = function (selectedKeys, e) {
  if (!e.node.dir) {
    Http.get(ApiUrls.db.getfilecontent, {path: e.node.basicData})
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