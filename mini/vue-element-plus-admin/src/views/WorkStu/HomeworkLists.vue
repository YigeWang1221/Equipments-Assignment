<script>
import { getWorkLists } from '@/apiX/homeWork'
import { useUserStore } from '@/store/modules/user'
import axios from 'axios'
import { ElMessage } from 'element-plus'
export default {
  data() {
    return {
      stuId: '',
      groupId: '',
      className: '',
      ClassWorkList: [],
      FinalClassWOrkList: []
    }
  },
  created() {
    const usrStore = new useUserStore()
    this.stuId = usrStore.getStuId
    this.getGroupIdNow()
    this.getBasicInfo()
    this.getBasicInfo2()
  },
  methods: {
    async getBasicInfo() {
      let url0 = 'http://47.120.78.42:8090/Usr/getClassIdByStuId'
      let url1 = 'http://47.120.78.42:8090/zjgsEmb/TeacherClass/getClassById'
      let url2 = 'http://47.120.78.42:8090/zjgsEmb/TeacherWork/getTeacherWorkByClassIdNotFinal'

      const res0 = await axios.get(url0, {
        params: {
          StuId: this.stuId
        }
      })
      const classId = res0.data.data
      const res1 = await axios.get(url1, {
        params: {
          classId: classId
        }
      })
      console.log('网络请求2 ' + res1)
      this.className = res1.data.data.className

      const res2 = await axios.get(url2, {
        params: {
          classId: classId
        }
      })
      this.ClassWorkList = res2.data.data
      for (let i = 0; i < this.ClassWorkList.length; i++) {
        const item = this.ClassWorkList[i]
        switch (item.isFinal) {
          case 0:
            item.isFinal = '未开始'
            break
          case 1:
            item.isFinal = '进行中'
            break
          case 2:
            item.isFinal = '截止'
            break
          default:
            item.isFinal = '时间处理中'
            break
        }
      }
    },
    async getBasicInfo2() {
      let url0 = 'http://47.120.78.42:8090/Usr/getClassIdByStuId'
      let url1 = 'http://47.120.78.42:8090/zjgsEmb/TeacherClass/getClassById'
      let url2 =
        'http://47.120.78.42:8090/zjgsEmb/TeacherWork/getTeacherWorkByClassIdANDGroupIdIsFinal'

      const res0 = await axios.get(url0, {
        params: {
          StuId: this.stuId
        }
      })
      const classId = res0.data.data
      const res1 = await axios.get(url1, {
        params: {
          classId: classId
        }
      })
      console.log('网络请求2 ' + res1)
      this.className = res1.data.data.className

      const res2 = await axios.get(url2, {
        params: {
          classId: classId,
          groupId: this.groupId
        }
      })
      this.FinalClassWOrkList = res2.data.data
      for (let i = 0; i < this.FinalClassWOrkList.length; i++) {
        const item = this.FinalClassWOrkList[i]
        switch (item.score) {
          case null:
            item.score = '教师尚未批改'
            break
          case -1:
            item.score = '作业未写'
            break
          default:
            break
        }
      }
    },
    async getGroupIdNow() {
      let url = 'http://47.120.78.42:8090/Usr/getGroupIdBystuId'
      const res = await axios.get(url, {
        params: {
          StuId: this.stuId
        }
      })
      this.groupId = res.data.data
      if (this.groupId === 0) {
        ElMessage.error('你尚未加入班级小组，请联系傅均老师处理')
        setTimeout(() => {
          this.$router.push({
            path: '/function/showListBoard'
          })
        }, 2000)
      }
    },
    downLoadZip(row) {
      if (row.detailurl === null || row.detailurl === '') {
        ElMessage.warning('当前作业没有对应资源可供下载')
        return
      }
      const link = document.createElement('a')
      const zipUrl = row.detailurl.substring(1, row.detailurl.length - 1)
      link.href = zipUrl
      link.setAttribute('download', zipUrl)
      link.click()
    },
    FinishHomeWork(row) {
      this.$router.push({
        path: '/hooks/StuworkTable',
        query: {
          groupId: this.groupId,
          TeacherWorkId: row.id,
          workname: row.workname
        }
      })
    }
  }
}
</script>

<template>
  <div align="middle" style="margin-top: 30px">
    <el-text align="middle" size="large" type="primary">尚未截至的作业</el-text>
  </div>
  <div class="work-chart">
    <el-table
      :data="ClassWorkList"
      align="middle"
      style="width: 100%; margin-top: 10px"
      height="250"
    >
      <el-table-column fixed="left" label="班级名称" width="140">{{ className }}</el-table-column>
      <el-table-column prop="workname" label="作业名称" width="180" />
      <el-table-column prop="modifiedTime" sortable label="开始时间" width="160" />
      <el-table-column prop="endTime" label="截止时间" width="160" />
      <el-table-column prop="isFinal" label="状态" width="120" />
      <el-table-column label="下载作业附件" width="140">
        <template #default="{ row }">
          <el-button type="success" link size="default" @click="downLoadZip(row)"
            >点击下载</el-button
          >
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="操作" width="220">
        <template #default="{ row }">
          <el-button
            link
            :disabled="row.isFinal === '未开始'"
            type="primary"
            size="small"
            @click="FinishHomeWork(row)"
            >去写作业</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <el-divider />
    <div align="middle" style="margin-top: 30px">
      <el-text align="middle" size="large" type="warning">已经截至的作业</el-text>
    </div>
  </div>
  <el-table
    :data="FinalClassWOrkList"
    align="middle"
    style="width: 100%; margin-top: 10px"
    height="250"
  >
    <el-table-column fixed="left" label="班级名称" width="180">{{ className }}</el-table-column>
    <el-table-column prop="workName" label="作业名称" width="280" />
    <el-table-column prop="modifiedTime" label="开始时间" width="180" />
    <el-table-column prop="finishTime" label="截止时间" width="180" />
    <el-table-column prop="isFinal" label="状态" width="120">截止</el-table-column>
    <el-table-column prop="score" label="成绩" width="180" />
  </el-table>
</template>

<style scoped lang="less"></style>
