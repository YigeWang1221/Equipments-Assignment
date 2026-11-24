import request from '@/axios'
import { unwrapData } from './types'
import type { ApiResponse, ClassSaveRequest, ClassVO, GroupVO, StudentVO, WorkVO } from './types'

export interface ClassQuery {
  type?: 'IOT' | 'ARM' | string
  year?: number | string
}

export const getClasses = async (params?: ClassQuery): Promise<ClassVO[]> => {
  const res = await request.get<ApiResponse<ClassVO[]>>({
    url: '/api/v1/classes',
    params
  })
  return unwrapData<ClassVO[]>(res)
}

export const getClass = async (id: number | string): Promise<ClassVO> => {
  const res = await request.get<ApiResponse<ClassVO>>({ url: `/api/v1/classes/${id}` })
  return unwrapData<ClassVO>(res)
}

export const createClass = async (data: ClassSaveRequest): Promise<ClassVO> => {
  const res = await request.post<ApiResponse<ClassVO>>({ url: '/api/v1/classes', data })
  return unwrapData<ClassVO>(res)
}

export const updateClass = async (
  id: number | string,
  data: ClassSaveRequest
): Promise<ClassVO> => {
  const res = await request.put<ApiResponse<ClassVO>>({ url: `/api/v1/classes/${id}`, data })
  return unwrapData<ClassVO>(res)
}

export const deleteClass = async (id: number | string): Promise<void> => {
  const res = await request.delete<ApiResponse<void>>({ url: `/api/v1/classes/${id}` })
  return unwrapData<void>(res)
}

export const getClassStudents = async (id: number | string): Promise<StudentVO[]> => {
  const res = await request.get<ApiResponse<StudentVO[]>>({
    url: `/api/v1/classes/${id}/students`
  })
  return unwrapData<StudentVO[]>(res)
}

export const getClassGroups = async (id: number | string): Promise<GroupVO[]> => {
  const res = await request.get<ApiResponse<GroupVO[]>>({ url: `/api/v1/classes/${id}/groups` })
  return unwrapData<GroupVO[]>(res)
}

export const getClassWorks = async (id: number | string): Promise<WorkVO[]> => {
  const res = await request.get<ApiResponse<WorkVO[]>>({ url: `/api/v1/classes/${id}/works` })
  return unwrapData<WorkVO[]>(res)
}
