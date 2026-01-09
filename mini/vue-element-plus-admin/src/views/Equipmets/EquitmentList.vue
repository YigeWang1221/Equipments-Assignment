<script setup lang="js">
// 这个界面是用来展示所有的器材的，最后一张卡片式（+） 只有教师可以添加新的器材
// 点击器材的时候需要根据permission来判断是进入借用详情还是 修改详情（就是下一个界面的可修改和可见改成false）
// 需要展示的是 图片，总数和剩余数量
// 回去看一下逻辑是个人借用还是小组借用
import { onMounted, ref } from 'vue'

//这个怎么和接口对接啊 有点不太行的样子
// const form = ref({
//
// })
const equipments = ref([])
const role = ref('')
const downloadZip = (equipment) => {}
const borrowEquipment = (equipment) => {}
const editEquipment = (equipment) => {}
const addEquipment = () => {}
// onMounted(async () => {})
</script>

<template>
  <div align="middle">
    <el-text size="large" type="warning">可用器材展示</el-text>
  </div>
  <el-divider class="color-block" />
  <div class="flex flex-wrap gap-4">
    <!--    这里展示所有的器材 使用v-for-->
    <!--    这里添加一个v-if 保证只有教师能够新增 todo-->
    <div v-for="(equipment, index) in equipments" :key="index">
      <el-card>
        <template #header>{{ equipment.name }}}</template>
        <!--        如果为空就显示本地的-->
        <el-image
          :src="equipment.photoUrl ? equipment.photoUrl : 'src/assets/imgs/Nophoto.jpg'"
          style="max-width: 100%; max-height: 280px"
        />
        <p>{{ equipment.detail }}}</p>
        <template #footer>
          <el-text type="danger">现有数量：{{ equipment.number }}}</el-text>
          <el-button link type="success" @click="downLoadZip(equipment)">下载配套资源</el-button>
          <el-button link type="primary" @click="borrowEquipment(equipment)">借用器材</el-button>
          <el-button v-if="role === 'teacher'" type="warning" @click="editEquipment(equipment)"
            >编辑器材</el-button
          >
        </template>
      </el-card>
    </div>
    <el-card style="max-width: 200px" shadow="hover">
      <template #header>新增器材</template>
      <el-image src="src/assets/imgs/NoPhoto.jpg" style="max-width: 100%; max-height: 280px" />
      <template #footer>
        <el-button link type="warning" @click="addEquipment">点击增加</el-button>
      </template>
    </el-card>
  </div>
</template>

<style scoped lang="less"></style>
