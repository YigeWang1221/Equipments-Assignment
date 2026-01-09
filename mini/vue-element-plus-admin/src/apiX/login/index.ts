import request from '@/axios'
import type { UserLoginType, GuestApplyType, GuestAndMessageType } from './types'

/////////////////////
// 根据用户的类型寻找请求的url
//教师类型的
// export const getAdminRoleApi = (
//   params: RoleParams
// ): Promise<IResponse<AppCustomRouteRecordRaw[]>> => {
//   return request.get({ url: '/mock/role/list', params })
// }
export const getTeacherRoleApi = (): Promise<IResponse<AppCustomRouteRecordRaw[]>> => {
  return request.get({ url: '/mock/role/TeacherList' })
}
//学生类型的
export const getStudentRoleApi = (): Promise<IResponse<AppCustomRouteRecordRaw[]>> => {
  return request.get({ url: '/mock/role/StudentList' })
}
//访客类型的
export const getGuestRoleApi = (): Promise<IResponse<AppCustomRouteRecordRaw[]>> => {
  return request.get({ url: '/mock/role/GuestList' })
}

/////////////////////////
// 登陆、登出消息
// export const MyLogin = (params: UserLoginType) => {
//   return request.get<any>({ url: 'http://127.0.0.1:8090/Usr/logIn', params })
// }
export const MyLogin = (params: UserLoginType) => {
  return request
    .get<IResponse>({ url: 'http://127.0.0.1:8090/Usr/logIn', params })
    .then((response) => {
      // 这里可以添加额外的逻辑处理返回的 response
      return response
    })
    .catch((error) => {
      // 处理请求错误
      console.error('MyLogin request error:', error)
      throw error
    })
}

export const MyLogout = (params: UserLoginType) => {
  return request.get({ url: '', params })
}

/// 访客登陆登出
// 请求短信验证码
export const GuestApply = (params: GuestApplyType) => {
  return request.post({ url: '', params })
}
// 和短信验证码一起发送
export const GuestLogin = (params: GuestAndMessageType) => {
  return request.get({ url: '', params })
}
