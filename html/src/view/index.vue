<template>
  <div id="container">
    <div class="topDiv">
      <a-row :gutter="16">
        <a-col :span="4">
          <a-button class="topBut">Default Button</a-button>
        </a-col>
        <a-col :span="3">
          <a-upload
              :action="updateUrl"
              name="file"
              accept=".json"
              @change="handleFileChange">
            <a-button>
              上传数据源
            </a-button>
          </a-upload>
        </a-col>
        <a-col :span="16">
          <a-select
              v-model:value="dbKey"
              style="width: 200px"
              :options="dbList"
              @change="selectDbValue"
          ></a-select>
          &nbsp;
          <a-button @click="cleanDbCache">清除当前DB缓存</a-button>
          &nbsp;
          <a-button type="primary">对比数据库</a-button>
          &nbsp;
          <a-button type="primary">搜索字段</a-button>

        </a-col>
        <!--
                        <a-col :span="4">
                            <div class="gutter-box">col-6</div>
                        </a-col>
        -->

      </a-row>
    </div>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted} from "vue";
import DbData from '@/utils/DbData'

const updateUrl = ref('/api/user/readDbCofig')
const dbKey = ref('')


const handleFileChange = function (e) {
  if (e.file.status == 'done') {
    var res = e.fileList[0]
    if (res.status == 'done') {
      var data = res.response.data;
      console.log(data)
      DbData.setDb(data);
      console.log(DbData.getDb())
      // this.reloadDbSelect()
    }
  }
}
// 从本地缓存中读取数据库下拉框
const reloadDbSelect = function () {
  let vm = this;
  var dbList = getDb();
  vm.dbList = [{label: "选择数据-0", value: 0}];
  var count = 0;
  for (let l in dbList) {
    vm.dbList.push({label: l, value: l});
    count++;
  }
  vm.dbList[0].label = '选择数据-' + count
}

</script>

<style scoped>
#container {
  padding: 20px 50px;
}

.topDiv {
  margin-left: 10px;
}

.content {
  margin-top: 20px;
}

.content .topSpan {
  margin-left: 20px;
}
</style>