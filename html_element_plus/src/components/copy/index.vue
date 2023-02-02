<template>
  <div class="right">
    <el-icon v-if="showCopyIcon" class="op" @click="copyClick">
      <CopyDocument />
    </el-icon>
    <el-icon v-else class="op"><Select /></el-icon>
  </div>
</template>

<script setup>
import {ref} from 'vue'
import {CopyDocument,Select} from '@element-plus/icons-vue'
import {ElMessage} from 'element-plus'

import { useClipboard } from '@vueuse/core'
const { copy, isSupported } = useClipboard()

const props = defineProps({
  content: {
    type: String
  }
})

const showCopyIcon = ref(true)
const copyClick = ()=>{
  if (!isSupported) {
    ElMessage.error("您的浏览器不支持复制功能 (Clipboard API)");
    return;
  }
  copy(props.content)
  // ElMessage.success('copy success')
  showCopyIcon.value=false
  setTimeout(() => showCopyIcon.value = true, 3000);
}

</script>
<style>
.right {
  position: absolute;right: 10px;top: 3px;
}
.op{
  cursor: pointer;
}
</style>