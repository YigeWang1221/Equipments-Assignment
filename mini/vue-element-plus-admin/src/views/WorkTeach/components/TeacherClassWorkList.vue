<script lang="js">
//子组件，主要是一个el-list 是这个班级过去的所有作业和情况的list
import { useUserStore } from '@/store/modules/user'
import { defineProps } from 'vue'
import { ElMessage } from 'element-plus'
import { getClass } from '@/api/equip/classes'
import { getWorks, deleteWork } from '@/api/equip/works'
import { exportClassGrades } from '@/api/equip/exports'

export default {
  data() {
    return {
      classId: '',
      className: '',
      TeacherWorkList: [],
      handledWorkList: []
    }
  },
  created() {
    const usrStore = useUserStore()
    this.classId = usrStore.getClassID
    this.getBasicInfo(this.classId)
    console.log('教师作业列表classId ' + this.classId)
  },
  methods: {
    initNewHomeWork() {
      this.$router.push({
        path: '/dashboard/initHomeWork',
        query: {
          fun: 'init'
        }
      })
    },
    updateHomwork(row) {
      this.$router.push({
        path: '/dashboard/initHomeWork',
        query: {
          fun: 'update',
          workid: row.id
        }
      })
    },
    ToCorrectHomeWork(row) {
      this.$router.push({
        path: '/dashboard/TeacherCorrectHomework',
        query: {
          workid: row.id,
          className: this.className,
          WorkName: row.workname
        }
      })
    },
    async getBasicInfo(classId) {
      const classInfo = await getClass(classId)
      this.className = classInfo.className

      const works = await getWorks({ classId })
      const statusMap = { 0: '未开始', 1: '进行中', 2: '截止' }
      this.TeacherWorkList = works.map((work) => ({
        ...work,
        workname: work.workName,
        modifiedTime: work.startTime,
        isFinal: work.statusName || statusMap[work.status] || '时间处理中'
      }))
    },
    async DeleteHomwork(row) {
      await deleteWork(row.id)
      ElMessage.success('删除成功')
      this.getBasicInfo(this.classId)
    },
    async downLoadHomework() {
      ElMessage.success('后端处理中请耐心等待')
      try {
        const blob = await exportClassGrades(this.classId)
        const fileName = '班级综合成绩.xlsx'
        if ('download' in document.createElement('a')) {
          // 非IE下载
          const elink = document.createElement('a')
          elink.download = fileName
          elink.style.display = 'none'
          elink.href = URL.createObjectURL(blob)
          document.body.appendChild(elink)
          elink.click()
          URL.revokeObjectURL(elink.href) // 释放URL 对象
          document.body.removeChild(elink)
        } else {
          // IE10+下载
          navigator.msSaveBlob(blob, fileName)
        }
      } catch (error) {
        console.error('Error downloading Excel file:', error)
      }
    }
  }
}
</script>

<template>
  <div>
    <!-- 上边界 -->
    <el-divider class="divider" direction="horizontal" content-position="left" />

    <!-- 你接下来要展示的内容 -->
    <!--    使用el-table来展示内容   第一个是已经布置的作业列表-->
    <div align="middle"> 班级作业列表</div>
    <div align="middle">
      <el-scrollbar height="400px">
        <el-table :data="TeacherWorkList" border stripe stye="width: 100%">
          <el-table-column fixed="left" label="班级名称" width="240">{{
            className
          }}</el-table-column>
          <el-table-column prop="workname" label="作业名称" width="240" />
          <!--          <el-table-column prop="id" label="总编号" width="240" />-->
          <el-table-column prop="modifiedTime" label="开始时间" sortable width="240" />
          <el-table-column prop="endTime" label="截至时间" sortable width="240" />
          <el-table-column prop="isFinal" primary label="状态" sortable width="240" />
          <el-table-column fixed="right" label="Operations" width="240">
            <template #default="{ row }">
              <!--            这个方法要改一下-->
              <el-button
                link
                v-if="row.isFinal === '未开始'"
                type="primary"
                size="small"
                @click="updateHomwork(row)"
                >编辑</el-button
              >
              <el-button
                link
                v-if="row.isFinal === '截止'"
                type="warning"
                size="small"
                @click="ToCorrectHomeWork(row)"
                >跳转到批改作业</el-button
              >
              <el-button link type="danger" size="small" @click="DeleteHomwork(row)"
                >删除作业</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </el-scrollbar>
    </div>
    <div style="margin-top: 10px" id="button_lay">
      <!--      todo 这里写一个点击新建作业的 跳转到InitWork上-->
      <el-button class="initWorkButton" type="primary" align="middle" @click="initNewHomeWork"
        >点击新建作业</el-button
      >
    </div>
    <!-- 下边界 -->
    <el-divider class="divider" direction="horizontal" content-position="right" />
  </div>
  <div style="margin-top: 10px">
    <el-button type="primary" align="middle" @click="downLoadHomework"
      >点击导出班级成绩表单</el-button
    >
    <div>耐心等待10s</div>
  </div>
</template>

<style>
.divider {
  border-color: #87cefa; /* 浅蓝色边界线 */
  margin-top: 4px; /* 距离上边界 4px */
  margin-bottom: 4px; /* 距离下边界 4px */
}
.initWorkButton {
  margin-right: 10px;
}
</style>
