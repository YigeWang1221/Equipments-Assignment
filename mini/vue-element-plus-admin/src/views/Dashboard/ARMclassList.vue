<script lang="js">
// 教师查看班级列表的界面 ARM页面
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
        this.learningClasses = await getClasses({ type: 'ARM' })
      } catch (error) {
        console.error('Error fetching learning classes:', error)
      }
    },
    JumpToClass(classId, className) {
      console.log(classId)
      console.log(className)
      // 使用 $router.push() 导航到目标页面，并传递参数
      this.$router.push({
        path: '/dashboard/analysis', // 目标页面的路由路径
        query: {
          id: classId,
          className: className,
          fun: 'base'
        }
      })
    },
    addClassPage() {
      this.$router.push({
        path: '/dashboard/analysis',
        query: {
          id: '',
          className: '',
          fun: 'basic'
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
      @click="JumpToClass(classItem.id, classItem.className)"
    >
      {{ classItem.className }}
    </el-card>
    <el-card style="width: 460px" shadow="hover" @click="addClassPage">点击添加班级</el-card>
  </div>
</template>

<style scoped lang="less"></style>
