<script lang="js">
// 教师查看班级列表的界面 ARM页面
import MyEditor from '@/views/Dashboard/components/MyEditor.vue'
import { ElMessage } from 'element-plus'
import { initMySubmission, updateSubmission } from '@/api/equip/submissions'
import { uploadFile } from '@/api/equip/files'
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
      const data = await initMySubmission(this.TeacherWorkId)
      this.StuWorkId = data.id
      this.workDoc = data.workDoc || ''
      this.infoUrl = data.zipUrl || ''
      this.VideoUrl = data.videoUrl || ''
    },
    async doUpload(params, apply) {
      try {
        const file = await uploadFile(params.file)
        apply(file.url)
        params.onSuccess?.(file)
      } catch (error) {
        console.error(error)
      }
    },
    uploadZip(params) {
      return this.doUpload(params, (url) => {
        this.infoUrl = url
      })
    },
    uploadVideo(params) {
      return this.doUpload(params, (url) => {
        this.VideoUrl = url
      })
    },
    uploadPhoto(params) {
      return this.doUpload(params, (url) => {
        this.PhotoUrl = url
      })
    },
    getUrlResVideo(res) {
      this.VideoUrl = res?.url || ''
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
        return Promise.reject()
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
      this.infoUrl = res?.url || ''
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
      this.PhotoUrl = res?.url || ''
    },
    async saveWork() {
      await updateSubmission(this.StuWorkId, {
        workDoc: this.workDoc,
        videoUrl: this.VideoUrl,
        zipUrl: this.infoUrl,
        submit: true
      })
      ElMessage.success('更新作业成功，继续编辑或退出')
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
        action="#"
        multiple
        :http-request="uploadZip"
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
        action="#"
        multiple
        :http-request="uploadVideo"
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
    action="#"
    multiple
    :http-request="uploadPhoto"
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
