<script setup>

</script>

<template>
  <main>
    <div style="margin-left: 100px;margin-top: 2px;margin-bottom: 0px;">
      <router-link to="/">index</router-link> &nbsp;&nbsp;
      <router-link to="/tpl">tpl</router-link>&nbsp;&nbsp;
    </div>
    <router-view v-slot="{ Component }">
      <transition>
        <div :key="$route.path" class="app-container">
          <keep-alive v-if="$route.meta.keepAlive">
            <component :is="Component"/>
          </keep-alive>
          <component :is="Component" v-else/>
        </div>
      </transition>
    </router-view>
  </main>
</template>

<style>

</style>
