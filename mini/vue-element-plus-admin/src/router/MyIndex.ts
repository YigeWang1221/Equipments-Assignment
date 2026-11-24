import { createRouter, createWebHashHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import type { App } from 'vue'
import { Layout } from '@/utils/routerHelper'
import { useI18n } from '@/hooks/web/useI18n'
import { NO_RESET_WHITE_LIST } from '@/constants'
import { constantRouterMap } from '@/router/index'

const { t } = useI18n()

//我自己设计的router
//这个是基础的，不需要permission来判断
export const MyconstRouterMap: AppRouteRecordRaw[] = [
  {
    // 这个应该是登陆之后的第一个界面
    path: '/',
    component: Layout,
    redirect: 'ShowList',
    name: 'showGreatHomeWork',
    meta: {
      hidden: true
    }
  },
  {
    //这个我不知道，但是也没有逐级的页面来代替他
    path: '/redirect',
    component: Layout,
    name: 'Redirect',
    children: [
      {
        path: '/redirect/:path(.*)',
        name: 'RedirectPage',
        component: () => import('@/views/Redirect/Redirect.vue'),
        meta: {}
      }
    ],
    meta: {
      hidden: true,
      noTagsView: true
    }
  },
  {
    // 使用原来的login的方式
    path: '/login',
    component: () => import('@/views/Login/Login.vue'),
    name: 'Login',
    meta: {
      hidden: true,
      title: t('router.login'),
      noTagsView: true
    }
  },
  {
    path: '/personal',
    component: Layout,
    redirect: '/personal/personal-center',
    name: 'Personal',
    meta: {
      title: t('router.personal'),
      hidden: true,
      canTo: true
    },
    children: [
      {
        path: 'personal-center',
        component: () => import('@/views/Personal/PersonalCenter/PersonalCenter.vue'),
        name: 'PersonalCenter',
        meta: {
          title: t('router.personalCenter'),
          hidden: true,
          canTo: true
        }
      }
    ]
  },
  {
    path: '/404',
    component: () => import('@/views/Error/404.vue'),
    name: 'NoFind',
    meta: {
      hidden: true,
      title: '404',
      noTagsView: true
    }
  }
]
///////////////////////////////////////////////////////////////////////////////////////
// 我自己的全部router判断
// 我这里不再使用双语的形式，所以没有title中没有t来转换
export const MyasyncRouterMap: AppRouteRecordRaw[] = [
  {
    // 第一层是优秀作业展示
    path: '/GreatWork',
    component: Layout,
    redirect: '/GreatWork/GreatWorkList',
    name: 'ShowGreatWork',
    meta: {
      title: '优秀作业展示',
      icon: 'ant-design:dashboard-filled',
      alwaysShow: true
    },
    children: [
      //两个子路由，一个展示板，一个是列表
      {
        path: 'GreatWorkList',
        component: () => import('@/views/Showboard/ShowList.vue'),
        name: 'GreatWorkList',
        meta: {
          title: '优秀作业列表',
          noCache: true,
          affix: true
        }
      },
      {
        // 固定在侧边栏，然后启用缓存
        path: 'GreatWorkBoard',
        component: () => import('@/views/Showboard/ShowBoard.vue'),
        name: 'GreatWorkBoard',
        meta: {
          title: '优秀作业展示板',
          noCache: false,
          affix: true
        }
      }
    ]
  },
  {
    //第二层，这里放器材管理的界面,没有找到图标的导入方法，后面不再使用图标了
    path: '/Equipment',
    component: Layout,
    redirect: '/Equipment/EquipmentList',
    name: 'Equipment',
    meta: {
      title: '教学器材',
      alwaysShow: true
    },
    children: [
      {
        path: 'EquipmentList',
        component: () => import('@/views/Equipmets/EquipmentBorrowList.vue'),
        name: 'EquipmentLists',
        meta: {
          title: '器材列表',
          //为了展示的更新，不使用缓存
          noCache: true,
          affix: true
        }
      },
      {
        path: 'EquipmentBorrowList',
        component: () => import('@/views/Equipmets/EquipmentBorrowList.vue'),
        name: 'EquipmentBorrowList',
        meta: {
          title: '教师器材借用归还列表',
          noCache: true,
          affix: true
        }
      }
    ]
  },
  {
    // 第三层，教师相关的班级管理
    path: '/ClassInfoManagement',
    component: Layout,
    redirect: '/ClassInfoManagement/IOTclassManagement',
    name: 'ClassInfoManagement',
    meta: {
      title: '学生和班级管理',
      alwaysShow: true
    },
    children: [
      {
        //第一层是IOT班级管理
        path: 'IOTclassManagement',
        component: () => import('@/views/TeacherClassNG/IOTclassList.vue'),
        name: 'IOTclass',
        meta: {
          title: '物联网课程管理',
          noCache: true,
          affix: true
        }
      },
      {
        //第一层是IOT班级管理
        path: 'ARMclassManagement',
        component: () => import('@/views/TeacherClassNG/ARMclassList.vue'),
        name: 'ARMclass',
        meta: {
          title: '嵌入式课程管理',
          noCache: true,
          affix: true
        }
      },
      {
        //第三层是综合管理，包含作业
        path: 'InteManagement',
        component: () => import('@/views/TeacherClassNG/TeacherManage.vue'),
        name: 'InteManagement',
        meta: {
          title: '教师综合管理界面',
          noCache: true,
          affix: true
        }
      }
    ]
  },
  {
    //这里少了一个界板块，学生的作业写作和作业上传
    path: '/StudentHomework',
    component: Layout,
    redirect: 'StudentHomework/StudentHomework',
    name: 'homework',
    meta: {
      title: '个人作业',
      alwaysShow: true,
      affix: true
    },
    children: [
      {
        path: 'StudentHomeworkList',
        component: () => import('@/views/WorkStu/HomeworkLists.vue'),
        name: 'HomeWorkList',
        meta: {
          title: '作业列表',
          noCache: true,
          affix: true
        }
      },
      {
        path: 'StudentHomeworkBoard',
        component: () => import('@/views/WorkStu/WorkingBoard.vue'),
        name: 'HomeWorkBoard',
        meta: {
          title: '工作台',
          affix: true,
          noCache: true
        }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  strict: true,
  routes: constantRouterMap as RouteRecordRaw[],
  scrollBehavior: () => ({ left: 0, top: 0 })
})

export const resetRouter = (): void => {
  router.getRoutes().forEach((route) => {
    const { name } = route
    if (name && !NO_RESET_WHITE_LIST.includes(name as string)) {
      router.hasRoute(name) && router.removeRoute(name)
    }
  })
}

export const setupRouter = (app: App<Element>) => {
  app.use(router)
}

export default router
