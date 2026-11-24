<template>
  <el-form :model="form" label-width="auto" style="max-width: 600px">
    <el-form-item label="器材名称">
      <el-input v-model="form.name" />
    </el-form-item>
    <el-form-item label="器材数量">
      <el-input v-model="form.number" placeholder="1~999" />
    </el-form-item>
    <el-form-item>
      <!--    这里用v-if 如果是添加进入的，那就展示原有图片-->
      <el-image
        style="max-width: 200px; max-height: 280px"
        v-if="fun === 'add'"
        :src="'/src/assets/imgs/NoPhoto.jpg'"
      />
      <el-image
        style="max-width: 200px; max-height: 280px"
        v-if="fun === 'edit'"
        :src="form.photoUrl"
      />
    </el-form-item>
    <el-form-item align="middle" label="上传替换/初始图片">
      <!--      这里用upload上传图片 上传方法使用自己的upload(file)，返回值是res{code，url}-->
      <el-upload
        class="avatar-uploader"
        action="#"
        :show-file-list="false"
        :http-request="uploadPhoto"
        :on-success="getPhotoRes"
        :before-upload="PhotoJudge"
        :limit="1"
      >
        <img v-if="form.photoUrl" :src="form.photoUrl" class="avatar" />
        <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
      </el-upload>
    </el-form-item>
    <!--    这里添加一个上传zip的upload 上传方法使用自己的upload(file)-->
    <el-form-item label="上传压缩包">
      <el-upload
        v-model:file-list="fileList"
        class="upload-demo"
        action="#"
        multiple
        :http-request="uploadZip"
        :before-upload="ZipJudge"
        :on-success="getZipRes"
        :on-preview="handlePreview"
        :on-remove="handleRemove"
        :before-remove="beforeRemove"
        :limit="1"
        :on-exceed="handleExceed"
      >
        <el-button type="primary">点击上传</el-button>
        <template #tip>
          <div class="el-upload__tip"> zip资源包不能超过10mb. </div>
        </template>
      </el-upload>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="uploadAllInfo">新建/更新</el-button>
      <el-button @click="DeleteEquip">删除器材</el-button>
    </el-form-item>
  </el-form>
</template>

<!--记得换成js-->
<script lang="js">
import { ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import {
  getEquipment,
  createEquipment,
  updateEquipment,
  deleteEquipment
} from '@/api/equip/equipments'
import { uploadFile } from '@/api/equip/files'
//传入这个界面的变量有两个，一个是 routerParam 区别add/editor 另一个是equipment
export default {
  data() {
    return {
      form: ref({
        name: '',
        photoUrl: '',
        number: null,
        infoUrl: null
      }),
      imageUrl: '',
      fun: null,
      EquipId: null,
      used: null
    }
  },
  created() {
    this.fun = this.$route.query.fun
    if (this.fun === 'edit') {
      this.EquipId = this.$route.query.EquipId
      this.getEquipById(this.EquipId)
    }
  },
  methods: {
    async getEquipById() {
      const dataX = await getEquipment(this.EquipId)
      this.form.name = dataX.name
      this.form.photoUrl = dataX.photoUrl || ''
      this.form.number = dataX.totalNumber
      this.form.infoUrl = dataX.infoUrl || null
      this.used = dataX.usedNumber
      this.EquipId = dataX.id
      this.imageUrl = dataX.photoUrl
    },
    ZipJudge(uploadFile) {
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
    PhotoJudge(uploadFile) {
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
    getZipRes(res) {
      this.form.infoUrl = res?.url || ''
    },
    getPhotoRes(res) {
      this.imageUrl = res?.url || ''
      this.form.photoUrl = res?.url || ''
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
    uploadPhoto(params) {
      return this.doUpload(params, (url) => {
        this.imageUrl = url
        this.form.photoUrl = url
      })
    },
    uploadZip(params) {
      return this.doUpload(params, (url) => {
        this.form.infoUrl = url
      })
    },
    validateEquipment() {
      if (!this.form.name || this.form.number === null || this.form.number === '') {
        ElMessage({
          message: '名字和数量不能为空',
          grouping: true,
          type: 'warning'
        })
        return false
      }
      return true
    },
    buildEquipmentPayload() {
      return {
        name: this.form.name,
        photoUrl: this.imageUrl || this.form.photoUrl || '',
        infoUrl: this.form.infoUrl || '',
        totalNumber: Number(this.form.number)
      }
    },
    async uploadAllInfo() {
      if (!this.validateEquipment()) return
      const payload = this.buildEquipmentPayload()
      if (this.fun === 'edit') {
        await updateEquipment(this.EquipId, payload)
        ElMessage.success('更新成功')
        this.$router.push({
          path: '/guide/index'
        })
      }
      if (this.fun === 'add' || this.fun === null) {
        await createEquipment(payload)
        ElMessage({
          message: '新建成功',
          grouping: true,
          type: 'success'
        })
      }
    },
    async DeleteEquip() {
      await deleteEquipment(this.EquipId)
      ElMessage.success('删除成功')
      this.$router.push({
        path: '/guide/index'
      })
    }
  }
}
</script>

<style scoped>
.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}
</style>
