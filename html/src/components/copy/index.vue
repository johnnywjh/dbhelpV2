<template>
  <span :style="props.styleVal">
    <el-icon v-if="showCopyIcon" class="op" @click="copyClick">
      <CopyDocument/>
    </el-icon>
    <el-icon v-else class="op"><Select/></el-icon>
  </span>
</template>

<script setup>
import {ref} from 'vue'
import {CopyDocument, Select} from '@element-plus/icons-vue'
import {ElMessage} from 'element-plus'

import {useClipboard} from '@vueuse/core'

const {copy, isSupported} = useClipboard()

const props = defineProps({
  content: {
    type: String
  },
  styleVal: {
    type: Object,
    default: {position: 'absolute', right: '10px', top: '3px'}
  }
})

const showCopyIcon = ref(true)
const copyClick = () => {
  if(!props.content){
    ElMessage.error("复制内容为空");
    return;
  }

  // if (!isSupported) {
  //   ElMessage.error("您的浏览器不支持复制功能 (Clipboard API)");
  //   return;
  // }

  // copy(props.content)   // 这个在linux docker 上无效,不知道是什么原因
  // showCopyIcon.value = false
  // setTimeout(() => showCopyIcon.value = true, 3000);

  copyText(props.content)
}

function copyText(val){
  // 动态创建 textarea 标签
  const textarea = document.createElement('textarea');
  // 将该 textarea 设为 readonly 防止 iOS 下自动唤起键盘，同时将 textarea 移出可视区域
  textarea.readOnly = 'readonly';
  textarea.style.position = 'absolute';
  textarea.style.left = '-9999px';
  // 将要 copy 的值赋给 textarea 标签的 value 属性
  textarea.value = val;
  // 将 textarea 插入到 body 中
  document.body.appendChild(textarea);
  // 选中值并复制
  textarea.select();
  textarea.setSelectionRange(0, textarea.value.length);
  const result = document.execCommand('Copy');
  if (result) {
    // console.log('复制成功');
    showCopyIcon.value = false
    setTimeout(() => showCopyIcon.value = true, 3000);
  }
  document.body.removeChild(textarea);
}

</script>
<style>
.op {
  cursor: pointer;
}
</style>