<script lang="js">
import { useUserStore } from '@/store/modules/user'
import { ElMessage } from 'element-plus'
import { getStudentsByClass } from '@/api/equip/students'
import { importStudents } from '@/api/equip/imports'

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
      this.stuData = await getStudentsByClass(this.PageClassId)
    },
    async uploadMehod(params) {
      try {
        const res = await importStudents(this.PageClassId, params.file)
        ElMessage.success('导入学生成功')
        params.onSuccess?.(res)
        this.getstuData()
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
