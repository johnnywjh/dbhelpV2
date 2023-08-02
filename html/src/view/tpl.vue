<template>
  <div id="container">
    <switch-theme v-show="false"/>
    <div class="topDiv">
      <el-space>
        <input type="file" ref="fileInput" @change="handleFileChange" accept=".json"/>
      </el-space>
    </div>
    <div class="content">
      <el-table border
                :data="dataSource"
                row-key="index"
                size="small"
      >

        <el-table-column v-for="(item,key,index) in dataSource[0]"
                         :key="index" :label="key" width="150">
          <template #default="scope">
            <span v-html="scope.row[key]"></span>
            <!--          <copy :content="scope.row[key]" :style-val="{'margin-left':'5px'}"/>-->
          </template>
        </el-table-column>

      </el-table>
    </div>

  </div>
</template>

<script setup>
import {ref} from 'vue';
import SwitchTheme from "@/view/theme/switch-theme.vue"

const tpl_data = ref(null);
const table_column = ref(null);

const select_key = ref(null);
const dataSource = ref([])

const fileInput = ref(null);
const handleFileChange = () => {
  const reader = new FileReader();
  reader.onload = (event) => {
    let jsonData = JSON.parse(event.target.result);
    tpl_data.value = jsonData.tpl_data
    table_column.value = jsonData.table_column

    select_key.value = 'v1.2.3'
    // console.log(JSON.stringify(tpl_data.value))
    // console.log(JSON.stringify(table_column.value))

    getTableData()
  };
  reader.readAsText(fileInput.value.files[0]);
}

// 生成表格数据
const getTableData = () => {
  // dataSource.value = []
  let dataSourceArr = []
  var list = tpl_data.value[select_key.value]
  let index = 1;
  for (let d of list) {
    let data = {
      index: index,
      appName: d
    }
    for (let c of table_column.value) {
      // data[c.key + '_key'] = c.key
      let url = c.url.replace('[appName]', d)
      let linkHtml = `<a href="${url}" type="success" target='_blank'>链接</a>`
      data[c.key + '_url'] = linkHtml
    }
    dataSourceArr.push(data)
    index++
  }
  dataSource.value = dataSourceArr
  console.log(dataSourceArr)
}

</script>
<style>
#container {
  padding: 10px 50px;
}
.topDiv {
  margin-left: 50px;
}
.content {
  margin-top: 20px;
}
a{

}
.a_dev{
  color: blue;
}
.a_test{
  color: green;
}
.a_pre{
  color: #67C23A;
}
.a_prod{
  color: #67C23A;
}

</style>
