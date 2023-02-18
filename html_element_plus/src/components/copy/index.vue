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
  if (!isSupported) {
    ElMessage.error("您的浏览器不支持复制功能 (Clipboard API)");
    return;
  }
  copy(props.content)
  copy(props.content)
  // ElMessage.success('copy success')
  showCopyIcon.value = false
  setTimeout(() => showCopyIcon.value = true, 3000);
}

</script>
<style>
.op {
  cursor: pointer;
}
</style>