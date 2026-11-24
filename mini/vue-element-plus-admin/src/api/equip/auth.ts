import request from '@/axios'
import { unwrapData } from './types'
import type { ApiResponse, LoginRequest, LoginResult, UserVO } from './types'

export const loginApi = async (data: LoginRequest): Promise<LoginResult> => {
  const res = await request.post<ApiResponse<LoginResult>>({
    url: '/api/v1/auth/login',
    data
  })
  return unwrapData<LoginResult>(res)
}

export const getMeApi = async (): Promise<UserVO> => {
  const res = await request.get<ApiResponse<UserVO>>({ url: '/api/v1/auth/me' })
  return unwrapData<UserVO>(res)
}

export const logoutApi = async (): Promise<void> => {
  const res = await request.post<ApiResponse<void>>({ url: '/api/v1/auth/logout' })
  return unwrapData<void>(res)
}
