<script lang="js">
import { ElDivider, ElMessage } from 'element-plus'
import { defineComponent } from 'vue'
import EditorRead from '@/views/Function/EditorRead.vue'
import { createVideoViewer } from '@/components/VideoPlayer'
import { getGreatWork, updateGreatWork, deleteGreatWork } from '@/api/equip/greatWorks'
import { uploadFile } from '@/api/equip/files'

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
      classType1: '',
      year: ''
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
      const data = await getGreatWork(this.greatWorkId)
      this.greatWorkItem = data
      this.workDoc = data.workDoc || ''
      this.VideoUrl = data.videoUrl || ''
      this.PhotoUrl = data.photoUrl || ''
      this.inputWorkName = data.workName || ''
      this.groupNum = data.members || data.groupName || ''
      this.classType = data.classType || ''
      this.year = data.year || ''
      if (data.classType === 2) {
        this.classType1 = '物联网'
      } else if (data.classType === 1) {
        this.classType1 = '嵌入式'
      }
    },
    showVideo() {
      if (!this.VideoUrl || this.VideoUrl === 'ul') {
        ElMessage.warning('当前作业没有可播放的视频')
        return
      }
      createVideoViewer({ url: this.VideoUrl })
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
      this.PhotoUrl = res?.url || ''
    },
    async uploadImage(params) {
      try {
        const file = await uploadFile(params.file)
        this.PhotoUrl = file.url
        params.onSuccess?.(file)
      } catch (error) {
        console.error(error)
      }
    },
    async updateGreateWork() {
      await updateGreatWork(this.greatWorkId, {
        workName: this.inputWorkName,
        photoUrl: this.PhotoUrl,
        classType: this.classType
      })
      ElMessage.success('更新成功')
    },
    async deleteItem() {
      await deleteGreatWork(this.greatWorkId)
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
        v-if="fun === 'update'"
        style="margin-top: 10px"
        class="upload-demo"
        action="#"
        multiple
        :http-request="uploadImage"
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
        v-if="fun === 'update'"
        v-model="inputWorkName"
        style="width: 100%"
        placeholder="输入新的优秀作业名称"
      />
    </el-col>
    <el-col :span="6"
      ><el-button v-if="fun === 'update'" type="success" @click="updateGreateWork"
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
        <el-button v-if="fun === 'update'" type="primary">
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
      ><el-button v-if="fun === 'update'" type="warning" @click="deleteItem"
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
