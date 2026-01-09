<script lang="js">
// 这个界面用来展示借出的所有器材,器材借出的名义是个人，数据库方便一点
import axios from 'axios'
import { ElMessage } from 'element-plus'

export default {
  data() {
    return {
      BorrowApplying: [],
      BorrowReturn: []
    }
  },
  created() {
    this.getBorrowApplying()
    this.getBorrowReturn()
  },
  methods: {
    async getBorrowApplying() {
      let url = 'http://47.120.78.42:8090/zjgsEmb/EquipMent/getAllApplyBorrow'
      const res = await axios.get(url)
      this.BorrowApplying = res.data.data
    },
    async getBorrowReturn() {
      let url = 'http://47.120.78.42:8090/zjgsEmb/EquipMent/getAllBorrowing'
      const res = await axios.get(url)
      this.BorrowReturn = res.data.data
    },
    async AgreeApplying(row) {
      if (row.mleftNumber >= row.broNum) {
        let url = 'http://47.120.78.42:8090/zjgsEmb/EquipMent/OperationToBorrow'
        const formData = new FormData()
        formData.append('BorrowId', row.id)
        formData.append('isReturned', 1)
        await axios.post(url, formData)
        this.getBorrowApplying()
        this.getBorrowReturn()
      } else {
        ElMessage.error('借用数量超出最大可用数量，已经自动拒绝借用请求')
        this.DisAgreeApplying(row)
        this.getBorrowApplying()
        this.getBorrowReturn()
      }
    },
    async DisAgreeApplying(row) {
      let url = 'http://47.120.78.42:8090/zjgsEmb/EquipMent/OperationToBorrow'
      const formData = new FormData()
      formData.append('BorrowId', row.id)
      formData.append('isReturned', 3)
      await axios.post(url, formData)
      ElMessage.success('确认不同意借出')
      this.getBorrowApplying()
      this.getBorrowReturn()
    },
    async ReturnEquip(row) {
      let url = 'http://47.120.78.42:8090/zjgsEmb/EquipMent/OperationToBorrow'
      const formData = new FormData()
      formData.append('BorrowId', row.id)
      formData.append('isReturned', 2)
      await axios.post(url, formData)
      ElMessage.success('系统中已经成功归还，请及时将器材入库')
      this.getBorrowApplying()
      this.getBorrowReturn()
    }
  }
}
</script>

<template>
  <div>
    <div align="middle">
      <el-text align="middle" size="large" type="success">学生借用申请</el-text>
    </div>
    <!--    这个div是尚未归还的表格-->
    <el-table
      stripe
      border
      :data="BorrowApplying"
      align="middle"
      style="margin-top: 20px"
      height="250"
    >
      <el-table-column sortable prop="browTime" label="请求日期" width="120" />
      <el-table-column prop="stuName" label="借用人" width="120" />
      <el-table-column prop="stuId" label="学号" width="120" />
      <el-table-column prop="mname" label="借用物品" width="240" />
      <el-table-column prop="mleftNumber" label="现有数量" width="120" />
      <el-table-column prop="broNum" label="借用数量" width="120" />
      <el-table-column label="操作" width="240">
        <template #default="{ row }">
          <!--          每次归还都要刷新界面，不然会出问题的 todo-->
          <el-button link type="primary" @click="AgreeApplying(row)">同意借出</el-button>
          <el-button link type="warning" @click="DisAgreeApplying(row)">拒绝借出</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
  <el-divider style="margin-top: 15px" />
  <div>
    <div align="middle" style="margin-top: 30px">
      <el-text align="middle" size="large" type="danger">学生归还</el-text>
    </div>
    <!--    这个div是尚未归还的表格-->
    <el-table
      stripe
      border
      :data="BorrowReturn"
      align="middle"
      style="margin-top: 20px"
      height="250"
    >
      <el-table-column sortable prop="browTime" label="请求日期" width="120" />
      <el-table-column prop="stuName" label="借用人" width="180" />
      <el-table-column prop="stuId" label="学号" width="180" />
      <el-table-column prop="mname" label="借用物品" width="240" />
      <el-table-column prop="broNum" label="借用数量" width="180" />
      <el-table-column label="操作" width="240">
        <template #default="{ row }">
          <!--          每次归还都要刷新界面，不然会出问题的 todo-->
          <el-button link type="warning" size="small" @click="ReturnEquip(row)"
            >线下已经确认回收</el-button
          >
        </template>
      </el-table-column>
    </el-table>
  </div>
  <el-divider />
</template>

<style scoped lang="less"></style>
