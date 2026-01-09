<script lang="js">
import { useUserStore } from '@/store/modules/user'
import { ElMessage } from 'element-plus'
import axios from 'axios'

export default {
  data() {
    return {
      fun: '',
      classId: '',
      StuId: '',
      name: '',
      className: '',
      college: ''
    }
  },
  created() {
    this.fun = this.$route.query.fun
    const usrStore = new useUserStore()
    this.StuId = this.$route.query.stuId
    this.classId = usrStore.getClassID
    if (this.fun === 'edit') {
      this.getInfo()
    }
  },
  methods: {
    async getInfo() {
      let url = 'http://47.120.78.42:8090/zjgsEmb/TeacherManageStu/getStuInClass'
      const res = await axios.get(url, {
        params: {
          classId: this.classId,
          uid: this.StuId
        }
      })
      const data = res.data.data
      this.className = data.className
      this.name = data.stuName
      this.className = data.className
    },
    async updateStu() {
      if (this.validateForm()) {
        ElMessage.error('填写所有表单')
        return
      }
      if (this.isLongId(this.StuId)) {
        ElMessage.error('学号格式错误')
        return
      }
      const formData = new FormData()
      formData.append('ClassName', this.className)
      formData.append('classId', this.classId)
      formData.append('college', this.college)
      formData.append('uid', this.StuId)
      formData.append('stuName', this.name)
      let url = 'http://47.120.78.42:8090/zjgsEmb/TeacherManageStu/updateStuInClass'
      const res = await axios.post(url, formData)
      console.log(res.data.data)
      ElMessage.warning(res.data.data)
      ElMessage.success('更新成功')
    },
    async AddStu() {
      if (this.validateForm()) {
        ElMessage.error('填写所有表单')
        return
      }
      if (this.isLongId(this.StuId)) {
        ElMessage.error('学号格式错误')
        return
      }
      const formData = new FormData()
      formData.append('ClassName', this.className)
      formData.append('classId', this.classId)
      formData.append('college', this.college)
      formData.append('uid', this.StuId)
      formData.append('stuName', this.name)
      let url = 'http://47.120.78.42:8090/zjgsEmb/TeacherManageStu/InsertStu'
      const res = await axios.post(url, formData)
      console.log(res.data.data)
      ElMessage.success('成功')
    },
    validateForm() {
      if (!this.name || !this.StuId || !this.className || !this.college) {
        // 如果有任何一个字段为空，返回 false
        return true
      }
      // 所有字段都不为空，返回 true
      return false
    },
    isLongId(id) {
      // 判断是否是数字
      if (!isNaN(id)) {
        // 判断是否是10位长度
        if (id.toString().length === 10) {
          // 判断是否在long类型的范围内
          if (id >= -9223372036 && id <= 9223372036) {
            return false // 是10位的 long 类型
          }
        }
      }
      return true // 不是10位的 long 类型
    },
    async deleteStu() {
      let url = 'http://47.120.78.42:8090/zjgsEmb/TeacherManageStu/deleteClassStudentByStudentId'
      const res = await axios
        .get(url, {
          params: {
            StuId: this.StuId,
            classId: this.classId
          }
        })
        .then(() => {
          ElMessage.warning('删除成功')
        })
    }
  }
}
</script>

<template>
  <el-form label-width="auto" style="max-width: 600px; margin-top: 10px">
    <el-form-item label="学生姓名">
      <el-input v-model="name" />
    </el-form-item>
    <el-form-item label="学生学号">
      <el-input v-model="StuId" />
    </el-form-item>
    <el-form-item label="行政班级">
      <el-input v-model="className" />
    </el-form-item>
    <el-form-item label="学院">
      <el-input v-model="college" />
    </el-form-item>
    <el-form-item>
      <el-button v-if="fun === 'init'" type="primary" @click="AddStu">添加学生</el-button>
      <el-button v-if="fun === 'edit'" type="primary" @click="updateStu">更新学生信息</el-button>
      <el-button v-if="fun === 'edit'" type="primary" @click="deleteStu">删除学生</el-button>
    </el-form-item>
  </el-form>
</template>

<style scoped lang="less"></style>
