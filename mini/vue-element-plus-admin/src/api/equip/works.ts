import request from '@/axios'
import { unwrapData } from './types'
import type { ApiResponse, WorkDetailVO, WorkSaveRequest, WorkVO } from './types'

export interface WorkQuery {
  classId?: number | string
  status?: number | string
}

export const getWorks = async (params?: WorkQuery): Promise<WorkVO[]> => {
  const res = await request.get<ApiResponse<WorkVO[]>>({ url: '/api/v1/works', params })
  return unwrapData<WorkVO[]>(res)
}

export const getWork = async (id: number | string): Promise<WorkDetailVO> => {
  const res = await request.get<ApiResponse<WorkDetailVO>>({ url: `/api/v1/works/${id}` })
  return unwrapData<WorkDetailVO>(res)
}

export const createWork = async (data: WorkSaveRequest): Promise<WorkVO> => {
  const res = await request.post<ApiResponse<WorkVO>>({ url: '/api/v1/works', data })
  return unwrapData<WorkVO>(res)
}

export const updateWork = async (id: number | string, data: WorkSaveRequest): Promise<WorkVO> => {
  const res = await request.put<ApiResponse<WorkVO>>({ url: `/api/v1/works/${id}`, data })
  return unwrapData<WorkVO>(res)
}

export const deleteWork = async (id: number | string): Promise<void> => {
  const res = await request.delete<ApiResponse<void>>({ url: `/api/v1/works/${id}` })
  return unwrapData<void>(res)
}

export const getWorkContent = async (id: number | string): Promise<WorkDetailVO> => {
  const res = await request.get<ApiResponse<WorkDetailVO>>({ url: `/api/v1/works/${id}/content` })
  return unwrapData<WorkDetailVO>(res)
}
