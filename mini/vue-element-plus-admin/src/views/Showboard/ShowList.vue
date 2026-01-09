<script>
// 这里应当使用两个dropdown 下拉菜单的按钮来选择年份和课程类型,再加上一个确认查询的按钮
// 下面是单拉出来的一个展示列表的框，点击框内的项目就兴建一个界面，title是展示文件的名称，通过叉掉上边框的方式结束观看
// 如果没有值上传的化默认就是000-all
import { getyearsList, getGreatWorkList } from '@/apiX/showWorks'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
export default {
  props: {
    itemList: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      yearList: []
    }
  },
  created() {
    for (let year = 2020; year <= 2099; year++) {
      this.yearList.push(year)
    }
  }
}
////////////////////////////////////////////
</script>

<template>
  <div style="margin: 4px">
    <div class="Top-container">
      <!-- 年份下拉菜单按钮 -->
      <el-dropdown size="default" trigger="hover">
        <span class="el-dropdown-link">
          年份 <el-icon class="el-icon--right"> <arrow-down /></el-icon>
        </span>
        <el-dropdown-menu v-slot="_">
          <el-dropdown-item v-for="year in yearList" :key="year" :value="year">{{
            year
          }}</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>

      <!-- 课程下拉菜单按钮 -->
      <el-dropdown size="medium" trigger="hover">
        <span class="el-dropdown-link">
          课程<i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <el-dropdown-menu v-slot="_">
          <el-dropdown-item>物联网课程</el-dropdown-item>
          <el-dropdown-item>嵌入式课程</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>

      <!--      这里放最左侧的search按钮-->、
      <el-button class="search-button" @click="searchList" icon="el-icon-search">查询</el-button>
      <!--容器      -->
    </div>
    <!-- Item-list 容器 -->
    <div class="Item-list" style="height: calc(100vh - 30px)">
      <el-scrollbar wrap-class="scrollbar-wrapper">
        <el-card
          v-for="item in itemLists"
          :key="item.id"
          class="item-card"
          shadow="hover"
          @click="goToShowBoard(item)"
        >
          <img :src="item.imageUrl" alt="item image" class="item-image" />
          <div class="item-content">{{ item.content }}</div>
        </el-card>
      </el-scrollbar>
    </div>
  </div>
</template>

<style scoped lang="less">
.Top-container {
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
  align-items: flex-start;
  height: 30px;
}

.el-dropdown {
  margin-bottom: 20px;
}
.search-button {
  margin-right: 10px;
  color: #2c3e50; /* 设置按钮图标的颜色为深蓝色 */
}
.Item-list {
  padding: 0 10px;
  margin-top: 10px;
  overflow: hidden; /* 隐藏滚动条 */
}

.scrollbar-wrapper {
  height: 100%;
}

.item-card {
  margin-bottom: 4px;
  position: relative;
}

.item-card img.item-image {
  position: absolute; /* 设置绝对定位 */
  top: 50%; /* 图像顶部与父元素顶部的距离为50% */
  transform: translateY(-50%); /* 使用transform将图像上移50%，实现垂直居中 */
  left: 2px; /* 图像左边与父元素左边的距离为2px */
  width: 40px; /* 图像的宽度 */
  height: 40px; /* 图像的高度 */
  border-radius: 4px; /* 圆角边框 */
}

.item-card:hover {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.item-content {
  padding: 10px;
  margin-left: 50px; /* 文字内容距离图像的距离 */
}
</style>
