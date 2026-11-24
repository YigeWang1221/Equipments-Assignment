<script lang="js">
// 教师查看班级列表的界面 ARM页面
import MyEditor from '@/views/Dashboard/components/MyEditor.vue'
import { useUserStore } from '@/store/modules/user'
import { ElMessage } from 'element-plus'
import { getWork, createWork, updateWork } from '@/api/equip/works'
import { uploadFile } from '@/api/equip/files'
export default {
  components: { MyEditor },
  data() {
    return {
      editorValue: '',
      PageClassId: '',
      workId: '',
      PhotoUrl: '',
      infoUrl: '',
      workName: '',
      date1: '',
      date2: '',
      workDoc: '',
      contentJson: '',
      fun: ''
    }
  },
  created() {
    const usrStore = useUserStore()
    this.PageClassId = usrStore.getClassID
    console.log('跳转到新建作业界面 classId(this.PageClassId)' + this.PageClassId)
    this.fun = this.$route.query.fun
    if (this.fun === 'update') {
      this.workId = this.$route.query.workid
      console.log('跳转到编辑作业界面 workId' + this.workId)
      this.initPageUpdateFun()
    }
    console.log(this.PageClassId)
  },
  methods: {
    async initPageUpdateFun() {
      const data = await getWork(this.workId)
      this.editorValue = data.contentHtml || ''
      this.contentJson = data.contentJson || ''
      this.date1 = (data.startTime || '').slice(0, 10)
      this.date2 = (data.endTime || '').slice(0, 10)
      this.workName = data.workName || ''
      this.infoUrl = data.detailUrl || ''
    },
    getUrlRes(res) {
      this.PhotoUrl = res?.url || ''
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
      if (uploadFile.size > 1024 * 1024 * 2) {
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
    handlePreview2(uploadFile) {
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
      if (uploadFile.size > 1024 * 1024 * 10) {
        ElMessage({
          message: '文件>=10MB',
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
    async uploadImg(params) {
      try {
        const file = await uploadFile(params.file)
        this.PhotoUrl = file.url
        params.onSuccess?.(file)
      } catch (error) {
        console.error(error)
      }
    },
    async uploadZip(params) {
      try {
        const file = await uploadFile(params.file)
        this.infoUrl = file.url
        params.onSuccess?.(file)
      } catch (error) {
        console.error(error)
      }
    },
    beforeRemove() {
      // 允许移除已选择的文件
      return true
    },
    selectTimeA(val) {
      this.date1 = val
    },
    selectTimeB(val) {
      this.date2 = val
    },
    buildWorkPayload() {
      return {
        classId: Number(this.PageClassId),
        workName: this.workName,
        contentHtml: this.editorValue,
        contentJson: this.contentJson,
        detailUrl: this.infoUrl,
        startTime: this.date1,
        endTime: this.date2
      }
    },
    async initWork() {
      if (this.workName === '') {
        ElMessage.error('必须设置作业名称')
        return
      }
      if (this.date2 === '' || this.date1 === '') {
        ElMessage.error('必须设置起止日期')
        return
      }
      await createWork(this.buildWorkPayload())
      ElMessage.success('新建作业成功')
      this.$router.push({
        path: '/dashboard/analysis',
        query: {
          id: this.PageClassId,
          funL: 'basic'
        }
      })
    },
    async updateWork() {
      await updateWork(this.workId, this.buildWorkPayload())
      ElMessage.success('更新作业内容成功')
      this.$router.push({
        path: '/dashboard/analysis',
        query: {
          id: this.PageClassId,
          funL: 'basic'
        }
      })
    }
  }
}
</script>

<template>
  <el-form-item label="作业名称">
    <el-input v-model="workName" />
  </el-form-item>
  <el-divider />
  <el-form-item label="作业起止日期">
    <el-col :span="11">
      <el-date-picker
        v-model="date1"
        type="date"
        @change="selectTimeA"
        placeholder="开始日期"
        style="width: 100%"
        format="YYYY-MM-DD"
        value-format="YYYY-MM-DD"
      />
    </el-col>
    <el-col :span="2" class="text-center">
      <span class="text-gray-500">-</span>
    </el-col>
    <el-col :span="11">
      <el-date-picker
        v-model="date2"
        type="date"
        @change="selectTimeB"
        placeholder="结束日期"
        style="width: 100%"
        format="YYYY-MM-DD"
        value-format="YYYY-MM-DD"
      />
    </el-col>
  </el-form-item>
  <el-divider />
  <el-upload
    style="margin-top: 10px"
    class="upload-demo"
    action="#"
    multiple
    :http-request="uploadZip"
    :before-upload="handlePreview2"
    :on-success="getUrlResZip"
    :before-remove="beforeRemove"
    :limit="1"
    ><el-button type="primary">上传作业附件zip</el-button></el-upload
  >
  <el-divider />
  <div>
    <MyEditor v-model="editorValue" />
  </div>
  <el-upload
    style="margin-top: 10px"
    class="upload-demo"
    action="#"
    multiple
    :http-request="uploadImg"
    :before-upload="handlePreview1"
    :on-success="getUrlRes"
    :before-remove="beforeRemove"
    :limit="1"
  >
    <el-button type="primary">上传要导入的图片url</el-button>
    <template #tip>
      <div class="el-upload__tip"> 将返回的url导入富文本编辑器. </div>
    </template>
  </el-upload>
  <div>{{ PhotoUrl }}</div>
  <el-button v-if="fun === 'init'" style="margin-top: 10px" type="danger" @click="initWork"
    >新建作业</el-button
  >
  <el-button v-if="fun === 'update'" style="margin-top: 10px" type="warning" @click="updateWork"
    >更新作业内容</el-button
  >
</template>

<style scoped lang="less"></style>
