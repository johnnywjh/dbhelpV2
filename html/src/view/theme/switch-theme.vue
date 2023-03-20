<template>
  <el-switch
      v-model="isDark"
      inline-prompt
      :active-icon="Moon"
      :inactive-icon="Sunny"
      @change="change"
  />

</template>

<script setup>
import {ref} from "vue";
import {useDark, useToggle} from '@vueuse/core'
import {Moon, Sunny} from '@element-plus/icons-vue'

const isDark = useDark()

const change = () => {
  useToggle(isDark)
}
</script>