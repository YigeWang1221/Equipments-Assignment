<script lang="js">
// 这个界面用来展示借出的所有器材,器材借出的名义是个人，数据库方便一点
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'

export default {
  data() {
    return {
      BorrowApplying: [],
      BorrowReturn: [],
      stuId: ''
    }
  },
  created() {
    const usrStore = new useUserStore()
    this.stuId = usrStore.getStuId
    console.log(this.stuId)
    this.getBorrowApplying()
    this.getBorrowReturn()
  },
  methods: {
    async getBorrowApplying() {
      let url = 'http://47.120.78.42:8090/zjgsEmb/EquipMent/getStudentApplyBorrow'
      const res = await axios.get(url, {
        params: {
          stuId: this.stuId
        }
      })
      this.BorrowApplying = res.data.data
    },
    async getBorrowReturn() {
      let url = 'http://47.120.78.42:8090/zjgsEmb/EquipMent/getStudentBorrowing'
      const res = await axios.get(url, {
        params: {
          stuId: this.stuId
        }
      })
      this.BorrowReturn = res.data.data
    },
    async DisApplying(row) {
      let url = 'http://47.120.78.42:8090/zjgsEmb/EquipMent/StuDeleteApply'
      await axios.get(url, {
        params: {
          borrowId: row.id
        }
      })
      ElMessage.success('取消成功，你的申请成功撤销')
      this.getBorrowApplying()
      this.getBorrowReturn()
    }
  }
}
</script>

<template>
  <div>
    <div align="middle">
      <el-text align="middle" size="large" type="success">我的借用申请</el-text>
    </div>
    <!--    这个div是尚未归还的表格-->
    <el-table stripe border :data="BorrowApplying" align="middle" style="margin-top: 20px">
      <el-table-column sortable prop="browTime" label="请求日期" width="160" />
      <el-table-column prop="stuName" label="借用人" width="160" />
      <el-table-column prop="stuId" label="学号" width="160" />
      <el-table-column prop="mname" label="借用物品" width="260" />
      <el-table-column prop="mleftNumber" label="现有数量" width="180" />
      <el-table-column prop="broNum" label="借用数量" width="120" />
      <el-table-column label="操作" width="240">
        <template #default="{ row }">
          <!--          每次归还都要刷新界面，不然会出问题的 todo-->
          <el-button link type="warning" @click="DisApplying(row)">取消借用申请</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
  <el-divider style="margin-top: 15px" />
  <div>
    <div align="middle" style="margin-top: 30px">
      <el-text align="middle" size="large" type="danger">我的正在使用的器材</el-text>
    </div>
    <!--    这个div是尚未归还的表格-->
    <el-table stripe border :data="BorrowReturn" align="middle" style="margin-top: 20px">
      <el-table-column sortable prop="browTime" label="申请时间" width="180" />
      <el-table-column prop="stuName" label="借用人" width="180" />
      <el-table-column prop="stuId" label="学号" width="180" />
      <el-table-column prop="mname" label="借用物品" width="260" />
      <el-table-column prop="broNum" label="借用数量" width="180" />
    </el-table>
  </div>
  <el-divider />
</template>

<style scoped lang="less"></style>
