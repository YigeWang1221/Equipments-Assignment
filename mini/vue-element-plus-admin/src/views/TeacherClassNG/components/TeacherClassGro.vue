<script lang="js">
import async from 'async'
import axios from 'axios'
import { useUserStore } from '@/store/modules/user'
export default {
  data() {
    return {
      GroupData: [],
      classIdX: ''
    }
  },
  created() {
    const userStore = useUserStore()
    this.classIdX = userStore.getClassID
    this.getgroupData()
  },
  methods: {
    async getgroupData() {
      let url = 'http://47.120.78.42:8090/zjgsEmb/TeacherImGro/SearchGro'
      const res = await axios.get(url, {
        params: {
          classId: this.classIdX
        }
      })
      console.log(res.data)
      this.GroupData = res.data.data
      console.log(this.GroupData)
    },
    async uploadMehod(params) {
      const file = params.file
      const formData = new FormData()
      // 添加其他参数到FormData中
      formData.append('classId', this.classIdX)
      // 将上传的文件添加到FormData中
      formData.append('file', file)
      try {
        let url = 'http://47.120.78.42:8090/zjgsEmb/TeacherImGro/ImportGro?classId'
        const res = await axios.post(url, formData, {
          headers: { 'Content-Type': 'multipart/form-data' }, //定义内容格式,很重要
          timeout: 40000
        })
        console.log(res.data)
      } catch (error) {
        console.error('Error handling preview:', error)
      }
    },
    ToEditGroup(row) {
      this.$router.push({
        path: '/Dashboard/AddGroup',
        query: {
          fun: 'edit',
          groupId: row.id
        }
      })
    },
    ToAddGroup() {
      this.$router.push({
        path: '/Dashboard/AddGroup',
        query: {
          fun: 'add'
        }
      })
    },
    async DeleteUploadMehod() {
      let url = 'hhttp://47.120.78.42:8090/zjgsEmb/TeacherImGro/deleteAllByClassId'
      const res = await axios.get(url, {
        param: {
          classId: this.classIdX
        }
      })
    }
  }
}
</script>

<script lang="js"></script>

<template>
  <div>
    <!-- 上边界 -->
    <el-divider class="divider" direction="horizontal" content-position="left" />

    <!-- 你接下来要展示的内容 -->
    <!--    使用el-table来展示内容-->
    <div align="middle">
      <el-table :data="GroupData" border stripe stye="width: 100%">
        <el-table-column fixed="left" prop="className" label="班级名称" width="240" />
        <el-table-column prop="groupName" label="小组名称" width="240" />
        <el-table-column prop="groupNum" label="小组人员数量" width="240" />
        <!--        <el-table-column prop="StuName" label="学生名称" width="240" />-->
        <!--        <el-table-column prop="StuId" label="学号" width="240" />-->
        <el-table-column prop="teachingClassName" label="教学班级" width="240" />
        <!--        <el-table-column fixed="right" label="Operations" width="240">-->
        <!--          <template #default>-->
        <!--            &lt;!&ndash;            这个方法要改一下&ndash;&gt;-->
        <!--            &lt;!&ndash;            <el-button link type="primary" size="small" @click="ToEditGroup(row)">编辑</el-button>&ndash;&gt;-->
        <!--          </template>-->
        <!--        </el-table-column>-->
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
            :on-preview="handlePreview"
            :on-remove="handleRemove"
            :before-remove="beforeRemove"
            :limit="3"
            :on-exceed="handleExceed"
          >
            <el-button type="primary">上传excel导入小组</el-button>
            <template #tip>
              <div class="el-upload__tip"> excel files with a size less than 500KB. </div>
            </template>
          </el-upload>
        </el-col>
      </el-row>
    </div>
    <!-- 下边界 -->
    <el-divider class="divider" direction="horizontal" content-position="right" />

    <el-button type="warning" @click="DeleteUploadMehod"
      >删除所有小组上传导入新的小组
      <template #tip>
        <div class="el-upload__tip"> excel files with a size less than 500KB. </div>
      </template>
    </el-button>
  </div>
</template>

<style scoped lang="less">
.divider {
  border-color: #87cefa; /* 浅蓝色边界线 */
  margin-top: 4px; /* 距离上边界 4px */
  margin-bottom: 4px; /* 距离下边界 4px */
}
</style>
