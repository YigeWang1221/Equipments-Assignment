<script lang="js">
// 这个界面用来展示借出的所有器材,器材借出的名义是个人，数据库方便一点
export default {
  data() {
    return {
      borrowList1: [],
      borrowList2: []
    }
  }
}
</script>

<template>
  <div>
    <div align="middle">
      <el-text align="middle" size="large" type="success">学生归还表</el-text>
    </div>
    <!--    这个div是尚未归还的表格-->
    <el-table stripe border data="" align="middle" style="margin-top: 20px">
      <el-table-column prop="date" label="借出日期" width="120" />
      <el-table-column prop="Name" label="借用人" width="120" />
      <el-table-column prop="StuId" label="学号" width="120" />
      <el-table-column prop="equipment.Name" label="借用物品" width="120" />
      <el-table-column prop="number" label="借用数量" width="120" />
      <el-table-column label="操作" width="120">
        <template #default(row)>
          <!--          每次归还都要刷新界面，不然会出问题的 todo-->
          <el-button link type="primary" @click="returnEquipment(row)">确认学生归还</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
  <el-divider style="margin-top: 15px" />
  <div>
    <div align="middle" style="margin-top: 30px">
      <el-text align="middle" size="large" type="danger">学生借用表</el-text>
    </div>
    <!--    这个div是尚未归还的表格-->
    <el-table stripe border data="" align="middle" style="margin-top: 20px">
      <el-table-column prop="date" label="请求日期" width="120" />
      <el-table-column prop="Name" label="借用人" width="120" />
      <el-table-column prop="StuId" label="学号" width="120" />
      <el-table-column prop="equipment.Name" label="借用物品" width="120" />
      <el-table-column prop="number" label="借用数量" width="120" />
      <el-table-column label="操作" width="120">
        <template #default(row)>
          <!--          每次归还都要刷新界面，不然会出问题的 todo-->
          <el-button link type="primary" size="small" @click="BorrowEquipment(row)"
            >教师确认借出</el-button
          >
          <el-button link type="warning" size="small" @click="DeleteRequest(row)"
            >拒绝借出</el-button
          >
        </template>
      </el-table-column>
    </el-table>
  </div>
  <el-divider />
</template>

<style scoped lang="less"></style>
