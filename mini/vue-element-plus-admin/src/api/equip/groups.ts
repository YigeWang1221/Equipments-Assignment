import request from '@/axios'
import { unwrapData } from './types'
import type { ApiResponse, GroupSaveRequest, GroupVO } from './types'

export interface GroupQuery {
  classId?: number | string
}

export const getGroups = async (params?: GroupQuery): Promise<GroupVO[]> => {
  const res = await request.get<ApiResponse<GroupVO[]>>({ url: '/api/v1/groups', params })
  return unwrapData<GroupVO[]>(res)
}

export const getClassGroups = async (classId: number | string): Promise<GroupVO[]> => {
  const res = await request.get<ApiResponse<GroupVO[]>>({
    url: `/api/v1/classes/${classId}/groups`
  })
  return unwrapData<GroupVO[]>(res)
}

export const createGroup = async (data: GroupSaveRequest): Promise<GroupVO> => {
  const res = await request.post<ApiResponse<GroupVO>>({ url: '/api/v1/groups', data })
  return unwrapData<GroupVO>(res)
}

export const updateGroup = async (
  id: number | string,
  data: GroupSaveRequest
): Promise<GroupVO> => {
  const res = await request.put<ApiResponse<GroupVO>>({ url: `/api/v1/groups/${id}`, data })
  return unwrapData<GroupVO>(res)
}

export const deleteGroup = async (id: number | string): Promise<void> => {
  const res = await request.delete<ApiResponse<void>>({ url: `/api/v1/groups/${id}` })
  return unwrapData<void>(res)
}

export const deleteClassGroups = async (classId: number | string): Promise<void> => {
  const res = await request.delete<ApiResponse<void>>({
    url: `/api/v1/classes/${classId}/groups`
  })
  return unwrapData<void>(res)
}
