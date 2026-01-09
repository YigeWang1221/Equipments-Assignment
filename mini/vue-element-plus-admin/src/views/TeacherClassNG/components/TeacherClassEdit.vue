<script lang="js">
import { onMounted, ref } from 'vue'
import axios from 'axios'
import async from 'async'
import { useUserStore } from '@/store/modules/user'
import { ElMessage } from 'element-plus'

export default {
  data() {
    return {
      form: ref({
        name: '',
        region: '',
        dateA: '',
        dateB: '',
        TeachingClass: false,
        classType: false,
        resource: '',
        desc: ''
      }),
      PageClassId: null,
      data1: '',
      data2: ''
    }
  },
  mounted() {},
  created() {
    const userStore = useUserStore()
    this.PageClassId = userStore.getClassID
    console.log(this.PageClassId)
    if (this.PageClassId !== null) {
      this.GetFormData()
    }
  },
  methods: {
    async GetFormData() {
      console.log('班级编辑组件')
      console.log(this.PageClassId)
      let url = 'http://47.120.78.42:8090/zjgsEmb/TeacherClass/getClassById'
      const res = await axios.get(url, {
        params: {
          classId: this.PageClassId
        }
      })
      const classData = res.data.data
      this.form.name = classData.className
      this.form.dateA = classData.modifiedTime
      this.form.dateB = classData.endTime
      this.form.year = classData.year
      console.log(res.data)
      if (classData.classType === 2) {
        this.form.classType = true
      }
    },
    async updataClass() {
      const formData = new FormData()
      formData.append('classId', this.PageClassId)
      formData.append('className', this.form.name)
      if (!isNaN(this.form.year)) {
        formData.append('year', this.form.year)
      } else {
        ElMessage.warning('年份必须是数字类型的')
        return
      }
      formData.append('modifiedTime', this.data1)
      formData.append('endTime', this.data2)
      if (this.form.classType === true) {
        formData.append('classType', 2)
      } else {
        formData.append('classType', 1)
      }
      let url = 'http://47.120.78.42:8090/zjgsEmb/TeacherClass/upDateClassInfo'
      const res = await axios.post(url, formData)
      console.log(res.data)
    },
    async initClass() {
      const formData = new FormData()
      formData.append('classId', this.PageClassId)
      formData.append('className', this.form.name)
      if (!isNaN(this.form.year)) {
        formData.append('year', this.form.year)
      } else {
        ElMessage.warning('年份必须是数字类型的')
        return
      }
      formData.append('modifiedTime', this.data1)
      formData.append('endTime', this.data2)
      if (this.form.classType === true) {
        formData.append('classType', 2)
      } else {
        formData.append('classType', 1)
      }
      let url = 'http://47.120.78.42:8090/zjgsEmb/TeacherClass/initClassInfo'
      const res = await axios.post(url, formData)
      this.$router.push({
        path: '/dashboard/IOTclasses'
      })
      console.log(res.data)
    },
    selectTimeB(val) {
      this.data2 = val
      this.form.dateB = val
      console.log(this.form.dateB)
    },
    selectTimeA(val) {
      this.data1 = val
      this.form.dateA = val
      console.log(this.form.dateA)
    },
    async deleteClass() {
      let url = 'http://47.120.78.42:8090/zjgsEmb/TeacherClass/DeleteClassInfo'
      const res = await axios.get(url, {
        params: {
          classId: this.PageClassId
        }
      })
      this.$router.push({
        path: '/dashboard/IOTclasses'
      })
    }
  }
}
</script>

<template>
  <div style="margin-top: 10px"> 班级编辑管理界面 </div>
  <span>{{ father }}</span>
  <el-form :model="form" label-width="auto" style="max-width: 600px" class="form">
    <el-form-item label="班级名称">
      <el-input v-model="form.name" />
    </el-form-item>
    <el-form-item label="学年">
      <el-input v-model="form.year" placeholder="2024" />
    </el-form-item>
    <!--    default-value 默认日期是上一次选择的日期-->
    <el-form-item label="班级起止时间">
      <el-col :span="11">
        <el-date-picker
          v-model="form.dateA"
          type="date"
          @change="selectTimeA"
          placeholder="开始日期"
          style="width: 100%"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
        />
      </el-col>
      <el-col :span="2" class="text-center">
        <span class="text-gray-500">-</span>
      </el-col>
      <el-col :span="11">
        <el-date-picker
          v-model="form.dateB"
          type="date"
          @change="selectTimeB"
          placeholder="结束日期"
          style="width: 100%"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
        />
      </el-col>
    </el-form-item>
    <el-form-item label="IOT or ARM">
      <el-switch v-model="form.classType" />
      <el-tooltip
        class="item"
        effect="dark"
        :content="form.classType ? '是-> 物联网班级' : '否-> 嵌入式班级'"
        placement="top"
      />
    </el-form-item>
    <el-form-item>
      <el-button v-if="PageClassId !== ''" type="primary" @click="updataClass">更新上传</el-button>
      <el-button v-if="PageClassId !== ''" @click="deleteClass">删除班级</el-button>
      <el-button v-if="PageClassId === ''" @click="initClass">作为新班级创建</el-button>
    </el-form-item>
  </el-form>
</template>

<style>
.form {
  margin-top: 20px;
}
</style>

<style scoped lang="less"></style>
