<template>
  <div>

  </div>
</template>

<script setup>
import {ref, computed} from "vue";

const props = defineProps(['fileList','expandedKeys'])
const selectedKeys = ref([]);

const expandedKeys = ref(['0-0', '0-1']);
const treeData = [{
  title: 'parent 0',
  key: '0-0',
  children: [{
    title: 'leaf 0-0',
    key: '0-0-0',
    isLeaf: true,
  }, {
    title: 'leaf 0-1',
    key: '0-0-1',
    isLeaf: true,
  }],
}, {
  title: 'parent 1',
  key: '0-1',
  children: [{
    title: 'leaf 1-0',
    key: '0-1-0',
    isLeaf: true,
  }, {
    title: 'leaf 1-1',
    key: '0-1-1',
    isLeaf: true,
  }],
}];
</script>

<style >

</style>