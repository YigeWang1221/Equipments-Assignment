<template>
  <el-form :model="form" label-width="auto" style="max-width: 600px">
    <el-form-item label="器材名称">
      <el-input disabled v-model="form.name" />
    </el-form-item>
    <el-form-item label="器材总数量">
      <el-input disabled v-model="form.number" placeholder="1~999" />
    </el-form-item>
    <el-form-item label="器材已借出数量">
      <el-input disabled v-model="used" placeholder="1~999" />
    </el-form-item>
    <el-form-item label="当前申请数量">
      <el-input-number v-model="borrowNum" :min="1" :max="10000" @change="handleChange" />
    </el-form-item>
    <el-form-item>
      <!--    加入判断，是不是有图片资料的-->
      <el-image
        style="max-width: 400px; max-height: 680px"
        :src="this.form.photoUrl ? this.form.photoUrl : '/src/assets/imgs/NoPhoto.jpg'"
      />
    </el-form-item>
    <!--    这里添加一个上传zip的upload 上传方法使用自己的upload(file)-->
    <el-form-item>
      <el-button type="primary" @click="ApplyingBorrow">申请借用</el-button>
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
import { useUserStore } from '@/store/modules/user'
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
      used: null,
      StuId: '',
      borrowNum: ''
    }
  },
  created() {
    const usrStore = new useUserStore()
    this.EquipId = this.$route.query.EquipmentId
    this.StuId = usrStore.getStuId
    this.getEquipById()
  },
  methods: {
    handleChange() {
      if (this.borrowNum + this.used >= this.form.number) {
        this.borrowNum = 0
        ElMessage.error('借用数量不能超过剩余数量')
      }
      console.log(this.borrowNum)
    },
    async getEquipById(id) {
      let url = 'http://47.120.78.42:8090/zjgsEmb/EquipMent/getEquipmentById'
      const res = await axios.get(url, {
        params: {
          id: this.EquipId
        }
      })
      console.log(res.data.data)
      const dataX = res.data.data
      this.form.name = dataX.name
      this.form.photoUrl = dataX.photoUrl.substring(1, dataX.photoUrl.length - 1)
      console.log(dataX.photoUrl)
      this.form.number = dataX.number
      this.used = dataX.used
      this.EquipId = dataX.id
      this.imageUrl = dataX.photoUrl
    },
    async ApplyingBorrow() {
      let url = 'http://47.120.78.42:8090/zjgsEmb/EquipMent/StudentApplyEquipment'
      const formData = new FormData()
      formData.append('equipmentId', this.EquipId)
      formData.append('num', this.borrowNum)
      formData.append('stuId', this.StuId)
      const res = axios.post(url, formData).then((res) => {
        if (res.data.state === 300) {
          ElMessage.success('你尚未加入班级')
        } else {
          ElMessage.success('申请成功，等待教师审核')
        }
        this.$router.push({
          path: '/guide/StuBorrow'
        })
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
