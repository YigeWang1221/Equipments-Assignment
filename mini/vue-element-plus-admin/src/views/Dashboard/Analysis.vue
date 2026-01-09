<script lang="js">
//管理界面的母界面 basic
//默认是在班级学生管理界面上
import '@/assets/scss/index.scss'
//导入component
import TeacherClassEdit from '@/views/TeacherClassNG/components/TeacherClassEdit.vue'
import TeacherClassGro from '@/views/TeacherClassNG/components/TeacherClassGro.vue'
import TeacherClassStu from '@/views/TeacherClassNG/components/TeacherClassStu.vue'
import TeacherWorkList from '@/views/WorkTeach/components/TeacherClassWorkList.vue'
import { useUserStore } from '@/store/modules/user'
export default {
  components: {
    TeacherClassEdit,
    TeacherClassGro,
    TeacherClassStu,
    TeacherWorkList
  },
  data() {
    return {
      selectedComponent: 'TeacherClassStu', // 存储当前选中的组件名称,默认是在班级学生管理界面的
      classId: '',
      className: '',
      fun: ''
    }
  },
  created() {
    // 从 $route.query 中获取传递过来的 id 和 className
    this.classId = this.$route.query.id
    this.className = this.$route.query.className
    this.fun = this.$route.query.fun
    const userStore = useUserStore()
    userStore.setClassId(this.classId)
    console.log('教师管理界面' + this.fun)
    if (this.fun === 'basic') {
      this.selectedComponent = 'TeacherClassEdit'
    }
    //展示组件
  },
  methods: {
    showComponent(componentName, id) {
      const userStore = useUserStore()
      userStore.setClassId(this.classId)
      this.selectedComponent = componentName // 点击后切换选中的组件
      this.id = id // 将 id 传递给选中的组件
    }
  }
}
</script>

<template>
  <div id="selectAction">
    <el-row :gutter="20">
      <el-col :span="6"
        ><el-card
          shadow="hover"
          class="ep-bg-purple"
          @click="showComponent('TeacherClassEdit', id)"
        >
          班级信息管理
        </el-card>
      </el-col>
      <el-col :span="6"
        ><el-card
          shadow="hover"
          class="ep-bg-purple-light"
          @click="showComponent('TeacherClassStu', id)"
          >班级成员管理</el-card
        >
      </el-col>
      <el-col :span="6"
        ><el-card
          shadow="hover"
          class="ep-bg-purple"
          @click="showComponent('TeacherClassGroup', id)"
          >班级小组管理</el-card
        >
      </el-col>
      <el-col :span="6"
        ><el-card
          shadow="hover"
          class="ep-bg-purple-light"
          @click="showComponent('TeacherClassHomeWork', id)"
          >班级作业管理</el-card
        >
      </el-col>
    </el-row>
  </div>
  <el-divider />
  <div>
    <!--    这里打算在点击后显示界面 /TeacherClassEdit/TeacherClassStu/TeacherClassGroup/TeacherClassHomework-->
    <TeacherClassEdit v-if="selectedComponent === 'TeacherClassEdit'" :id="id" />
    <TeacherClassStu v-if="selectedComponent === 'TeacherClassStu'" :id="id" />
    <TeacherClassGro v-if="selectedComponent === 'TeacherClassGroup'" :id="id" />
    <TeacherWorkList
      v-if="selectedComponent === 'TeacherClassHomeWork'"
      :PostclassName="className"
    />
  </div>
</template>

<style scoped lang="less">
.el-row {
  margin-bottom: 20px;
}
.el-row:last-child {
  margin-bottom: 0;
}
.el-col {
  border-radius: 4px;
}

.grid-content {
  border-radius: 4px;
  min-height: 36px;
}
</style>
