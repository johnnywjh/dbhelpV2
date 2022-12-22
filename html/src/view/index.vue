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
import {message, Modal} from 'ant-design-vue';
import Http from '@/utils/Http'
import LocalData from "@/utils/localData";

// 页面初始加载
onMounted(() => {
  reloadDbSelect()
});

const updateUrl = ref('/api/user/readDbCofig')
const dbKey = ref('')
const dbList = ref([{label: "选择数据-0", value: 0}])
const tableList = ref([])
const searchTableText = ref('')
const userinfo = ref({db: {}})

const handleFileChange = function (e) {
  if (e.file.status == 'done') {
    var res = e.fileList[0]
    if (res.status == 'done') {
      var data = res.response.data;
      DbData.setDb(data)
      reloadDbSelect()
    }
  }
}
// 从本地缓存中读取数据库下拉框
const reloadDbSelect = function () {
  var arr = [{label: "选择数据", value: 0}];
  var count = 0;
  for (let l in DbData.getDb()) {
    arr.push({label: l, value: l});
    count++;
  }
  // arr[0].label = '选择数据-' + count
  dbList.value = arr
}

const selectDbValue = function () {
  let val = dbKey.value
  if (val != '0') {
    var db = DbData.getDb()[val];
    db.key = val;

    userinfo.value.db = db;
    DbData.setUser(userinfo.value);

    reLoadTables();
    // searchTableText.value = '';
  } else {
    tableList.value = [];
  }
}
// 搜索输入框的回车键事件
const reLoadTables = function () {
  var key = userinfo.value.db.key;
  if (key == 0 || !key) {
    message.warning('需要选择一个数据源');
    return;
  }
  var list = DbData.getTables(key);
  tableList.value = list;

  if (list) {
    filterList(list);
  } else {
    Http.post('/api/db/getTables', userinfo.value.db)
        .then(function (res) {
          var list = res.data.data;
          DbData.setTables(key, list);
          filterList(list);
        })
        .catch(function (error) {
          console.log(error);
        });
  }
  const filterList = function (list) {
    if (searchTableText.value) {
      var contentArr = searchTableText.value.split(",");
      var arr = [];
      for (let l of list) {
        for (let content of contentArr) {
          let includesFlag = false;
          if (l.tableName.includes(content)) {
            l.tableNameStr = l.tableName.replace(new RegExp(content, 'g'), `<b class="searchText">${content}</b>`);
            includesFlag = true;
          }
          if (l.comment) {
            if (l.comment.includes(content)) {
              l.commentStr = l.comment.replace(new RegExp(content, 'g'), `<b class="searchText">${content}</b>`);
              includesFlag = true;
            }
          }
          if (includesFlag) {
            arr.push(l)
          }
        }
      }
      list = arr;
    }
    if (list) {
      for (var i = 0; i < list.length; i++) {
        list[i].index = i + 1;
        if (!list[i].tableNameStr) {
          list[i].tableNameStr = list[i].tableName;
        }
        if (!list[i].commentStr) {
          list[i].commentStr = list[i].comment;
        }
        list[i].index = i + 1;
      }
    }
    tableList.value = list;
    console.log(tableList.value)
  }
}

// 清除数据库缓存
const cleanDbCache = function () {
  if (dbKey.value == '0') {
    return;
  }
  Modal.confirm({
    title: '确定要清除 ' + dbKey.value + ' 的所有缓存?',
    onOk() {
      DbData.cleanDb()
      setTimeout(() => location.reload(), 1000);
    },
    class: 'test',
  });
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