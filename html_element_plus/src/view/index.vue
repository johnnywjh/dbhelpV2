<template>
  <div >

      <button @click="toggleDark()" class="border-none w-full bg-transparent cursor-pointer" style="height: var(--ep-menu-item-height);">
        <i inline-flex i="dark:ep-moon ep-sunny" />
        1111
      </button>

    1234
  </div>
</template>

<script setup>
import { toggleDark } from '@/composables';
</script>