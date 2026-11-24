<script setup lang="tsx">
import { reactive, ref, watch, onMounted, unref } from 'vue'
import { Form, FormSchema } from '@/components/Form'
import { useI18n } from '@/hooks/web/useI18n'
import { ElCheckbox, ElLink } from 'element-plus'
import { useForm } from '@/hooks/web/useForm'
import { getTestRoleApi } from '@/api/login'
import { useAppStore } from '@/store/modules/app'
import { usePermissionStore } from '@/store/modules/permission'
import { useRouter } from 'vue-router'
import type { RouteLocationNormalizedLoaded, RouteRecordRaw } from 'vue-router'
import { UserType } from '@/api/login/types'
// import { useValidator } from '@/hooks/web/useValidator'
import { useUserStore } from '@/store/modules/user'
import { BaseButton } from '@/components/Button'
import { getGuestRoleApi, getTeacherRoleApi, getStudentRoleApi } from '@/apiX/login'
import { loginApi } from '@/api/equip/auth'
import type { BackendRole } from '@/api/equip/types'

const emit = defineEmits(['to-register'])

const appStore = useAppStore()

const userStore = useUserStore()

const permissionStore = usePermissionStore()

const { currentRoute, addRoute, push } = useRouter()

const { t } = useI18n()

const rules = {
  // username: [required()],
  // password: [required()]
}

const schema = reactive<FormSchema[]>([
  {
    field: 'title',
    colProps: {
      span: 24
    },
    formItemProps: {
      slots: {
        default: () => {
          return <h2 class="text-2xl font-bold text-center w-[100%]">{t('login.login')}</h2>
        }
      }
    }
  },
  {
    field: 'username',
    label: t('login.username'),
    // value: 'admin',
    component: 'Input',
    colProps: {
      span: 24
    },
    componentProps: {
      placeholder: '参加课程的同学输入学号，访客输入手机号'
    }
  },
  {
    field: 'password',
    label: t('login.password'),
    // value: 'admin',
    component: 'InputPassword',
    colProps: {
      span: 24
    },
    componentProps: {
      style: {
        width: '100%'
      },
      placeholder: '参加课程的同学输入学号，访客输入电话号码'
    }
  },
  {
    field: 'tool',
    colProps: {
      span: 24
    },
    formItemProps: {
      slots: {
        default: () => {
          return (
            <>
              <div class="flex justify-between items-center w-[100%]">
                <ElCheckbox v-model={remember.value} label={t('login.remember')} size="small" />
                <ElLink type="primary" underline={false}>
                  {t('login.forgetPassword')}
                </ElLink>
              </div>
            </>
          )
        }
      }
    }
  },
  {
    field: 'login',
    colProps: {
      span: 24
    },
    formItemProps: {
      slots: {
        default: () => {
          return (
            <>
              <div class="w-[100%]">
                <BaseButton
                  loading={loading.value}
                  type="primary"
                  class="w-[100%]"
                  onClick={signIn}
                >
                  {t('login.login')}
                </BaseButton>
              </div>
              <div class="w-[100%] mt-15px">
                <BaseButton class="w-[100%]" onClick={toRegister}>
                  {t('login.register')}
                </BaseButton>
              </div>
            </>
          )
        }
      }
    }
  }
])

const remember = ref(userStore.getRememberMe)

const initLoginInfo = () => {
  const loginInfo = userStore.getLoginInfo
  if (loginInfo) {
    const { username, password } = loginInfo
    setValues({ username, password })
  }
}
onMounted(() => {
  initLoginInfo()
})

const { formRegister, formMethods } = useForm()
const { getFormData, getElFormExpose, setValues } = formMethods

const loading = ref(false)

const redirect = ref<string>('')

watch(
  () => currentRoute.value,
  (route: RouteLocationNormalizedLoaded) => {
    redirect.value = route?.query?.redirect as string
  },
  {
    immediate: true
  }
)

// 登录
const signIn = async () => {
  const formRef = await getElFormExpose()
  await formRef?.validate(async (isValid) => {
    if (isValid) {
      loading.value = true
      const formData = await getFormData<UserType>()
      try {
        const result = await loginApi({
          username: formData.username,
          password: formData.password
        })
        if (result) {
          // 是否记住我
          if (unref(remember)) {
            userStore.setLoginInfo({
              username: formData.username,
              password: formData.password
            })
          } else {
            userStore.setLoginInfo(undefined)
          }
          userStore.setRememberMe(unref(remember))
          userStore.setToken(result.token)
          const backendUser = result.user
          const roleMap: Record<BackendRole, string> = {
            TEACHER: 'Teacher',
            STUDENT: 'Student',
            GUEST: 'Guest'
          }
          const role = roleMap[backendUser.role] || 'Guest'
          const userInfo: UserType = {
            username: backendUser.username || formData.username,
            password: '',
            role,
            roleId: String(backendUser.id ?? '')
          }
          userStore.setUserInfo(userInfo)
          userStore.setRole(role)
          userStore.setClassId(backendUser.classId ?? undefined)
          userStore.setStuId(backendUser.stuId ?? undefined)
          // 是否使用动态路由
          if (appStore.getDynamicRouter) {
            await getRole(role, backendUser.stuId ?? undefined)
          } else {
            await permissionStore.generateRoutes('static').catch(() => {})
            permissionStore.getAddRouters.forEach((route) => {
              addRoute(route as RouteRecordRaw) // 动态添加可访问路由表
            })
            permissionStore.setIsAddRouters(true)
            push({ path: redirect.value || permissionStore.addRouters[0].path })
          }
        }
      } catch (error) {
        console.error('登录失败', error)
      } finally {
        loading.value = false
      }
    }
  })
}

// 获取角色信息
const getRole = async (param: string, stuId?: number) => {
  const formData = await getFormData<UserType>()
  const params = {
    roleName: formData.username
  }
  if (param === 'Teacher' || param === 'TEACHER') {
    userStore.setRole('Teacher')
    console.log('userStore.role: ' + userStore.getRole)
    const res =
      appStore.getDynamicRouter && appStore.getServerDynamicRouter
        ? await getTeacherRoleApi()
        : await getTestRoleApi(params)
    if (res) {
      const routers = res.data || []
      userStore.setRoleRouters(routers)
      appStore.getDynamicRouter && appStore.getServerDynamicRouter
        ? await permissionStore.generateRoutes('server', routers).catch(() => {})
        : await permissionStore.generateRoutes('frontEnd', routers).catch(() => {})

      permissionStore.getAddRouters.forEach((route) => {
        addRoute(route as RouteRecordRaw) // 动态添加可访问路由表
      })
      permissionStore.setIsAddRouters(true)
      push({ path: redirect.value || permissionStore.addRouters[0].path })
    }
  }
  if (param === 'Student' || param === 'Stu' || param === 'STUDENT') {
    userStore.setRole('Student')
    userStore.setStuId(stuId)
    const res =
      appStore.getDynamicRouter && appStore.getServerDynamicRouter
        ? await getStudentRoleApi()
        : await getTestRoleApi(params)
    if (res) {
      const routers = res.data || []
      userStore.setRoleRouters(routers)
      appStore.getDynamicRouter && appStore.getServerDynamicRouter
        ? await permissionStore.generateRoutes('server', routers).catch(() => {})
        : await permissionStore.generateRoutes('frontEnd', routers).catch(() => {})

      permissionStore.getAddRouters.forEach((route) => {
        addRoute(route as RouteRecordRaw) // 动态添加可访问路由表
      })
      permissionStore.setIsAddRouters(true)
      push({ path: redirect.value || permissionStore.addRouters[0].path })
    }
  }
  if (param === 'Guest' || param === 'GUEST') {
    userStore.setRole('Guest')
    userStore.setStuId(stuId)
    const res =
      appStore.getDynamicRouter && appStore.getServerDynamicRouter
        ? await getGuestRoleApi()
        : await getTestRoleApi(params)
    if (res) {
      const routers = res.data || []
      userStore.setRoleRouters(routers)
      appStore.getDynamicRouter && appStore.getServerDynamicRouter
        ? await permissionStore.generateRoutes('server', routers).catch(() => {})
        : await permissionStore.generateRoutes('frontEnd', routers).catch(() => {})

      permissionStore.getAddRouters.forEach((route) => {
        addRoute(route as RouteRecordRaw) // 动态添加可访问路由表
      })
      permissionStore.setIsAddRouters(true)
      push({ path: redirect.value || permissionStore.addRouters[0].path })
    }
  }
}

// 去注册页面
const toRegister = async () => {
  signIn()
  if (appStore.getDynamicRouter) {
    getRole('Guest', 0)
  } else {
    await permissionStore.generateRoutes('static').catch(() => {})
    permissionStore.getAddRouters.forEach((route) => {
      addRoute(route as RouteRecordRaw) // 动态添加可访问路由表
    })
    permissionStore.setIsAddRouters(true)
    push({ path: redirect.value || permissionStore.addRouters[0].path })
    // push({ path: '/function/showListBoard' })
  }
  push({ path: '/function/showListBoard' })
}
</script>

<template>
  <Form
    :schema="schema"
    :rules="rules"
    label-position="top"
    hide-required-asterisk
    size="large"
    class="dark:(border-1 border-[var(--el-border-color)] border-solid)"
    @register="formRegister"
  />
</template>
