<!--这个作为vditor的编辑载体-->
<template>
  <div>
    <div>编辑作业</div>
    <div id="vditor" class="vditor"></div>
    <div class="buttons-container">
      <a href="https://b3log.org/vditor/" target="_blank" class="tutorial-button">使用教程</a>
      <el-button class="upload-button" @click="uploadWork">
        <i class="iconfont icon-upload"></i>
        提交
      </el-button>
    </div>
  </div>
</template>
<script>
import { defineProps, ref, onMounted } from 'vue'
import Vditor from 'vditor'
import 'vditor/dist/index.css'
const contentEditor = ref('')
export default {
  setup() {
    const contentEditor = ref(null)
    onMounted(() => {
      contentEditor.value = new Vditor('vditor', {
        height: 700,
        toolbarConfig: {
          pin: true
        },
        mode: 'ir', // 设置默认模式为所见即所得（ir）模式
        outline: true, // 显示大纲
        cache: {
          enable: false
        },
        //测试过了 不用使用lazy加载的模式，直接上传md转string的内容他自己就能够渲染的
        // 只要链接能够正常的访问就ok了的
        upload: {
          accept: 'image/*,.mp3, .wav, .rar',
          token: 'test',
          url: '/api/minio/upload', // 修改为 MinIO 的上传接口地址
          linkToImgUrl: '/api/minio/getUrl', // 修改为 MinIO 的获取文件 URL 接口地址
          filename(name) {
            return name.replace(/[^(a-zA-Z0-9\u4e00-\u9fa5\.)]/g, '')
            replace(/[\?\\/:|<>\*\[\]\(\)\$%\{\}@~]/g, '').replace('/\\s/g', '')
          }
        },
        after: () => {
          contentEditor.value.setValue('vditor test')
        }
      })
    })

    return {
      contentEditor
    }
  },
  /////////////////////////////
  methods: {
    uploadWork() {
      // 在这里调用上传作业的逻辑
      console.log('上传作业')
    }
  }
}
</script>
<style>
.vditor {
  height: 80vh;
  /* 添加其他样式 */
}

.upload-button,
.tutorial-button {
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 8px 16px;
  cursor: pointer;
  margin-right: 10px; /* 设置按钮之间的间距 */
}

.upload-button:hover,
.tutorial-button:hover {
  background-color: #0056b3;
}

.upload-button i {
  margin-right: 4px;
}

.tutorial-button {
  background-color: #28a745; /* 浅绿色 */
}

.buttons-container {
  float: right; /* 将按钮容器浮动到右侧 */
  margin-right: 20px; /* 与页面右侧保持 20px 的距离 */
}
</style>
