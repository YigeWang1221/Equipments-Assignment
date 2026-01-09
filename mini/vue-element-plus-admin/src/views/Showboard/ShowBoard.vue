<script setup>
import ShowItems from '@/views/Showboard/components/ShowItems.vue'
import { useRoute } from 'vue-router'
const route = useRoute()
const item = route.params.item
</script>

<template>
  <ShowItems :item="item" />
</template>

<style scoped lang="less"></style>
