<template>
  <!--  https://blog.csdn.net/qq_32674347/article/details/125623013-->
  <span>
    开启暗黑模式
    <a-switch style="margin-left: 5px;"
              v-model:checked="isDark"
              @change="change"
    >
      <template #checkedChildren>
        <check-outlined/>
      </template>
      <template #unCheckedChildren>
        <close-outlined/>
      </template>
    </a-switch>
  </span>

</template>

<script setup>
import {ref, reactive, computed, onMounted} from "vue";
import {CheckOutlined, CloseOutlined} from '@ant-design/icons-vue';
import dark from '@/style/dark.less?inline'
import light from '@/style/light.less?inline'

const isDark = ref(true)

const change = () => {
  changeTheme(isDark.value ? dark : light)
  setDarkTheme(isDark.value)
}
// 页面初始加载
onMounted(() => {
  var darkTheme = getDarkTheme();
  if (darkTheme != undefined) {
    isDark.value = darkTheme
  }
  changeTheme(isDark.value ? dark : light)
});

// 切换css
const changeTheme = (theme) => {
  const head = document.head;
  document.getElementById("theme")?.remove();
  const styleDom = document.createElement("style");
  styleDom.id = "theme";
  styleDom.innerHTML = theme;
  head.appendChild(styleDom);
};

let themeKey = 'theme_key'
const setDarkTheme = (t) => {
  localStorage.setItem(themeKey, t)
}
const getDarkTheme = () => {
  let valueStr = localStorage.getItem(themeKey)
  return valueStr ? JSON.parse(valueStr) : null
}

</script>