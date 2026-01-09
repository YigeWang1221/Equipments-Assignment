<script setup lang="ts">
import { ref } from 'vue'
// import { ElMessage } from 'element-plus'
// import {ElMessage} from "element-plus"
// const props = defineProps({
//   onSubmitFinished: Function
// })
///////////////////////////上传逻辑
const headers = {
  'Content-Type': 'multipart/form-data'
}
let fileUpload = ref()
const handleChange = (file: any) => {
  fileUpload.value = file
}
const submitUploadZip = async () => {
  let param = new FormData()
  //todo upload video同理
  param.append('file', fileUpload.value.raw)
  // todo 上传的接口
  // const res = await upLoadCheckProject(param)
  // if (res.code === 200) {
  //   props.onSubmitFinished!(res)
  //   ElMessage({
  //     message: '上传成功',
  //     type: 'success'
  //   })
  // } else {
  //   ElMessage({
  //     message: '上传失败',
  //     type: 'error'
  //   })
  // }
}
</script>

<template>
  <div
    ><el-row :gutter="20">
      <el-col :span="6"><div class="grid-content ep-bg-purple"></div></el-col>
      <el-col :span="6">
        <div class="grid-content ep-bg-purple-light">
          <el-upload
            class="upload-demo"
            action="#"
            :auto-upload="false"
            :limit="1"
            :headers="headers"
            accept=".zip"
            :on-change="handleChange"
          >
            <template #trigger>
              <el-button type="primary">选取压缩文件</el-button>
            </template>

            <el-button class="ml-3" type="success" @click="submitUploadZip"> 上传 </el-button>

            <template #tip>
              <div class="el-upload__tip"> 上传压缩文件，大小不超过40mb，限制单个重传覆盖 </div>
            </template>
          </el-upload>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="grid-content ep-bg-purple">
          <el-upload
            ref="uploadRef"
            class="upload-demo"
            action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15"
            :auto-upload="false"
          >
            <template #trigger>
              <el-button type="primary">选取视频文件</el-button>
            </template>

            <el-button class="ml-3" type="success"> 上传 </el-button>

            <template #tip>
              <div class="el-upload__tip"> 上传视频文件，大小不超过40mb ，限制单个重传覆盖</div>
            </template>
          </el-upload>
        </div>
      </el-col>
    </el-row></div
  >
</template>

<style scoped lang="less"></style>
