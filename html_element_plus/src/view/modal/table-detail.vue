<!-- 表格详情的弹出 -->
<template>
  <div>
    <el-dialog
        v-model="visible"
        draggable
        :width="props.width"
        :title="props.title"
    >
      <el-tabs v-model="activeKey">
        <el-tab-pane name="field" label="字段">
          <el-table border
                    :data="props.detailData.columns"
                    row-key="id"
                    size="small"
                    @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55"/>
            <el-table-column prop="name" label="字段" width="200"/>
            <el-table-column prop="type" label="类型" width="120"/>
            <el-table-column prop="comment" label="注释" width="200"/>
            <el-table-column prop="javaName" label="驼峰">
              <template #default="scope">
                <span v-if="scope.row.javaName!=scope.row.name">{{ scope.row.javaName }}</span>
                <span v-else></span>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane name="query" label="查询sql">
          <el-space>
            <el-input v-model="as" style="width: 100px;" placeholder="表的别名"/>
          </el-space>
          <!-- div1 -->
          <div class="liInfo_div">
            <copy :content="liInfo_div1"/>
            <highlightjs language="sql" :code="liInfo_div1"/>
          </div>
          <!-- div2 -->
          <div class="liInfo_div">
            <copy :content="liInfo_div2"/>
            <highlightjs language="sql" :code="liInfo_div2"/>
          </div>
          <!-- div3 -->
          <div class="liInfo_div">
            <copy :content="liInfo_div3"/>
            <highlightjs language="sql" :code="liInfo_div3"/>
          </div>
        </el-tab-pane>
        <el-tab-pane name="doc" label="文档">
          <el-tabs
              v-model="liInfo_doc_class"
              type="card"
          >
            <el-tab-pane label="普通" name="1">
              <div class="liInfo_div">
                <copy :content="liInfo_doc_1_copy"/>
                <div v-html="liInfo_doc_1"></div>
              </div>
            </el-tab-pane>
            <el-tab-pane label="java1" name="2">
              <div class="liInfo_div">
                <copy :content="liInfo_doc_2_copy"/>
                <div v-html="liInfo_doc_2"></div>
              </div>
            </el-tab-pane>
            <el-tab-pane label="java2" name="3">
              <div class="liInfo_div">
                <copy :content="liInfo_doc_3_copy"/>
                <div v-html="liInfo_doc_3"></div>
              </div>
            </el-tab-pane>
            <el-tab-pane label="markdown" name="4">
              <div class="liInfo_div">
                <copy :content="liInfo_doc_4_copy"/>
                <div v-html="liInfo_doc_4"></div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-tab-pane>
        <el-tab-pane name="ddl" label="DDL">
          <div class="liInfo_div">
            <copy :content="detailData.ddl"/>
            <highlightjs language="sql" :code="detailData.ddl"/>
          </div>
        </el-tab-pane>
        <el-tab-pane name="tableData" label="表数据">
          <el-form :inline="true" style="margin-left: 50px">
            <el-form-item label="排序字段">
              <el-select v-model="tableDbData.orderByName" filterable clearable placeholder="排序字段" style="width: 150px">
                <el-option
                    v-for="item in props.detailData.columns"
                    :key="item.name"
                    :label="item.name"
                    :value="item.name"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="排序类型">
              <el-switch v-model="tableDbData.orderByAsc"/>
            </el-form-item>
            <el-form-item label="limit">
              <el-input-number v-model="tableDbData.limit" :min="1" :max="100" />
            </el-form-item>
            <el-form-item>
              <el-button  @click="queryDbTable" type="primary">查询数据</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>

    </el-dialog>
  </div>
</template>

<script setup>
import {ref,reactive, defineExpose, computed} from 'vue'

const visible = ref(false)
const props = defineProps({
  title: {
    type: String,
    default: 'xxx:yyyy'
  },
  width: {
    type: String,
    default: '900px'
  },
  detailData: {
    type: Object,
    require: true,
  }
})

// 子组件的弹出方法 ==> 父组件调用
const show = () => {
  visible.value = true
  as.value = ''
}
// **重点！！这里需要使用defineExpose暴露出去**
defineExpose({show})

//=================
// 界面的逻辑
//=================
const activeKey = ref('field')
// const columns = ref([
//   {title: '字段', dataIndex: 'name', width: '200px'},
//   {title: '类型', dataIndex: 'type', width: '120px'},
//   {title: '注释', dataIndex: 'comment', ellipsis: true, width: '200px'},
//   {title: '驼峰', dataIndex: 'javaName'},
// ])
// 选中的表格数据
const detailDataSelectList = ref([])
// const rowSelect = {
//   onChange: (selectedRowKeys, selectedRows) => {
//     // console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
//     detailDataSelectList.value = selectedRows
//   }
// };
const handleSelectionChange = (val) => {
  detailDataSelectList.value = val
}

// const detailCodeShowSql = ref(false)
// const detailCodeShowDoc = ref(true)
// const detailCodeShowDDL = ref(false)
const as = ref('')
const ass = computed(() => {
  return as.value.length > 0 ? as.value + '.' : ''
})

const liInfo_div1 = computed(() => {
  let arr = [];
  for (let l of detailDataSelectList.value) {
    arr.push(`${ass.value}${l.name}`)
  }
  return 'select \n' + arr.join(' , ') + ` \n from ${props.detailData.tableName} ` + as.value
})
const liInfo_div2 = computed(() => {
  let arr = [];
  for (let l of detailDataSelectList.value) {
    if (l.name == l.javaName) {
      arr.push(`${ass.value}${l.name}`)
    } else {
      arr.push(`${ass.value}${l.name} '${l.javaName}'`)
    }
  }
  return 'select \n' + arr.join(' , ') + ` \n from ${props.detailData.tableName} ` + as.value
})
const liInfo_div3 = computed(() => {
  let arr = [];
  for (let l of detailDataSelectList.value) {
    let com = l.comment || l.name;
    arr.push(`${ass.value}${l.name} '${com}'`)
  }
  return 'select \n' + arr.join(' , ') + ` \n from ${props.detailData.tableName} ` + as.value
})

// 文档上单选按钮
const liInfo_doc_class = ref('1')

const liInfo_doc_1 = computed(() => {
  return doc1('<br/>')
})
const liInfo_doc_1_copy = computed(() => {
  return doc1('\n')
})

function doc1(str) {
  let arr = [];
  for (let item of detailDataSelectList.value) {
    arr.push(`${item.javaName} : ${item.comment}`)
  }
  return arr.join(str)
}

const liInfo_doc_2 = computed(() => {
  return doc2('<br/>')
})
const liInfo_doc_2_copy = computed(() => {
  return doc2('\n')
})

function doc2(str) {
  let arr = [];
  for (let item of detailDataSelectList.value) {
    arr.push(`private ${item.javaType} ${item.javaName}; // ${item.comment}`)
  }
  return arr.join(str)
}

const liInfo_doc_3 = computed(() => {
  return doc3('<br/>')
})
const liInfo_doc_3_copy = computed(() => {
  return doc3('\n')
})

function doc3(str) {
  let arr = [];
  for (let item of detailDataSelectList.value) {
    arr.push(`/** ${item.comment} */`)
    arr.push(`private ${item.javaType} ${item.javaName};`)
  }
  return arr.join(str)
}

const liInfo_doc_4 = computed(() => {
  return doc4('<br/>')
})
const liInfo_doc_4_copy = computed(() => {
  return doc4('\n')
})

function doc4(str) {
  let arr = [];
  arr.push('字段 | 类型 | 描述')
  arr.push(':----|:----|:----')
  for (let item of detailDataSelectList.value) {
    arr.push(`${item.javaName} | ${item.javaType} | ${item.comment}`)
  }
  return arr.join(str)
}

//表数据预览
const tableDbData = reactive({
  orderByName:undefined,
  orderByAsc:true,
  limit:10
})
const queryDbTable = function (){
  console.log(tableDbData)
}


</script>
