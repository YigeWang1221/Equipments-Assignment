<script lang="js">
import { ElDivider, ElMessage } from 'element-plus'
import { defineComponent } from 'vue'
import EditorRead from '@/views/Function/EditorRead.vue'
import axios from 'axios'
import { VideoPlayer, createVideoViewer } from '@/components/VideoPlayer'

export default defineComponent({
  components: { EditorRead },
  data() {
    return {
      workDoc: '点击批改作业按钮开始批改作业',
      VideoUrl: '',
      mark: '',
      zip: '',
      WorkId: '',
      className: '',
      stuWorkList: [],
      StuWorkId: '',
      StuWorkItem: '',
      workname: ''
    }
  },
  created() {
    this.WorkId = this.$route.query.workid
    this.className = this.$route.query.className
    this.workname = this.$route.query.WorkName
    this.getStuWorkList()
    console.log(this.className)
    console.log(this.workname)
  },
  methods: {
    async getStuWorkList() {
      let url = 'http://47.120.78.42:8090/zjgsEmb/TeacherWork/getMiniStuWorkTableById'
      const res = await axios.get(url, {
        params: {
          workId: this.WorkId
        }
      })
      this.stuWorkList = res.data.data
    },
    async getStuWorkItem(row) {
      this.mark = null
      this.StuWorkId = row.stuWorkId
      let url = 'http://47.120.78.42:8090/zjgsEmb/TeacherWork/getStuWorkTableByWorkId'
      const res = await axios.get(url, {
        params: {
          StuworkId: row.stuWorkId
        }
      })
      console.log(res.data)
      const data = res.data.data
      this.workDoc = res.data.data.workDoc
      // item.photoUrl = item.photoUrl.substring(1, item.photoUrl.length - 1)
      this.zip = ''
      this.VideoUrl = ''
      this.VideoUrl = data.video.substring(1, data.video.length - 1)
      this.zip = data.zip.substring(1, data.zip.length - 1)
      this.StuWorkItem = data
    },
    downLoadZip() {
      console.log(this.zip)
      if (this.zip === 'ul' || this.zip === null || this.zip === '') {
        ElMessage.warning('当前作业没有可下载的资源')
        return
      }
      const link = document.createElement('a')
      link.href = this.zip
      link.setAttribute('download', '学生作业资料')
      link.click()
    },
    showVideo() {
      if (this.VideoUrl === 'ul' || this.VideoUrl === null || this.VideoUrl === '') {
        ElMessage.warning('当前作业没有可播放的视频')
        return
      }
      console.log(this.VideoUrl)
      createVideoViewer({
        url: this.VideoUrl,
        poster: 'http://47.120.78.42:9001/files/2024/04/21/9a92b636e5a94d87a0da8607773beee8.png'
      })
    },
    async updateScore() {
      console.log('this.stuWorkId ' + this.StuWorkId)
      if (this.StuWorkId === '') {
        ElMessage.error('请先选择一份作业')
      }
      if (this.mark !== '' && !isNaN(this.mark) && this.mark >= 0 && this.mark <= 100) {
        let markFloat = parseFloat(this.mark)
        let url =
          'http://47.120.78.42:8090/zjgsEmb/TeacherWork/CorrectHomeWork?StuWorkId=' +
          this.StuWorkId +
          '&mark=' +
          this.mark
        const res = await axios.post(url).then(() => {
          this.getStuWorkList()
        })
      } else {
        ElMessage.error('分数的类型错误，重新输入提交')
      }
    },
    async setGreatWork() {
      if (this.StuWorkId === '') {
        ElMessage.error('请先选择一份作业')
      }
      let url = 'http://47.120.78.42:8090/zjgsEmb/GreatWork/setGreatWorkByStuWorkId'
      const formData = new FormData()
      formData.append('StuWorkId', this.StuWorkId)
      formData.append('workName', this.workname)
      const res = await axios.post(url, formData)
      console.log(res.data)
      if (res.data.state === 300) {
        ElMessage.error('请不要对一份作业重复创建优秀作业')
        return
      }
      ElMessage.success('设置成功，你可以在优秀作业展示面板对作业进行编辑')
    }
  }
})
</script>

<template>
  <div>
    <!-- 上边界 -->
    <el-divider class="divider" direction="horizontal" content-position="left" />

    <!-- 你接下来要展示的内容 -->
    <!--    使用el-table来展示内容   第一个是已经布置的作业列表-->
    <div align="middle"> 本次作业已提交的列表</div>
    <el-scrollbar height="250px">
      <div align="middle">
        <el-table :data="stuWorkList" border stripe stye="width: 100%" title="班级作业列表">
          <el-table-column fixed="left" label="班级名称" width="240">{{
            className
          }}</el-table-column>
          <el-table-column prop="groupName" label="小组编号" width="240" />
          <el-table-column prop="groupNum" label="小组人员数量" width="240" />
          <el-table-column prop="members" label="学生名称" width="240" />
          <el-table-column prop="score" label="分数" width="240" />
          <el-table-column fixed="right" label="Operations" width="240">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="getStuWorkItem(row)"
                >点击批改作业</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-scrollbar>
    <!-- 下边界 -->
    <el-divider class="divider" direction="horizontal" content-position="right" />
  </div>
  <el-row :gutter="20" style="margin-top: 10px">
    <el-col :span="5"
      ><ContentWrap title="演示视频">
        <el-button
          v-if="VideoUrl !== null"
          style="margin-top: 4px"
          type="primary"
          @click="showVideo"
          >弹窗展示演示视频</el-button
        >
      </ContentWrap></el-col
    >
    <el-col :span="5"
      ><el-input
        v-model="mark"
        style="width: 240px"
        :placeholder="'输入分数1-100 当前分数 ' + StuWorkItem.mark"
    /></el-col>
    <el-col :span="3"><el-button type="primary" @click="updateScore">提交分数</el-button> </el-col>
    <el-col :span="3"
      ><el-button type="primary" @click="setGreatWork">设置为优秀作业</el-button>
    </el-col>
    <el-col :span="4"
      ><el-button type="primary" @click="downLoadZip">下载作业附件(zip)</el-button>
    </el-col>
  </el-row>

  <el-divider />
  <div>
    <EditorRead :modelValue="workDoc" />
  </div>
</template>
<style>
.vditor-container {
  border: 2px solid blue;
  padding: 10px;
}
</style>
