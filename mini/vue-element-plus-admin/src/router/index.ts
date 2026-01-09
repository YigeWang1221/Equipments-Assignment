import { createRouter, createWebHashHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import type { App } from 'vue'
import { Layout, getParentLayout } from '@/utils/routerHelper'
import { useI18n } from '@/hooks/web/useI18n'
import { NO_RESET_WHITE_LIST } from '@/constants'

const { t } = useI18n()

// 原有的router
export const constantRouterMap: AppRouteRecordRaw[] = [
  {
    // 第一层是优秀作业展示
    path: '/',
    component: Layout,
    redirect: '/function/showListBoard',
    name: 'ShowGreatWork',
    meta: {
      title: '优秀作业展示',
      icon: 'ant-design:dashboard-filled',
      hidden: true
    },
    children: [
      //两个子路由，一个展示板，一个是列表
      {
        path: 'showListBoard',
        component: () => import('@/views/Function/ShowList.vue'),
        name: 'ShowList',
        meta: {
          title: '优秀作业列表'
        }
      },
      {
        // 固定在侧边栏，然后启用缓存
        path: 'ShowItem',
        component: () => import('@/views/Function/ShowItem.vue'),
        name: 'ShowItem',
        meta: {
          title: '优秀作业展示板',
          hidden: true
        }
      }
    ]
  },
  {
    path: '/redirect',
    component: Layout,
    name: 'Redirect',
    children: [
      {
        path: '/redirect/:path(.*)',
        name: 'Redirect',
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

export const asyncRouterMap: AppRouteRecordRaw[] = [
  {
    // 第三层，教师相关的班级管理
    path: '/ClassInfoManagement',
    component: Layout,
    redirect: '/ClassInfoManagement/IOTclassManagement',
    name: 'ClassInfoManagement',
    meta: {
      title: '学生和班级管理'
    },
    children: [
      {
        //第一层是IOT班级管理
        path: 'IOTclassManagement',
        component: () => import('@/views/TeacherClassNG/IOTclassList.vue'),
        name: 'IOTclass',
        meta: {
          title: '物联网课程管理'
        }
      },
      {
        //第一层是IOT班级管理
        path: 'ARMclassManagement',
        component: () => import('@/views/TeacherClassNG/ARMclassList.vue'),
        name: 'ARMclass',
        meta: {
          title: '嵌入式课程管理'
        }
      },
      {
        //第三层是综合管理，包含作业
        path: 'InteManagement',
        component: () => import('@/views/TeacherClassNG/TeacherManage.vue'),
        name: 'InteManagement',
        meta: {
          title: '教师综合管理界面'
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
      title: '个人作业'
    },
    children: [
      {
        path: 'StudentHomeworkList',
        component: () => import('@/views/WorkStu/HomeworkLists.vue'),
        name: 'HomeWorkList',
        meta: {}
      },
      {
        path: 'StudentHomeworkBoard',
        component: () => import('@/views/WorkStu/WorkingBoard.vue'),
        name: 'HomeWorkBoard',
        meta: {
          title: '工作台'
        }
      }
    ]
  },
  {
    path: '/dashboard',
    component: Layout,
    redirect: '/dashboard/analysis',
    name: 'Dashboard',
    meta: {
      title: t('router.dashboard'),
      icon: 'ant-design:dashboard-filled'
    },
    children: [
      {
        path: 'analysis',
        component: () => import('@/views/Dashboard/Analysis.vue'),
        name: 'Analysis',
        meta: {
          title: t('router.analysis')
        }
      },
      {
        path: 'IOTclasses',
        component: () => import('@/views/Dashboard/IOTclassList.vue'),
        name: 'IOTclasses',
        meta: {
          title: '物联网班级列表'
        }
      },
      {
        path: 'ARMclasses',
        component: () => import('@/views/Dashboard/ARMclassList.vue'),
        name: 'ARMclasses',
        meta: {
          title: '嵌入式班级列表'
        }
      },
      {
        path: 'initHomeWork',
        component: () => import('@/views/Dashboard/TeacherInitWork.vue'),
        name: 'initHomeWork',
        meta: {
          title: '新建作业'
        }
      },
      {
        path: 'TeacherCorrectHomework',
        component: () => import('@/views/Dashboard/TeacherCorrectHomework.vue'),
        name: 'TeacherCorrectHomework',
        meta: {
          title: '批改作业'
        }
      },
      {
        path: 'AddStudent',
        component: () => import('@/views/Dashboard/AddStudent.vue'),
        name: 'AddStudent',
        meta: {
          title: '学生添加界面'
        }
      },
      {
        path: 'AddGroup',
        component: () => import('@/views/Dashboard/AddGroup.vue'),
        name: 'AddGroup',
        meta: {
          title: '小组添加界面'
        }
      }
    ]
  },
  // {
  //   path: 'Student',
  //   component: Layout,
  //   redirect: '/Student/StuworkList',
  //   name: 'student',
  //   meta: {
  //     title: '学生功能'
  //   },
  //   children: [
  //     {
  //       path: 'StuworkList',
  //       component: () => import('@/views/WorkStu/HomeworkLists.vue'),
  //       name: 'workList',
  //       meta: {
  //         title: '学生作业列表',
  //         noCache: true,
  //         affix: true
  //       }
  //     }
  //   ]
  // },
  {
    path: '/external-link',
    component: Layout,
    meta: {
      hidden: true
    },
    name: 'ExternalLink',
    children: [
      {
        path: 'https://gitee.com/admin-mini/element-plus-admin-mini',
        name: 'DocumentLink',
        meta: {
          hidden: true,
          title: t('router.document'),
          icon: 'clarity:document-solid'
        }
      }
    ]
  },
  {
    path: '/guide',
    component: Layout,
    name: 'Guide',
    meta: {},
    children: [
      {
        path: 'index',
        component: () => import('@/views/Guide/Guide.vue'),
        name: 'GuideDemo',
        meta: {
          title: t('router.guide'),
          icon: 'cib:telegram-plane'
        }
      },
      {
        path: 'borrow',
        component: () => import('@/views/Guide/Borrow.vue'),
        name: 'BorrowDemo',
        meta: {
          title: t('学生借用器材表'),
          icon: 'cib:telegram-plane'
        }
      },
      {
        path: 'EmbEdit',
        component: () => import('@/views/Guide/EditEquipment.vue'),
        name: 'EditEquipmentDemo',
        redirect: 'noRedirect',
        meta: {
          hidden: true,
          title: t('器材管理'),
          icon: 'cib:telegram-plane'
        }
      },
      {
        path: 'StuBorrow',
        component: () => import('@/views/Guide/StuBorrow.vue'),
        name: 'StuBorrow',
        meta: {
          title: '我的借用情况'
        }
      }
    ]
  },
  {
    path: '/components',
    component: Layout,
    name: 'ComponentsDemo',
    meta: {
      title: t('router.component'),
      icon: 'bx:bxs-component',
      alwaysShow: true
    },
    children: [
      {
        path: 'form',
        component: getParentLayout(),
        redirect: '/components/form/default-form',
        name: 'Form',
        meta: {
          title: t('router.form'),
          alwaysShow: true
        },
        children: [
          {
            path: 'default-form',
            component: () => import('@/views/Components/Form/DefaultForm.vue'),
            name: 'DefaultForm',
            meta: {
              title: t('router.defaultForm')
            }
          },
          {
            path: 'use-form',
            component: () => import('@/views/Components/Form/UseFormDemo.vue'),
            name: 'UseForm',
            meta: {
              title: 'UseForm'
            }
          }
        ]
      },
      {
        path: 'table',
        component: getParentLayout(),
        redirect: '/components/table/default-table',
        name: 'TableDemo',
        meta: {
          title: t('router.table'),
          alwaysShow: true
        },
        children: [
          {
            path: 'default-table',
            component: () => import('@/views/Components/Table/DefaultTable.vue'),
            name: 'DefaultTable',
            meta: {
              title: t('router.defaultTable')
            }
          },
          {
            path: 'use-table',
            component: () => import('@/views/Components/Table/UseTableDemo.vue'),
            name: 'UseTable',
            meta: {
              title: 'UseTable'
            }
          },
          {
            path: 'tree-table',
            component: () => import('@/views/Components/Table/TreeTable.vue'),
            name: 'TreeTable',
            meta: {
              title: t('router.treeTable')
            }
          },
          {
            path: 'table-image-preview',
            component: () => import('@/views/Components/Table/TableImagePreview.vue'),
            name: 'TableImagePreview',
            meta: {
              title: t('router.PicturePreview')
            }
          },
          {
            path: 'table-video-preview',
            component: () => import('@/views/Components/Table/TableVideoPreview.vue'),
            name: 'TableVideoPreview',
            meta: {
              title: t('router.tableVideoPreview')
            }
          },
          {
            path: 'card-table',
            component: () => import('@/views/Components/Table/CardTable.vue'),
            name: 'CardTable',
            meta: {
              title: t('router.cardTable')
            }
          }
        ]
      },
      {
        path: 'editor-demo',
        component: getParentLayout(),
        redirect: '/components/editor-demo/editor',
        name: 'EditorDemo',
        meta: {
          title: t('router.editor'),
          alwaysShow: true
        },
        children: [
          {
            path: 'editor',
            component: () => import('@/views/Components/Editor/Editor.vue'),
            name: 'Editor',
            meta: {
              title: t('router.richText')
            }
          },
          {
            path: 'json-editor',
            component: () => import('@/views/Components/Editor/JsonEditor.vue'),
            name: 'JsonEditor',
            meta: {
              title: t('router.jsonEditor')
            }
          }
        ]
      },
      {
        path: 'search',
        component: () => import('@/views/Components/Search.vue'),
        name: 'Search',
        meta: {
          title: t('router.search')
        }
      },
      {
        path: 'descriptions',
        component: () => import('@/views/Components/Descriptions.vue'),
        name: 'Descriptions',
        meta: {
          title: t('router.descriptions')
        }
      },
      {
        path: 'image-viewer',
        component: () => import('@/views/Components/ImageViewer.vue'),
        name: 'ImageViewer',
        meta: {
          title: t('router.imageViewer')
        }
      },
      {
        path: 'dialog',
        component: () => import('@/views/Components/Dialog.vue'),
        name: 'Dialog',
        meta: {
          title: t('router.dialog')
        }
      },
      {
        path: 'icon',
        component: () => import('@/views/Components/Icon.vue'),
        name: 'Icon',
        meta: {
          title: t('router.icon')
        }
      },
      {
        path: 'icon-picker',
        component: () => import('@/views/Components/IconPicker.vue'),
        name: 'IconPicker',
        meta: {
          title: t('router.iconPicker')
        }
      },
      {
        path: 'echart',
        component: () => import('@/views/Components/Echart.vue'),
        name: 'Echart',
        meta: {
          title: t('router.echart')
        }
      },
      {
        path: 'count-to',
        component: () => import('@/views/Components/CountTo.vue'),
        name: 'CountTo',
        meta: {
          title: t('router.countTo')
        }
      },
      {
        path: 'qrcode',
        component: () => import('@/views/Components/Qrcode.vue'),
        name: 'Qrcode',
        meta: {
          title: t('router.qrcode')
        }
      },
      {
        path: 'highlight',
        component: () => import('@/views/Components/Highlight.vue'),
        name: 'Highlight',
        meta: {
          title: t('router.highlight')
        }
      },
      {
        path: 'infotip',
        component: () => import('@/views/Components/Infotip.vue'),
        name: 'Infotip',
        meta: {
          title: t('router.infotip')
        }
      },
      {
        path: 'input-password',
        component: () => import('@/views/Components/InputPassword.vue'),
        name: 'InputPassword',
        meta: {
          title: t('router.inputPassword')
        }
      },
      {
        path: 'waterfall',
        component: () => import('@/views/Components/Waterfall.vue'),
        name: 'waterfall',
        meta: {
          title: t('router.waterfall')
        }
      },
      {
        path: 'image-cropping',
        component: () => import('@/views/Components/ImageCropping.vue'),
        name: 'ImageCropping',
        meta: {
          title: t('router.imageCropping')
        }
      },
      {
        path: 'video-player',
        component: () => import('@/views/Components/VideoPlayer.vue'),
        name: 'VideoPlayer',
        meta: {
          title: t('router.videoPlayer')
        }
      },
      // {
      //   path: 'avatars',
      //   component: () => import('@/views/Components/Avatars.vue'),
      //   name: 'Avatars',
      //   meta: {
      //     title: t('router.avatars')
      //   }
      // },
      {
        path: 'i-agree',
        component: () => import('@/views/Components/IAgree.vue'),
        name: 'IAgree',
        meta: {
          title: t('router.iAgree')
        }
      }
    ]
  },
  {
    path: '/function',
    component: Layout,
    redirect: '/function/multipleTabs',
    name: 'Function',
    meta: {
      title: '优秀作业展示',
      icon: 'ri:function-fill',
      alwaysShow: true
    },
    children: [
      {
        path: 'showListBoard',
        component: () => import('@/views/Function/ShowList.vue'),
        name: 'MultipleTabs',
        meta: {
          title: '优秀作业列表'
        }
      },
      {
        path: 'multiple-tabs-demo/:id',
        component: () => import('@/views/Function/MultipleTabsDemo.vue'),
        name: 'MultipleTabsDemo',
        meta: {
          hidden: true,
          title: t('router.details'),
          canTo: true,
          activeMenu: '/function/multiple-tabs'
        }
      },
      {
        path: 'request',
        component: () => import('@/views/Function/Request.vue'),
        name: 'Request',
        meta: {
          title: t('router.request')
        }
      },
      {
        path: 'ShowItem',
        component: () => import('@/views/Function/ShowItem.vue'),
        name: 'Test',
        meta: {
          title: t('优秀作业展示'),
          permission: ['add', 'edit', 'delete']
        }
      }
    ]
  },
  {
    path: '/hooks',
    component: Layout,
    redirect: '/hooks/StuworkList',
    name: 'student',
    meta: {
      title: '学生功能',
      icon: 'ic:outline-webhook',
      alwaysShow: true
    },
    children: [
      {
        path: 'StuworkList',
        component: () => import('@/views/WorkStu/HomeworkLists.vue'),
        name: 'workList',
        meta: {
          title: '学生作业列表'
        }
      },
      {
        path: 'StuworkTable',
        component: () => import('@/views/WorkStu/WorkingBoard.vue'),
        name: 'workingboard',
        meta: {
          title: '学生工作台'
        }
      },
      {
        path: 'useTagsView',
        component: () => import('@/views/hooks/useTagsView.vue'),
        name: 'UseTagsView',
        meta: {
          title: 'useTagsView'
        }
      },
      {
        path: 'useValidator',
        component: () => import('@/views/hooks/useValidator.vue'),
        name: 'UseValidator',
        meta: {
          title: 'useValidator'
        }
      },
      {
        path: 'useCrudSchemas',
        component: () => import('@/views/hooks/useCrudSchemas.vue'),
        name: 'UseCrudSchemas',
        meta: {
          title: 'useCrudSchemas'
        }
      },
      {
        path: 'useClipboard',
        component: () => import('@/views/hooks/useClipboard.vue'),
        name: 'UseClipboard',
        meta: {
          title: 'useClipboard'
        }
      },
      {
        path: 'useNetwork',
        component: () => import('@/views/hooks/useNetwork.vue'),
        name: 'UseNetwork',
        meta: {
          title: 'useNetwork'
        }
      }
    ]
  },
  {
    path: '/level',
    component: Layout,
    redirect: '/level/menu1/menu1-1/menu1-1-1',
    name: 'Level',
    meta: {
      title: t('router.level'),
      icon: 'carbon:skill-level-advanced'
    },
    children: [
      {
        path: 'menu1',
        name: 'Menu1',
        component: getParentLayout(),
        redirect: '/level/menu1/menu1-1/menu1-1-1',
        meta: {
          title: t('router.menu1')
        },
        children: [
          {
            path: 'menu1-1',
            name: 'Menu11',
            component: getParentLayout(),
            redirect: '/level/menu1/menu1-1/menu1-1-1',
            meta: {
              title: t('router.menu11'),
              alwaysShow: true
            },
            children: [
              {
                path: 'menu1-1-1',
                name: 'Menu111',
                component: () => import('@/views/Level/Menu111.vue'),
                meta: {
                  title: t('router.menu111')
                }
              }
            ]
          },
          {
            path: 'menu1-2',
            name: 'Menu12',
            component: () => import('@/views/Level/Menu12.vue'),
            meta: {
              title: t('router.menu12')
            }
          }
        ]
      },
      {
        path: 'menu2',
        name: 'Menu2',
        component: () => import('@/views/Level/Menu2.vue'),
        meta: {
          title: t('router.menu2')
        }
      }
    ]
  },
  {
    path: '/example',
    component: Layout,
    redirect: '/example/example-dialog',
    name: 'Example',
    meta: {
      title: t('router.example'),
      icon: 'ep:management',
      alwaysShow: true
    },
    children: [
      {
        path: 'example-dialog',
        component: () => import('@/views/Example/Dialog/ExampleDialog.vue'),
        name: 'ExampleDialog',
        meta: {
          title: t('router.exampleDialog')
        }
      },
      {
        path: 'example-page',
        component: () => import('@/views/Example/Page/ExamplePage.vue'),
        name: 'ExamplePage',
        meta: {
          title: t('router.examplePage')
        }
      },
      {
        path: 'example-add',
        component: () => import('@/views/Example/Page/ExampleAdd.vue'),
        name: 'ExampleAdd',
        meta: {
          title: t('router.exampleAdd'),
          noTagsView: true,
          noCache: true,
          hidden: true,
          canTo: true,
          activeMenu: '/example/example-page'
        }
      },
      {
        path: 'example-edit',
        component: () => import('@/views/Example/Page/ExampleEdit.vue'),
        name: 'ExampleEdit',
        meta: {
          title: t('router.exampleEdit'),
          noTagsView: true,
          noCache: true,
          hidden: true,
          canTo: true,
          activeMenu: '/example/example-page'
        }
      },
      {
        path: 'example-detail',
        component: () => import('@/views/Example/Page/ExampleDetail.vue'),
        name: 'ExampleDetail',
        meta: {
          title: t('router.exampleDetail'),
          noTagsView: true,
          noCache: true,
          hidden: true,
          canTo: true,
          activeMenu: '/example/example-page'
        }
      }
    ]
  },
  {
    path: '/error',
    component: Layout,
    redirect: '/error/404',
    name: 'Error',
    meta: {
      title: t('router.errorPage'),
      icon: 'ci:error',
      alwaysShow: true
    },
    children: [
      {
        path: '404-demo',
        component: () => import('@/views/Error/404.vue'),
        name: '404Demo',
        meta: {
          title: '404'
        }
      },
      {
        path: '403-demo',
        component: () => import('@/views/Error/403.vue'),
        name: '403Demo',
        meta: {
          title: '403'
        }
      },
      {
        path: '500-demo',
        component: () => import('@/views/Error/500.vue'),
        name: '500Demo',
        meta: {
          title: '500'
        }
      }
    ]
  },
  {
    path: '/authorization',
    component: Layout,
    redirect: '/authorization/user',
    name: 'Authorization',
    meta: {
      title: t('router.authorization'),
      icon: 'eos-icons:role-binding',
      alwaysShow: true
    },
    children: [
      {
        path: 'department',
        component: () => import('@/views/Authorization/Department/Department.vue'),
        name: 'Department',
        meta: {
          title: t('router.department')
        }
      },
      {
        path: 'user',
        component: () => import('@/views/Authorization/User/User.vue'),
        name: 'User',
        meta: {
          title: t('router.user')
        }
      },
      {
        path: 'menu',
        component: () => import('@/views/Authorization/Menu/Menu.vue'),
        name: 'Menu',
        meta: {
          title: t('router.menuManagement')
        }
      },
      {
        path: 'role',
        component: () => import('@/views/Authorization/Role/Role.vue'),
        name: 'Role',
        meta: {
          title: t('router.role')
        }
      }
    ]
  },
  // {
  //   // 第一层是优秀作业展示
  //   path: '/GreatWork',
  //   component: Layout,
  //   redirect: '/GreatWork/GreatWorkList',
  //   name: 'ShowGreatWork',
  //   meta: {
  //     title: '优秀作业展示',
  //     icon: 'ant-design:dashboard-filled',
  //     alwaysShow: true
  //   },
  //   children: [
  //     //两个子路由，一个展示板，一个是列表
  //     {
  //       path: 'GreatWorkList',
  //       component: () => import('@/views/Showboard/ShowList.vue'),
  //       name: 'GreatWorkList',
  //       meta: {
  //         title: '优秀作业列表',
  //         noCache: true,
  //         affix: true
  //       }
  //     },
  //     {
  //       // 固定在侧边栏，然后启用缓存
  //       path: 'GreatWorkBoard',
  //       component: () => import('@/views/Showboard/ShowBoard.vue'),
  //       name: 'GreatWorkBoard',
  //       meta: {
  //         title: '优秀作业展示板',
  //         noCache: false,
  //         affix: true
  //       }
  //     }
  //   ]
  // },
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
