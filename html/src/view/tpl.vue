<template>
  <div id="container">
    <switch-theme v-show="false"/>
    <div class="topDiv">
      <el-space>
        <input type="file" ref="fileInput" @change="handleFileChange" accept=".json"/>

        <el-select v-model="selectKey" filterable @change="selectVersionValue" placeholder="选择版本">
          <el-option
              v-for="item in keyList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-space>
    </div>
    <div class="content">
      <el-table border
                :data="dataSource"
                row-key="index"

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
import {ref, onMounted} from 'vue';
import SwitchTheme from "@/view/theme/switch-theme.vue"

let json_file_key = 'tpl_json_file_key'
let user_select_key = 'tpl_user_select_key'

onMounted(() => {
  let jsonStr = localStorage.getItem(json_file_key)
  if (jsonStr) {
    loadSelectData(jsonStr)
  }
})

const tpl_data = ref(null);
const table_column = ref(null);
const appNameKey = ref(null);

const keyList = ref([{label: "选择数据-0", value: 0}])
const selectKey = ref(null);
const dataSource = ref([])

const fileInput = ref(null);
const handleFileChange = () => {
  const reader = new FileReader();
  reader.onload = (event) => {
    localStorage.removeItem(json_file_key)
    let jsonStr = event.target.result;
    localStorage.setItem(json_file_key, jsonStr)

    loadSelectData(jsonStr)

    // console.log(JSON.stringify(tpl_data.value))
    // console.log(JSON.stringify(table_column.value))

    getTableData()
  };
  reader.readAsText(fileInput.value.files[0]);
}

const loadSelectData = (jsonStr) => {
  let jsonData = JSON.parse(jsonStr);
  tpl_data.value = jsonData.tpl_data
  table_column.value = jsonData.table_column
  appNameKey.value = jsonData.appNameKey

  let user_select_val = localStorage.getItem(user_select_key)
  let isLoadTable = false;
  keyList.value = []
  for (let key in tpl_data.value) {
    keyList.value.push({label: key, value: key})
    if(user_select_val){
      if (key == user_select_val) {
        isLoadTable = true;
      }
    }
  }


  if (isLoadTable && user_select_val) {
    selectKey.value = user_select_val
    getTableData()
  }
}

// 生成表格数据
const getTableData = () => {
  // dataSource.value = []
  let dataSourceArr = []
  var list = tpl_data.value[selectKey.value]
  let index = 1;
  for (let d of list) {
    let data = {
      index: index,
      appName: d
    }
    for (let c of table_column.value) {
      // data[c.key + '_key'] = c.key
      let appNameKeyStr = appNameKey.value || 'appName'
      let url = c.url.replace(appNameKeyStr, d)
      let styleStr = '';
      if (c.key.indexOf('dev') > -1) {
        styleStr = 'a_dev'
      } else if (c.key.indexOf('test') > -1) {
        styleStr = 'a_test'
      } else if (c.key.indexOf('pre') > -1) {
        styleStr = 'a_pre'
      } else if (c.key.indexOf('prod') > -1) {
        styleStr = 'a_prod'
      }
      let linkHtml = `<a href="${url}" class="${styleStr}" target='_blank'>链接</a>`
      data[c.key + '_url'] = linkHtml
    }
    dataSourceArr.push(data)
    index++
  }
  dataSource.value = dataSourceArr
  // console.log(dataSourceArr)
}

const selectVersionValue = () => {
  localStorage.setItem(user_select_key, selectKey.value)
  getTableData()
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

a {

}

.a_dev {
  color: #31BDEC;
}

.a_test {
  color: #16b777;
}

.a_pre {
  color: #FFB800;
}

.a_prod {
  color: #FF5722;
}

</style>
