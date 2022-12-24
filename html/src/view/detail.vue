<template>
  <div>
    <a-tabs v-model:activeKey="detailActiveKey">
      <a-tab-pane key="field" tab="字段">
        <!--          <h3>{{ detailDataSelectList.length }}</h3>-->
        <a-table
            :data-source="props.detailData.columns"
            :columns="detailDataColumns"
            row-key="id"
            :pagination="false" size="small"
            :row-selection="detailDataRowSelect"
        >
          <template #bodyCell="{ column, text, record }">
            <template v-if="column.dataIndex === 'javaName'">
              <span v-if="record.javaName!=record.name">{{ record.javaName }}</span>
              <span v-else></span>
            </template>
          </template>
        </a-table>
      </a-tab-pane>
      <a-tab-pane key="query" tab="查询sql">
        <a-space>
          <a-input v-model:value="as" style="width: 100px;" placeholder="表的别名"/>
<!--          代码样式-->
<!--          <a-switch v-model:checked="detailCodeShowSql" checked-children="开" un-checked-children="关"/>-->
        </a-space>
        <!-- div1 -->
        <a-typography-paragraph :code="detailCodeShowSql" copyable class="liInfo_div">
          <highlightjs language="sql" :code="liInfo_div1" />
        </a-typography-paragraph>
        <!-- div2 -->
        <a-typography-paragraph :code="detailCodeShowSql" copyable class="liInfo_div">
          <highlightjs language="sql" :code="liInfo_div2" />
        </a-typography-paragraph>
        <!-- div3 -->
        <a-typography-paragraph :code="detailCodeShowSql" copyable class="liInfo_div">
          <highlightjs language="sql" :code="liInfo_div3" />
        </a-typography-paragraph>
      </a-tab-pane>
      <a-tab-pane key="doc" tab="文档">
        <a-space>
          <a-radio-group v-model:value="liInfo_doc_class" button-style="solid">
            <a-radio-button value="1">普通</a-radio-button>
            <a-radio-button value="2">java1</a-radio-button>
            <a-radio-button value="3">java2</a-radio-button>
            <a-radio-button value="4">markdown</a-radio-button>
          </a-radio-group>
          代码样式
          <a-switch v-model:checked="detailCodeShowDoc" checked-children="开" un-checked-children="关"/>
        </a-space>
        <a-typography-paragraph :code="detailCodeShowDoc" copyable class="liInfo_div">
            <span v-if="liInfo_doc_class==4">
                <span>字段  |  类型  | 描述</span> <br/>
                <span>:----|:----|:----</span><br/>
            </span>
          <span v-for="item in detailDataSelectList">
              <span v-if="liInfo_doc_class==1">
                {{ item.javaName }} : {{ item.comment }}
              </span>
              <span v-if="liInfo_doc_class==2">
                private {{ item.javaType }} {{ item.javaName }}; // {{item.comment}}
              </span>
              <span v-if="liInfo_doc_class==3">
                /** {{item.comment}} */ <br/>
                private {{ item.javaType }} {{ item.javaName }};
              </span>
              <span v-if="liInfo_doc_class==4">
                  <span>{{ item.javaName }} | {{ item.javaType }} | {{item.comment}}</span>
              </span>

              <br/>
            </span>
        </a-typography-paragraph>
      </a-tab-pane>
      <a-tab-pane key="ddl" tab="DDL">
<!--        <a-space>-->
<!--          代码样式-->
<!--          <a-switch v-model:checked="detailCodeShowDDL" checked-children="开" un-checked-children="关"/>-->
<!--        </a-space>-->
        <a-typography-paragraph :code="detailCodeShowDDL" copyable class="liInfo_div">
          <highlightjs language="sql" :code="detailData.ddl" />
        </a-typography-paragraph>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script setup>
import {ref, computed} from "vue";

const props = defineProps(['detailData'])


const detailActiveKey = ref('field')
const detailDataColumns = ref([
  {title: '字段', dataIndex: 'name', width: '200px'},
  {title: '类型', dataIndex: 'type', width: '120px'},
  {title: '注释', dataIndex: 'comment', ellipsis: true, width: '200px'},
  {title: '驼峰', dataIndex: 'javaName'},
])
// 选中的表格数据
const detailDataSelectList = ref([])
const detailDataRowSelect = {
  onChange: (selectedRowKeys, selectedRows) => {
    // console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
    detailDataSelectList.value = selectedRows
  }
};

const detailCodeShowSql = ref(false)
const detailCodeShowDoc = ref(true)
const detailCodeShowDDL = ref(false)
const as = ref('')
const ass = computed(() => {
  return as.value.length > 0 ? as.value + '.' : ''
})

const liInfo_div1 = computed(() => {
  let arr = [];
  for (let l of detailDataSelectList.value) {
    arr.push(`${ass.value}${l.name}`)
  }
  return 'select \n'+arr.join(' , ')+` \n from ${props.detailData.tableName} ` + as.value
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
  return 'select \n'+arr.join(' , ')+` \n from ${props.detailData.tableName} ` + as.value
})
const liInfo_div3 = computed(() => {
  let arr = [];
  for (let l of detailDataSelectList.value) {
    let com = l.comment || l.name;
    arr.push(`${ass.value}${l.name} '${com}'`)
  }
  return 'select \n'+arr.join(' , ')+` \n from ${props.detailData.tableName} ` + as.value
})

// 文档上单选按钮
const liInfo_doc_class = ref('1')


</script>

<style>

</style>