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
      workDoc: '测试一下传入的内容',
      VideoUrl: '',
      PhotoUrl: '',
      greatWorkId: '',
      inputWorkName: '',
      fun: '',
      groupNum: '',
      classType: '',
      classType1: ''
    }
  },
  created() {
    this.fun = this.$route.query.fun
    this.greatWorkId = this.$route.query.greatworkId
    this.classType = -1
    this.getGreateWorkItem()
  },
  methods: {
    async getGreateWorkItem() {
      let url = 'http://47.120.78.42:8090/zjgsEmb/GreatWork/getGreateWorkById'
      const res = await axios.get(url, {
        params: {
          workId: this.greatWorkId
        }
      })
      this.greatWorkItem = res.data.data
      console.log(this.greatWorkItem)
      const data = res.data.data
      this.workDoc = res.data.data.workDoc
      this.VideoUrl = res.data.data.video
      this.PhotoUrl = data.photo
      console.log(data.workName)
      this.inputWorkName = data.workName
      this.groupNum = data.stuName
      // this.VideoUrl = this.VideoUrl.substring(1, this.VideoUrl.length - 1)
    },
    showVideo() {
      if (this.VideoUrl === 'ul' || this.VideoUrl === null || this.VideoUrl === '') {
        ElMessage.warning('当前作业没有可播放的视频')
        return
      }
      console.log(this.VideoUrl)
      const url = this.VideoUrl.substring(1, this.VideoUrl.length - 1)
      createVideoViewer({
        url: url,
        poster: 'http://47.120.78.42:9001/files/2024/04/21/9a92b636e5a94d87a0da8607773beee8.png'
      })
    },
    handlePreview1(uploadFile) {
      const allowedExtensions = ['jpg', 'jpeg', 'png', 'webp']
      const fileExtension = uploadFile.name.split('.').pop().toLowerCase()
      if (!allowedExtensions.includes(fileExtension)) {
        ElMessage({
          message: '上传文件格式不正确，应该为图片文件，jpg/png/jpeg/webp',
          grouping: true,
          type: 'warning'
        })
        return new Promise((resolve, reject) => {
          return reject()
        })
      }
      if (uploadFile.size > 1024 * 1024) {
        ElMessage({
          message: '图片>=1MB',
          grouping: true,
          type: 'warning'
        })
        return new Promise((resolve, reject) => {
          return reject()
        })
      }
    },
    getUrlRes(res) {
      this.PhotoUrl = JSON.stringify(res)
      console.log(this.PhotoUrl)
    },
    async updateGreateWork() {
      let url = 'http://47.120.78.42:8090/zjgsEmb/GreatWork/updateGreatWorkByWorkId'
      const formData = new FormData()
      formData.append('GreatWorkId', this.greatWorkId)
      formData.append('photo', this.PhotoUrl)
      formData.append('workName', this.inputWorkName)
      formData.append('classType', this.classType)
      axios.post(url, formData)
      ElMessage.success('更新成功')
    },
    async deleteItem() {
      let url = 'http://47.120.78.42:8090/zjgsEmb/GreatWork/DeleteByShowId'
      const res = await axios.get(url, {
        params: {
          ShowId: this.greatWorkId
        }
      })
      ElMessage.success('删除成功即将跳转')
      this.$router.push({
        path: '/function/showListBoard'
      })
    },
    setClassType1() {
      this.classType = 2
      this.classType1 = '物联网'
    },
    setClassType2() {
      this.classType = 1
      this.classType1 = '嵌入式'
    }
  }
})
</script>

<template>
  <el-row :gutter="20">
    <el-col :span="6"
      ><el-button style="margin-top: 4px" type="primary" @click="showVideo"
        >弹窗展示演示视频</el-button
      ></el-col
    >
    <el-col :span="6">
      <el-upload
        v-if="this.fun === 'update'"
        style="margin-top: 10px"
        class="upload-demo"
        action="http://47.120.78.42:8090/upload"
        multiple
        :http-request="uploadMehod"
        :before-upload="handlePreview1"
        :on-success="getUrlRes"
        :before-remove="beforeRemove"
        :limit="1"
      >
        <el-button type="primary">上传要替换的封面图片url</el-button>
      </el-upload>
    </el-col>
    <el-col :span="6">
      <el-input
        v-if="this.fun === 'update'"
        v-model="inputWorkName"
        style="width: 100%"
        placeholder="输入新的优秀作业名称"
      />
    </el-col>
    <el-col :span="6"
      ><el-button v-if="this.fun === 'update'" type="success" @click="updateGreateWork"
        >确认当前修改</el-button
      >
    </el-col>
  </el-row>
  <el-divider />
  <el-row :gutter="20">
    <el-col :span="6"
      ><el-text size="large" type="primary">项目成员 {{ groupNum }}</el-text></el-col
    >
    <el-col :span="6"
      ><el-dropdown size="medium" trigger="hover">
        <el-button v-if="this.fun === 'update'" type="primary">
          课程选择 <el-icon class="el-icon--right">{{ classType1 }}</el-icon>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="setClassType1">物联网课程</el-dropdown-item>
            <el-dropdown-item @click="setClassType2">嵌入式课程</el-dropdown-item>
          </el-dropdown-menu>
        </template> </el-dropdown
      ><div>{{ selectedClassName }}</div></el-col
    >
    <el-col :span="6"
      ><el-button v-if="this.fun === 'update'" type="warning" @click="deleteItem"
        >删除此展示作业</el-button
      >
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
