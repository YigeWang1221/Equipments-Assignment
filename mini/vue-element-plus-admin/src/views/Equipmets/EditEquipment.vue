<template>
  <el-form :model="form" label-width="auto" style="max-width: 600px">
    <el-form-item label="器材名称">
      <el-input v-model="form.name" />
    </el-form-item>
    <el-form-item label="器材数量">
      <el-input v-model="form.number" placeholder="1~999" />
    </el-form-item>
    <el-form-item>
      <!--    这里用v-if 如果是编辑进入的，那就展示原有图片-->
      <el-image
        v-if="$route.params.routerParam === 'edit'"
        :src="equipment.photoUrl ? equipment.photoUrl : 'src/assets/imgs/Nophoto.jpg'"
      />
    </el-form-item>
    <el-form-item align="middle" label="上传替换/初始图片">
      <!--      这里用upload上传图片 上传方法使用自己的upload(file)，返回值是res{code，url}-->
      <el-upload
        class="avatar-uploader"
        :show-file-list="false"
        :on-success="handleAvatarSuccess"
        :before-upload="beforeAvatarUpload"
        :limit="1"
      >
        <img v-if="imageUrl" :src="imageUrl" class="avatar" />
        <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
      </el-upload>
    </el-form-item>
    <!--    这里添加一个上传zip的upload 上传方法使用自己的upload(file)-->
    <el-form-item label="上传压缩包">
      <el-upload
        v-model:file-list="fileList"
        class="upload-demo"
        action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15"
        multiple
        :on-preview="handlePreview"
        :on-remove="handleRemove"
        :before-remove="beforeRemove"
        :limit="3"
        :on-exceed="handleExceed"
      >
        <el-button type="primary">Click to upload</el-button>
        <template #tip>
          <div class="el-upload__tip"> zip files with a size less than 5mb. </div>
        </template>
      </el-upload>
    </el-form-item>
    <el-form-item label="Instant delivery">
      <el-switch v-model="form.delivery" />
    </el-form-item>
    <el-form-item label="详细描述">
      <!--      如果有的话就是detail-->
      <el-input v-model="form.desc" type="textarea" :placeholder="form.desc" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="onSubmit">Create</el-button>
      <el-button>Cancel</el-button>
    </el-form-item>
  </el-form>
</template>

<!--记得换成js-->
<script lang="js" setup>
import { reactive, ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'
// do not use same name with ref

// 传入这个界面的变量有两个，一个是 routerParam 区别add/editor 另一个是equipment
const form = reactive({
  name: '',
  region: '',
  number: 1,
  date1: '',
  date2: '',
  delivery: false,
  type: [],
  resource: '',
  desc: ''
})

const onSubmit = () => {
  console.log('submit!')
}
const imageUrl = ref('')
const handleAvatarSuccess = (response, file) => {
  imageUrl.value = URL.createObjectURL(file.raw)
}

const beforeAvatarUpload = (file) => {
  upload(file)
}

const upload = (file) => {
  // 调用自定义的上传方法，假设 upload 方法返回一个包含图片 URL 的 Promise 对象
  upload(file)
    .then((res) => {
      imageUrl.value = res.url
    })
    .catch((error) => {
      console.error('Error uploading image:', error)
    })
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
