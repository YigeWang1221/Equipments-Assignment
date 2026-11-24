<script>
import { useUserStore } from '@/store/modules/user'
import { ElMessage } from 'element-plus'
import { getMyClass, getMyGroup, getStudentClass, getStudentGroup } from '@/api/equip/students'
import { getWorks } from '@/api/equip/works'
import { getMySubmission } from '@/api/equip/submissions'
export default {
  data() {
    return {
      stuId: '',
      groupId: '',
      classId: '',
      className: '',
      ClassWorkList: [],
      FinalClassWOrkList: []
    }
  },
  created() {
    const usrStore = useUserStore()
    this.stuId = usrStore.getStuId
    this.getGroupIdNow()
    this.getBasicInfo()
    this.getBasicInfo2()
  },
  methods: {
    normalizeWork(work) {
      const statusMap = { 0: '未开始', 1: '进行中', 2: '截止' }
      return {
        ...work,
        workname: work.workName,
        modifiedTime: work.startTime,
        finishTime: work.endTime,
        detailurl: work.detailUrl,
        isFinal: work.statusName || statusMap[work.status] || '时间处理中'
      }
    },
    async getClassInfo() {
      try {
        const classInfo = this.stuId ? await getStudentClass(this.stuId) : await getMyClass()
        this.classId = classInfo.id
        this.className = classInfo.className
        return classInfo
      } catch (error) {
        console.error('获取学生班级失败', error)
        return null
      }
    },
    async getBasicInfo() {
      const classInfo = await this.getClassInfo()
      if (!classInfo) return
      const works = await getWorks({ classId: classInfo.id })
      this.ClassWorkList = works
        .filter((work) => work.status !== 2)
        .map((work) => this.normalizeWork(work))
    },
    async getBasicInfo2() {
      const classInfo = await this.getClassInfo()
      if (!classInfo) return
      const works = await getWorks({ classId: classInfo.id, status: 2 })
      const list = works.filter((work) => work.status === 2).map((work) => this.normalizeWork(work))
      this.FinalClassWOrkList = await Promise.all(
        list.map(async (item) => {
          if (!item.groupSubmitted) {
            item.score = '教师尚未批改'
            return item
          }
          try {
            const submission = await getMySubmission(item.id)
            if (submission?.score !== null && submission?.score !== undefined) {
              item.score = submission.score
            } else {
              item.score = '教师尚未批改'
            }
          } catch (error) {
            item.score = '教师尚未批改'
          }
          return item
        })
      )
    },
    async getGroupIdNow() {
      try {
        const group = this.stuId ? await getStudentGroup(this.stuId) : await getMyGroup()
        this.groupId = group?.id || 0
      } catch (error) {
        console.error('获取学生小组失败', error)
        this.groupId = 0
      }
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
      if (row.detailurl === null || row.detailurl === undefined || row.detailurl === '') {
        ElMessage.warning('当前作业没有对应资源可供下载')
        return
      }
      const link = document.createElement('a')
      link.href = row.detailurl
      link.setAttribute('download', row.detailurl)
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
