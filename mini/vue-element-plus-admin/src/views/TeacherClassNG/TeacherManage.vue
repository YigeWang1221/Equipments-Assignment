<script lang="js">
//管理界面的母界面 basic
//默认是在班级学生管理界面上
import '@/assets/scss/index.scss'
//导入component
import TeacherClassEdit from '@/views/TeacherClassNG/components/TeacherClassEdit.vue'
import TeacherClassGro from '@/views/TeacherClassNG/components/TeacherClassGro.vue'
import TeacherClassStu from '@/views/TeacherClassNG/components/TeacherClassStu.vue'
import TeacherWorkList from '@/views/WorkTeach/components/TeacherClassWorkList.vue'
export default {
  data() {
    return {
      selectedComponent: 'TeacherClassStu', // 存储当前选中的组件名称,默认是在班级学生管理界面的
      id: null,
      className: null,
      fun: ''
    }
  },
  components: {
    TeacherClassEdit,
    TeacherClassGro,
    TeacherClassStu,
    TeacherWorkList
  },
  mounted() {
    console.log('页面完全加载')
    console.log(this.$router.query.id)
    console.log(this.$router.query.fun)
  },
  created() {
    // 从 $route.query 中获取传递过来的 id 和 className
    this.id = this.$router.query.id
    // this.id = JSON.parse(JSON.stringify(this.id))
    this.className = this.$router.query.className
    this.fun = this.$router.query.fun
    if (this.fun === 'editclass') {
      console.log(this.fun)
      this.showComponent('TeacherClassEdit')
    }
    console.log('基础界面输出id' + this.id)
  },
  methods: {
    showComponent(componentName) {
      console.log('点击页面跳转之后' + classId)
      console.log(this.$router.query.id)
      console.log(this.$router.query.fun)
      this.selectedComponent = componentName // 点击后切换选中的组件
      this.id = this.$router.query.id
    }
  }
}
</script>

<template>
  <div id="selectAction">
    <el-row :gutter="20">
      <el-col :span="6"
        ><el-card shadow="hover" class="ep-bg-purple" @click="showComponent('TeacherClassEdit')">
          班级信息管理
        </el-card>
      </el-col>
      <el-col :span="6"
        ><el-card
          shadow="hover"
          class="ep-bg-purple-light"
          @click="showComponent('TeacherClassStu')"
          >班级成员管理</el-card
        >
      </el-col>
      <el-col :span="6"
        ><el-card shadow="hover" class="ep-bg-purple" @click="showComponent('TeacherClassGroup')"
          >班级小组管理</el-card
        >
      </el-col>
      <el-col :span="6"
        ><el-card
          shadow="hover"
          class="ep-bg-purple-light"
          @click="showComponent('TeacherClassHomeWork')"
          >班级作业管理</el-card
        >
      </el-col>
    </el-row>
  </div>
  <el-divider />
  <div>
    <!--    这里打算在点击后显示界面 /TeacherClassEdit/TeacherClassStu/TeacherClassGroup/TeacherClassHomework-->
    <TeacherClassEdit v-if="selectedComponent === 'TeacherClassEdit'" :classid="id" />
    <TeacherClassStu v-if="selectedComponent === 'TeacherClassStu'" :classid="id" />
    <TeacherClassGro v-if="selectedComponent === 'TeacherClassGroup'" :classid="id" />
    <TeacherWorkList v-if="selectedComponent === 'TeacherClassHomeWork'" :classid="id" />
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
