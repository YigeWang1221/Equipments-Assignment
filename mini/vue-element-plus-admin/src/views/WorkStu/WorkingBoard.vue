<script lang="js">
// 教师查看班级列表的界面 ARM页面
import MyEditor from '@/views/Dashboard/components/MyEditor.vue'
import { useUserStore } from '@/store/modules/user'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { reject } from 'lodash-es'
export default {
  components: { MyEditor },
  data() {
    return {
      editorValue: '',
      PageClassId: '',
      TeacherWorkId: '',
      VideoUrl: '',
      infoUrl: '',
      StuWorkId: '',
      workName: '',
      workDoc: '',
      PhotoUrl: '',
      fun: '',
      groupId: ''
    }
  },
  created() {
    this.groupId = this.$route.query.groupId
    this.TeacherWorkId = this.$route.query.TeacherWorkId
    this.workName = this.$route.query.workname
    console.log('this.groupId' + this.groupId)
    console.log('this.workId' + this.TeacherWorkId)
    this.PageInit()
  },
  methods: {
    async PageInit() {
      //我不知道为什么，但是请求两次就好了
      let url = 'http://47.120.78.42:8090/zjgsEmb/Stu/selectHomeWorkByTeacherWorkIdAndGroupId'
      await axios.get(url, {
        params: {
          TeacherWorkId: this.TeacherWorkId,
          groupId: this.groupId
        }
      })
      const res = await axios.get(url, {
        params: {
          TeacherWorkId: this.TeacherWorkId,
          groupId: this.groupId
        }
      })
      const data = res.data.data
      console.log(data)
      this.StuWorkId = data.id
      this.workDoc = data.workDoc
      this.infoUrl = data.zip
      this.VideoUrl = data.video
    },
    getUrlResVideo(res) {
      this.VideoUrl = JSON.stringify(res)
    },
    handlePreviewVideo(uploadFile) {
      const allowedExtensions = ['mp4', 'avi', 'mov', 'wmv', 'flv']
      const fileExtension = uploadFile.name.split('.').pop().toLowerCase()

      if (!allowedExtensions.includes(fileExtension)) {
        ElMessage({
          message: '上传文件格式不正确，应该为视频文件，mp4/avi/mov/wmv/flv',
          grouping: true,
          type: 'warning'
        })
        return reject()
      }

      if (uploadFile.size > 1024 * 1024 * 300) {
        // 限制文件大小为 1GB
        ElMessage({
          message: '视频文件大小不能超过300MB',
          grouping: true,
          type: 'warning'
        })
        return Promise.reject()
      }

      return reject()
    },
    handlePreviewZip(uploadFile) {
      console.log(uploadFile.type)
      if (uploadFile.type !== 'application/x-zip-compressed') {
        ElMessage({
          message: '上传文件格式不正确，应该为zip文件',
          grouping: true,
          type: 'warning'
        })
        return new Promise((resolve, reject) => {
          return reject()
        })
      }
      if (uploadFile.size > 1024 * 1024 * 15) {
        ElMessage({
          message: '文件>=15MB',
          grouping: true,
          type: 'warning'
        })
        return new Promise((resolve, reject) => {
          return reject()
        })
      }
    },
    getUrlResZip(res) {
      this.infoUrl = JSON.stringify(res)
    },
    handlePreviewPhoto(uploadFile) {
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
    getUrlResPhoto(res) {
      this.PhotoUrl = JSON.stringify(res)
    },
    async saveWork() {
      const formData = new FormData()
      formData.append('InfoUrl', this.infoUrl)
      formData.append('StuWorkId', this.StuWorkId)
      formData.append('Video', this.VideoUrl)
      formData.append('workDoc', this.workDoc)
      console.log(formData)

      let url = 'http://47.120.78.42:8090/zjgsEmb/Stu/updateStuHomeWork'
      const res = await axios.post(url, formData)
      console.log(res.data)
      if (res.data.state === 200) {
        ElMessage.success('更新作业成功，继续编辑或退出')
      }
    }
  }
}
</script>

<template>
  <el-form-item label="作业名称">
    <el-input disabled v-model="workName" :placeholder="workname" />
  </el-form-item>
  <el-divider />
  <el-divider />
  <el-row :gutter="20">
    <el-col :span="8"
      ><el-upload
        style="margin-top: 10px"
        class="upload-demo"
        action="http://47.120.78.42:8090/upload"
        multiple
        :http-request="uploadMehod"
        :before-upload="handlePreviewZip"
        :on-success="getUrlResZip"
        :before-remove="beforeRemove"
        :limit="1"
        ><el-button type="primary">上传作业附件zip</el-button></el-upload
      ></el-col
    >
    <el-col :span="8"
      ><el-upload
        style="margin-top: 10px"
        class="upload-demo"
        action="http://47.120.78.42:8090/upload"
        multiple
        :http-request="uploadMehod"
        :before-upload="handlePreviewVideo"
        :on-success="getUrlResVideo"
        :before-remove="beforeRemove"
        :limit="1"
        ><el-button type="primary">上传作业演示视频</el-button></el-upload
      ></el-col
    >
    <el-col :span="4" />
    <el-col :span="4" />
  </el-row>

  <el-divider />
  <div>
    <MyEditor v-model="workDoc" />
  </div>
  <el-upload
    style="margin-top: 10px"
    class="upload-demo"
    action="http://47.120.78.42:8090/upload"
    multiple
    :http-request="uploadMehod"
    :before-upload="handlePreviewPhoto"
    :on-success="getUrlResPhoto"
    :before-remove="beforeRemove"
    :limit="1"
  >
    <el-button type="primary">上传要导入的图片url</el-button>
    <template #tip>
      <div class="el-upload__tip"> 将返回的url导入富文本编辑器. </div>
    </template>
  </el-upload>
  <div>{{ PhotoUrl }}</div>
  <el-tooltip content="你在截止之前能够一直编辑作业内容，但是记得保存">
    <el-button @click="saveWork" type="primary">保存作业内容</el-button>
  </el-tooltip>
</template>

<style scoped lang="less"></style>
