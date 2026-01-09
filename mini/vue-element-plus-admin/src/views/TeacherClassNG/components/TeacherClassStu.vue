<script lang="js">
import { setup } from 'mockjs'
import async from 'async'
import axios from 'axios'
import { useUserStore } from '@/store/modules/user'

export default {
  props: {
    id: Number
  },
  data() {
    return {
      stuData: [],
      PageClassId: '',
      file: null
    }
  },
  created() {
    const userStore = useUserStore()
    this.PageClassId = userStore.getClassID
    this.getstuData()
  },
  methods: {
    async getstuData() {
      let url = 'http://47.120.78.42:8090/zjgsEmb/TeacherManageStu/getStuByClass'
      const res = await axios.get(url, {
        params: {
          classId: this.PageClassId
        }
      })
      console.log(res.data)
      this.stuData = res.data.data
      console.log(this.stuData)
      console.log('学生管理界面，班级的id ' + this.PageClassId)
    },
    async uploadMehod(params) {
      const file = params.file
      const formData = new FormData()
      // 添加其他参数到FormData中
      formData.append('classId', this.PageClassId)
      formData.append('classType', 2)
      // 将上传的文件添加到FormData中
      formData.append('file', file)
      try {
        let url = 'http://47.120.78.42:8090/zjgsEmb/TeacherManageStu/ImportStu'
        const res = await axios.post(url, formData, {
          headers: { 'Content-Type': 'multipart/form-data' }, //定义内容格式,很重要
          timeout: 40000
        })
        console.log(res.data)
      } catch (error) {
        console.error('Error handling preview:', error)
      }
    },
    JumpToEditStu() {
      this.$router.push({
        path: '/Dashboard/AddStudent',
        query: {
          fun: 'init'
        }
      })
    },
    EditStudent(row) {
      this.$router.push({
        path: '/Dashboard/AddStudent',
        query: {
          stuId: row.stuId,
          fun: 'edit'
        }
      })
    }
  }
}
</script>

<template>
  <div>
    <!-- 上边界 -->
    <el-divider class="divider" direction="horizontal" content-position="left" />
    <!-- 你接下来要展示的内容 -->
    <!--    使用el-table来展示内容-->
    <div align="middle">
      <el-table :data="stuData" stripe border stye="width: 100%">
        <el-table-column fixed="left" prop="teachingClassName" label="教学班级名称" width="240" />
        <el-table-column prop="className" label="行政班级名称" width="240" />
        <el-table-column prop="stuName" label="学生名称" width="240" />
        <el-table-column prop="teachingClassName" label="教学班级" width="240" />
        <el-table-column prop="stuId" label="学号" width="240" />
        <el-table-column prop="groupName" label="小组编号" sortable width="240" />
        <el-table-column fixed="right" label="Operations" width="240">
          <template #default="{ row }">
            <!--            这个方法要改一下 跳转到编辑学生页面 todo-->
            <el-button link type="primary" size="small" @click="EditStudent(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div id="button_lay" style="margin: 10px">
      <el-row gutter="4" class="row-bg" justify="space-around">
        <el-col :span="6" />
        <el-col :span="6" />
        <el-col :span="6"
          ><el-upload
            v-model:file-list="fileList"
            class="upload-demo"
            action="#"
            multiple
            :http-request="uploadMehod"
            :on-change="handlePreview"
            :on-success="handlePreview"
            :on-remove="handleRemove"
            :before-remove="beforeRemove"
            :limit="1"
            :on-exceed="handleExceed"
          >
            <el-button type="primary">上传excel导入学生</el-button>
            <template #tip>
              <div class="el-upload__tip"> excel files with a size less than 500KB. </div>
            </template>
          </el-upload>
        </el-col>
      </el-row>
    </div>
    <div id="添加学生" style="margin-top: 10px">
      <!--      todo-->
      <el-button type="success" @click="JumpToEditStu">点击手动向班级添加学生</el-button>
    </div>
    <!-- 下边界 -->
    <el-divider class="divider" direction="horizontal" content-position="right" />
  </div>
</template>

<style scoped lang="less"></style>
