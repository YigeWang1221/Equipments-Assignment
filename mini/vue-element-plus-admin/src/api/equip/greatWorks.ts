import request from '@/axios'
import { unwrapData } from './types'
import type {
  ApiResponse,
  GreatWorkCreateRequest,
  GreatWorkDetailVO,
  GreatWorkUpdateRequest,
  GreatWorkVO
} from './types'

export interface GreatWorkQuery {
  year?: number | string
  classType?: number | string
}

export const getGreatWorks = async (params?: GreatWorkQuery): Promise<GreatWorkVO[]> => {
  const res = await request.get<ApiResponse<GreatWorkVO[]>>({
    url: '/api/v1/great-works',
    params
  })
  return unwrapData<GreatWorkVO[]>(res)
}

export const getGreatWork = async (id: number | string): Promise<GreatWorkDetailVO> => {
  const res = await request.get<ApiResponse<GreatWorkDetailVO>>({
    url: `/api/v1/great-works/${id}`
  })
  return unwrapData<GreatWorkDetailVO>(res)
}

export const createGreatWorkFromSubmission = async (
  submissionId: number | string,
  data: GreatWorkCreateRequest
): Promise<GreatWorkVO> => {
  const res = await request.post<ApiResponse<GreatWorkVO>>({
    url: `/api/v1/submissions/${submissionId}/great-work`,
    data
  })
  return unwrapData<GreatWorkVO>(res)
}

export const updateGreatWork = async (
  id: number | string,
  data: GreatWorkUpdateRequest
): Promise<GreatWorkVO> => {
  const res = await request.put<ApiResponse<GreatWorkVO>>({
    url: `/api/v1/great-works/${id}`,
    data
  })
  return unwrapData<GreatWorkVO>(res)
}

export const deleteGreatWork = async (id: number | string): Promise<void> => {
  const res = await request.delete<ApiResponse<void>>({ url: `/api/v1/great-works/${id}` })
  return unwrapData<void>(res)
}
