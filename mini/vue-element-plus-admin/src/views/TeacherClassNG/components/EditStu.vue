<script setup>
import { ref } from 'vue'

const form = ref({
  name: '',
  region: '',
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
</script>

<template>
  <div style="margin-top: 10px"> 学生管理编辑界面 </div>
  <el-form :model="form" label-width="auto" style="max-width: 600px" class="form">
    <el-form-item label="学生姓名">
      <el-input v-model="form.name" />
    </el-form-item>
    <el-form-item label="学号">
      <el-input v-model="form.uid" placeholder="2012190230" />
    </el-form-item>
    <el-form-item label="当前选中班级">
      <el-input v-model="form.className" placeholder="嵌入式2024" />
    </el-form-item>
    <!--    这里需要多一个输入框，输入的内容是行政班级 使用上传到StaticClass元素名下 新建的接口是addStu-->
    <el-form-item label="在此班级中的小组">
      <!--      小组的编号上传的时候如果不存在就上传-1（在更新和新建的时候），限制为上传数字编号-->
      <el-input v-model="form.GroupName" placeholder="23" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="onSubmit">更新修改</el-button>
      <el-button @click="deleteStu">删除学生</el-button>
      <el-button @click="initStu">新建学生</el-button>
    </el-form-item>
  </el-form>
</template>

<style>
.form {
  margin-top: 20px;
}
</style>

<style scoped lang="less"></style>
