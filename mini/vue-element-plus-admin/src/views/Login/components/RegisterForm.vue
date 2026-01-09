<script setup lang="tsx">
import { Form, FormSchema } from '@/components/Form'
import { reactive, ref, watch } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { useForm } from '@/hooks/web/useForm'
import { FormRules } from 'element-plus'
import { useValidator } from '@/hooks/web/useValidator'
import { BaseButton } from '@/components/Button'
import { type RouteLocationNormalizedLoaded, RouteRecordRaw, useRouter } from 'vue-router'
import { useAppStore } from '@/store/modules/app'
import { usePermissionStore } from '@/store/modules/permission'
import { getGuestRoleApi } from '@/apiX/login'
import { getTestRoleApi } from '@/api/login'
import { useUserStore } from '@/store/modules/user'
// import { IAgree } from '@/components/IAgree'

const emit = defineEmits(['to-login'])

const userStore = useUserStore()

const { formRegister } = useForm()

const { t } = useI18n()

const { required, check } = useValidator()

const appStore = useAppStore()

const permissionStore = usePermissionStore()

const { currentRoute, addRoute, push } = useRouter()

watch(
  () => currentRoute.value,
  (route: RouteLocationNormalizedLoaded) => {
    redirect.value = route?.query?.redirect as string
  },
  {
    immediate: true
  }
)
const redirect = ref<string>('')

const schema = reactive<FormSchema[]>([
  {
    field: 'title',
    colProps: {
      span: 24
    },
    formItemProps: {
      slots: {
        default: () => {
          return <h2 class="text-2xl font-bold text-center w-[100%]">{t('login.register')}</h2>
        }
      }
    }
  },
  {
    field: 'register',
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
                  type="primary"
                  class="w-[100%]"
                  loading={loading.value}
                  onClick={loginRegister}
                >
                  {t('login.register')}
                </BaseButton>
              </div>
              <div class="w-[100%] mt-15px">
                <BaseButton class="w-[100%]" onClick={toLogin}>
                  {t('学生||教师，返回登录界面')}
                </BaseButton>
              </div>
            </>
          )
        }
      }
    }
  }
])

const rules: FormRules = {
  number: [required()],
  // password: [required()],
  // check_password: [required()],
  code: [required()],
  iAgree: [required(), check()]
}

const toLogin = () => {
  emit('to-login')
}

const loading = ref(false)

const loginRegister = async () => {
  if (appStore.getDynamicRouter) {
    getRole()
  } else {
    await permissionStore.generateRoutes('static').catch(() => {})
    permissionStore.getAddRouters.forEach((route) => {
      addRoute(route as RouteRecordRaw) // 动态添加可访问路由表
    })
    permissionStore.setIsAddRouters(true)
    await push({ path: redirect.value || permissionStore.addRouters[0].path })
  }
}

const getRole = async () => {
  const params = {
    roleName: ''
  }
  userStore.setRole('Guest')
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
