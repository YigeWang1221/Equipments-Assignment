<script lang="js">
import { ref } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { ElMessage } from 'element-plus'
import {
  getClass,
  createClass as createClassApi,
  updateClass as updateClassApi,
  deleteClass as deleteClassApi
} from '@/api/equip/classes'

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
    this.PageClassId = userStore.getClassID || ''
    if (this.PageClassId !== '') {
      this.GetFormData()
    }
  },
  methods: {
    async GetFormData() {
      const classData = await getClass(this.PageClassId)
      this.form.name = classData.className
      this.form.dateA = classData.startTime || ''
      this.form.dateB = classData.endTime || ''
      this.data1 = classData.startTime || ''
      this.data2 = classData.endTime || ''
      this.form.year = classData.year
      if (classData.classType === 2) {
        this.form.classType = true
      }
    },
    buildClassPayload() {
      if (isNaN(this.form.year)) {
        ElMessage.warning('年份必须是数字类型的')
        return null
      }
      return {
        className: this.form.name,
        year: Number(this.form.year),
        startTime: this.data1 || this.form.dateA,
        endTime: this.data2 || this.form.dateB,
        classType: this.form.classType === true ? 2 : 1
      }
    },
    async updataClass() {
      const payload = this.buildClassPayload()
      if (!payload) return
      await updateClassApi(this.PageClassId, payload)
      ElMessage.success('更新成功')
    },
    async initClass() {
      const payload = this.buildClassPayload()
      if (!payload) return
      await createClassApi(payload)
      ElMessage.success('新建成功')
      this.$router.push({
        path: '/dashboard/IOTclasses'
      })
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
      await deleteClassApi(this.PageClassId)
      ElMessage.success('删除成功')
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
