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
        action="http://47.120.78.42:8090/upload"
        :show-file-list="false"
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
        action="http://47.120.78.42:8090/upload"
        multiple
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
import { reactive, ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { reject } from 'lodash-es'
import async from 'async'
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
    async getEquipById(id) {
      let url = 'http://47.120.78.42:8090/zjgsEmb/EquipMent/getEquipmentById'
      const res = await axios.get(url, {
        params: {
          id: this.EquipId
        }
      })
      console.log(res)
      const dataX = res.data.data
      this.form.name = dataX.name
      this.form.photoUrl = dataX.photoUrl.substring(1, dataX.photoUrl.length - 1)
      // console.log(dataX.photoUrl)
      this.form.number = dataX.number
      this.used = dataX.used
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
      this.form.infoUrl = JSON.stringify(res)
      console.log(this.form.infoUrl)
    },
    getPhotoRes(res) {
      this.imageUrl = JSON.stringify(res)
      this.form.photoUrl = JSON.stringify(res)
      console.log('getPhotoRes' + this.imageUrl)
    },
    async uploadAllInfo() {
      if (this.fun === 'edit') {
        console.log(this.fun)
        if (this.form.name === null || this.form.number === null) {
          ElMessage({
            message: '名字和数量不能为空',
            grouping: true,
            type: 'warning'
          })
          return
        }
        //上传的代码
        const formData = new FormData()
        if (this.form.infoUrl !== null) {
          formData.append('infoUrl', this.form.infoUrl)
        }

        formData.append('photoUrl', this.imageUrl)
        console.log(this.imageUrl)
        formData.append('number', this.form.number)
        formData.append('id', this.EquipId)
        formData.append('name', this.form.name)
        formData.append('used', 0)
        let url = 'http://47.120.78.42:8090/zjgsEmb/EquipMent/updateEquip'
        const res = await axios.post(url, formData)
        ElMessage.warning(res.data.data)
        this.$router.push({
          path: '/guide/index'
        })
      }
      if (this.fun === 'add' || this.fun === null) {
        console.log(this.fun)
        if (this.form.name === null || this.form.number === null) {
          ElMessage({
            message: '名字和数量不能为空',
            grouping: true,
            type: 'warning'
          })
          return
        }
        //上传的代码
        const formData = new FormData()
        formData.append('infoUrl', this.form.infoUrl)
        formData.append('photoUrl', this.imageUrl)
        formData.append('number', this.form.number)
        formData.append('name', this.form.name)
        formData.append('used', 0)
        let url = 'http://47.120.78.42:8090/zjgsEmb/EquipMent/initEquipment'
        const res = await axios.post(url, formData)
        ElMessage({
          message: '新建成功',
          grouping: true,
          type: 'success'
        })
      }
    },
    async DeleteEquip() {
      let url = 'http://47.120.78.42:8090/zjgsEmb/EquipMent/deleteEquipmentById'
      const res = await axios.get(url, {
        params: {
          id: this.EquipId
        }
      })
      ElMessage.success('删除成功')
      console.log(res.data)
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
