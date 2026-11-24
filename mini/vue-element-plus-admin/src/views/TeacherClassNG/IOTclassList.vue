<script lang="js">
// 教师查看班级列表的界面 IOT页面
import { getClasses } from '@/api/equip/classes'

export default {
  data() {
    return {
      learningClasses: [] // 存储获取到的课程列表数据
    }
  },
  created() {
    this.getLearningClasses() // 在页面创建时请求课程列表数据
  },
  methods: {
    async getLearningClasses() {
      try {
        this.learningClasses = await getClasses({ type: 'IOT' })
      } catch (error) {
        console.error('Error fetching learning classes:', error)
      }
    },
    handleCardClick(id, className) {
      // 点击 el-card 时触发的事件处理函数
      //todo
      // 使用路由导航将 id 和 className 传递到 /classWorkList 页面
      this.$router.push({
        path: '/TeacherManage',
        query: {
          id: id,
          className: className
        }
      })
    }
  }
}
</script>

<template>
  <div class="flex flex-wrap gap-4">
    <el-card
      v-for="classItem in learningClasses"
      :key="classItem.id"
      class="card-item"
      style="width: 460px"
      shadow="hover"
      @click="handleCardClick(classItem.id, classItem.className)"
    >
      {{ classItem.className }}
    </el-card>
    <el-card style="width: 460px" shadow="hover">Always</el-card>
  </div>
</template>

<style scoped lang="less"></style>
