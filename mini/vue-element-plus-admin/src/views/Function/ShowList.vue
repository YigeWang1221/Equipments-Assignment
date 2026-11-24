<script>
// 这里应当使用两个dropdown 下拉菜单的按钮来选择年份和课程类型,再加上一个确认查询的按钮
// 下面是单拉出来的一个展示列表的框，点击框内的项目就兴建一个界面，title是展示文件的名称，通过叉掉上边框的方式结束观看
// 如果没有值上传的化默认就是000-all
import { ArrowDown } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'
import { getGreatWorks } from '@/api/equip/greatWorks'
export default {
  data() {
    return {
      yearList: [],
      greatWorkList: [],
      selectedYear: 0,
      selectedClass: -1,
      selectedClassName: '',
      Role: ''
    }
  },
  created() {
    const usrStore = useUserStore()
    this.Role = usrStore.getRole
    for (let year = 2024; year <= 2035; year++) {
      this.yearList.push(year)
    }
    this.getGreatWorkByYearAndType()
    console.log(this.greatWorkList)
  },
  methods: {
    ToGreatWork(greatwork) {
      console.log(greatwork.photo)
      this.$router.push({
        path: '/function/ShowItem',
        query: {
          greatworkId: greatwork.id
        }
      })
    },
    updateShowWork(greatwork) {
      this.$router.push({
        path: '/function/ShowItem',
        query: {
          greatworkId: greatwork.id,
          fun: 'update'
        }
      })
    },
    async getGreatWorkByYearAndType() {
      const params = {}
      if (this.selectedYear && this.selectedYear !== 0) {
        params.year = this.selectedYear
      }
      if (this.selectedClass > 0) {
        params.classType = this.selectedClass
      }
      this.greatWorkList = await getGreatWorks(params)
    },
    setClassType1() {
      this.selectedClass = 2
      this.selectedClassName = '物联网课程'
    },
    setClassType2() {
      this.selectedClass = 1
      this.selectedClassName = '嵌入式课程'
    },
    setYear(year) {
      this.selectedYear = year
    },
    setYear0() {
      this.selectedYear = 0
    },
    setClassAll() {
      this.selectedClass = -1
      this.selectedClassName = '查询全部班级'
    }
  }
}
////////////////////////////////////////////
</script>

<template>
  <el-row :gutter="18">
    <el-col :span="6"><div class="grid-content ep-bg-purple"></div></el-col>
    <el-col :span="6"
      ><el-dropdown>
        <el-button type="primary">
          年份选择<el-icon class="el-icon--right"><arrow-down /></el-icon>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="setYear0()">all</el-dropdown-item>
            <el-divider />
            <el-dropdown-item v-for="year in yearList" :key="year" @click="setYear(year)">{{
              year
            }}</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      <div>{{ selectedYear }}</div></el-col
    >
    <el-col :span="6"
      ><el-dropdown size="medium" trigger="hover">
        <el-button type="primary">
          课程选择<el-icon class="el-icon--right"><arrow-down /></el-icon>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="setClassType1">物联网课程</el-dropdown-item>
            <el-dropdown-item @click="setClassType2">嵌入式课程</el-dropdown-item>
            <el-divider />
            <el-dropdown-item @click="setClassAll">全部</el-dropdown-item>
          </el-dropdown-menu>
        </template> </el-dropdown
      ><div>{{ selectedClassName }}</div></el-col
    >
    <el-col :span="6"
      ><el-button class="search-button" @click="getGreatWorkByYearAndType" icon="el-icon-search"
        >查询</el-button
      ></el-col
    >
  </el-row>
  <el-divider />
  <div class="flex flex-wrap gap-4">
    <div v-for="(greatwork, index) in greatWorkList" :key="index">
      <el-card style="max-width: 280px; height: 280px">
        <template #header
          ><div @click="ToGreatWork(greatwork)">{{ greatwork.workName }}</div>
          <el-button
            v-if="Role === 'Teacher'"
            @click="updateShowWork(greatwork)"
            link
            type="primary"
            >编辑</el-button
          >
        </template>
        <el-image
          @click="ToGreatWork(greatwork)"
          :src="greatwork.photoUrl ? greatwork.photoUrl : '/src/assets/imgs/NoPhoto.jpg'"
          style="max-width: 100%; max-height: 160px; object-fit: contain"
          :key="fit"
        />
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.example-showcase .el-dropdown + .el-dropdown {
  margin-left: 15px;
}
.example-showcase .el-dropdown-link {
  cursor: pointer;
  color: var(--el-color-primary);
  display: flex;
  align-items: center;
}
</style>
