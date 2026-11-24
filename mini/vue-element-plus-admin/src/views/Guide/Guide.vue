<!--<script setup lang="ts">-->
<!--import { ContentWrap } from '@/components/ContentWrap'-->
<!--import { useI18n } from '@/hooks/web/useI18n'-->
<!--import { useGuide } from '@/hooks/web/useGuide'-->

<!--const { t } = useI18n()-->

<!--const { drive } = useGuide()-->

<!--const guideStart = () => {-->
<!--  drive()-->
<!--}-->
<!--</script>-->

<!--<template>-->
<!--  <ContentWrap :title="t('guideDemo.guide')" :message="t('guideDemo.message')">-->
<!--    <BaseButton type="primary" @click="guideStart">{{ t('guideDemo.start') }}</BaseButton>-->
<!--  </ContentWrap>-->
<!--</template>-->
<script lang="js">
// 这个界面是用来展示所有的器材的，最后一张卡片式（+） 只有教师可以添加新的器材
// 点击器材的时候需要根据permission来判断是进入借用详情还是 修改详情（就是下一个界面的可修改和可见改成false）
// 需要展示的是 图片，总数和剩余数量
// 回去看一下逻辑是个人借用还是小组借用
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import { getEquipments } from '@/api/equip/equipments'

export default {
  data() {
    return {
      equipments: [],
      role: ''
    }
  },
  created() {
    // todo
    const userStore = useUserStore()
    this.role = userStore.getRole
    console.log(this.role)
    // this.role = 'teacher'
    this.getEquipList()
  },
  methods: {
    async getEquipList() {
      this.equipments = await getEquipments()
    },
    ToAddEquipment() {
      console.log('跳转到编辑界面')
      this.$router.push({
        path: '/guide/EmbEdit',
        query: {
          fun: 'add'
        }
      })
    },
    downLoadZip(equipment) {
      if (equipment.infoUrl == null) {
        ElMessage.warning('当前器材没有对应资源可供下载')
        return
      }
      const link = document.createElement('a')
      link.href = equipment.infoUrl
      link.setAttribute('download', equipment.name)
      link.click()
    },
    editEquipment(equipment) {
      this.$router.push({
        path: '/guide/EmbEdit',
        query: {
          fun: 'edit',
          EquipId: equipment.id
        }
      })
    },
    borrowEquipment(equipment) {
      //todo 跳转到新的界面
      this.$router.push({
        path: '/guide/BorrowApplying',
        query: {
          EquipmentId: equipment.id
        }
      })
    }
  }
}
</script>

<template>
  <div align="middle">
    <el-text size="large" type="warning">可用器材展示</el-text>
  </div>
  <el-divider class="color-block" />
  <div class="flex flex-wrap gap-4">
    <!--    这里展示所有的器材 使用v-for-->
    <!--    这里添加一个v-if 保证只有教师能够新增 todo-->
    <div v-for="(equipment, index) in equipments" :key="index">
      <el-card style="width: 200px; height: 400px">
        <template #header>{{ equipment.name }}</template>
        <!--        如果为空就显示本地的-->
        <el-image
          :src="equipment.photoUrl ? equipment.photoUrl : '/src/assets/imgs/NoPhoto.jpg'"
          style="max-width: 200px; max-height: 280px; object-fit: contain"
        />
        <template #footer>
          <el-text type="danger">现有数量：{{ equipment.availableNumber }}</el-text>
          <el-button v-if="equipment.infoUrl" link type="success" @click="downLoadZip(equipment)"
            >下载配套资源</el-button
          >
          <div>
            <el-button
              v-if="role === 'Student'"
              link
              type="primary"
              @click="borrowEquipment(equipment)"
              >借用器材</el-button
            >
          </div>
          <el-button v-if="role === 'Teacher'" type="warning" @click="editEquipment(equipment)"
            >编辑器材</el-button
          >
        </template>
      </el-card>
    </div>
    <el-card style="max-width: 200px" shadow="hover" v-if="role === 'Teacher'">
      <template #header>新增器材</template>
      <el-image src="/src/assets/imgs/NoPhoto.jpg" style="max-width: 100%; max-height: 280px" />
      <template #footer>
        <el-button link type="warning" @click="ToAddEquipment">点击增加</el-button>
      </template>
    </el-card>
  </div>
</template>

<style scoped lang="less"></style>
