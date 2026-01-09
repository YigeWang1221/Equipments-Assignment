<script>
//这个是教师批改作业的界面
// 70%是vditor，展示学生的作业
// 或者, 先请求作业的全部内容，判断有没有视频url 如果有的话，提供一个300px高度的视频播放页面（从展示界面引入）
// 20是作业的评分空间，可以提出分数/下载作业附件
// 10是单个的内容，提供下一个的按钮功能（这个要想一下怎么实现）
// todo vditor的展示很有问题，需要统一修一下
import { ref } from 'vue'
import Vditor from 'vditor' // 确保引入正确的 Vditor 路径

export default {
  setup() {
    const contentEditor = ref(null)

    return {
      contentEditor
    }
  },
  mounted() {
    this.contentEditor.value = new Vditor('vditor', {
      height: 360,
      toolbarConfig: {
        pin: true
      },
      toolbar: false,
      cache: {
        enable: false
      },
      after: () => {
        this.contentEditor.value.setValue('hello,Vditor+Vue!')
      }
    })
  },
  methods: {
    uploadScore() {},
    downLoadZip() {},
    Next() {}
  }
}
</script>

<template>
  <!--  &lt;!&ndash;  这里放一个vditor查看学生提交的内容&ndash;&gt;-->
  <!--  &lt;!&ndash;  <div id="vditor" name="description"></div>&ndash;&gt;-->
  <div id="scores" style="margin-top: 10px">
    <el-input v-model="score" placeholder="100" style="width: 200px" />
    <el-button type="warning" @click="uploadScore" style="margin-left: 10px">提交分数</el-button>
    <el-button type="info" @click="downLoadZip" style="margin-left: 10px">下载压缩包</el-button>
    <el-button type="success" @click="Next" style="margin-left: 10px">批阅下一份</el-button>
  </div>
  <el-divider />
  <div style="margin-top: 10px" align="middle">
    <!--    这个用来放超出允许查重率，相似度高的文章-->
    <!--    这里少一个data= 需要后续跟进-->
    <el-table stripe border>
      <el-table-column prop="className" label="班级名称" width="240" />
      <el-table-column prop="homeWorkName" label="作业名称" width="240" />
      <el-table-column prop="groupName" label="小组名称" width="240" />
      <el-table-column prop="StuNames" label="组员名称" width="240" />
      <el-table-column label="check" width="240">
        <template #default="row">
          <el-button link type="warning" @click="checkHomeWork(row)" size="default">link</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
  <!--  <div>为什么什么都没有</div>-->
</template>

<style scoped lang="less"></style>
