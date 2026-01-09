<script lang="js">
//子组件，主要是一个el-list 是这个班级过去的所有作业和情况的list
//这个界面有问题，需要重新规划
</script>

<template>
  <div>
    <!-- 上边界 -->
    <el-divider class="divider" direction="horizontal" content-position="left" />

    <!-- 你接下来要展示的内容 -->
    <!--    使用el-table来展示内容   第一个是已经布置的作业列表-->
    <div align="middle"> 班级作业列表</div>
    <div align="middle">
      <el-table :data="GroupData" border stripe stye="width: 100%">
        <el-table-column fixed="left" prop="className" label="班级名称" width="240" />
        <el-table-column prop="GroupName" label="小组名称" width="240" />
        <el-table-column prop="number" label="小组人员数量" width="240" />
        <el-table-column prop="StuName" label="学生名称" width="240" />
        <el-table-column prop="StuId" label="学号" width="240" />
        <el-table-column prop="TeachingClass" label="教学班级" width="240" />
        <el-table-column fixed="right" label="Operations" width="240">
          <template #default>
            <!--            这个方法要改一下-->
            <el-button link type="primary" size="small" @click="handleClick">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div style="margin-top: 10px" id="button_lay">
      <!--      todo 这里写一个点击新建作业的-->
      <el-button class="initWorkButton" type="primary" align="middle" @click="initNewHomeWork"
        >点击新建作业</el-button
      >
    </div>
    <!-- 下边界 -->
    <el-divider class="divider" direction="horizontal" content-position="right" />
  </div>
  <div>
    <!-- 上边界 -->
    <el-divider class="divider" direction="horizontal" content-position="left" />

    <!-- 你接下来要展示的内容 -->
    <!--    使用el-table来展示内容   第一个是已经布置的作业列表-->
    <div align="middle"> 本次作业已提交的列表</div>
    <div align="middle">
      <el-table :data="GroupData" border stripe stye="width: 100%" title="班级作业列表">
        <el-table-column fixed="left" prop="className" label="班级名称" width="240" />
        <el-table-column prop="GroupName" label="小组名称" width="240" />
        <el-table-column prop="number" label="小组人员数量" width="240" />
        <el-table-column prop="StuName" label="学生名称" width="240" />
        <el-table-column prop="StuId" label="学号" width="240" />
        <el-table-column prop="TeachingClass" label="教学班级" width="240" />
        <el-table-column fixed="right" label="Operations" width="240">
          <template #default>
            <!--            这个方法要改一下 todo 批改作业-->
            <el-button link type="primary" size="small" @click="CorrectHomeWork"
              >点击批改作业</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!-- 下边界 -->
    <el-divider class="divider" direction="horizontal" content-position="right" />
  </div>
  <div style="margin-top: 10px">
    <el-button type="primary" align="middle" @click="downLoadHomework"
      >点击下载作业压缩包</el-button
    >
  </div>
</template>

<style>
.divider {
  border-color: #87cefa; /* 浅蓝色边界线 */
  margin-top: 4px; /* 距离上边界 4px */
  margin-bottom: 4px; /* 距离下边界 4px */
}
.initWorkButton {
  margin-right: 10px;
}
</style>
