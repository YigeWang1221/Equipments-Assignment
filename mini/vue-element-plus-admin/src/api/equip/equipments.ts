import request from '@/axios'
import { unwrapData } from './types'
import type { ApiResponse, EquipmentSaveRequest, EquipmentVO } from './types'

export interface EquipmentQuery {
  keyword?: string
}

export const getEquipments = async (params?: EquipmentQuery): Promise<EquipmentVO[]> => {
  const res = await request.get<ApiResponse<EquipmentVO[]>>({
    url: '/api/v1/equipments',
    params
  })
  return unwrapData<EquipmentVO[]>(res)
}

export const getEquipment = async (id: number | string): Promise<EquipmentVO> => {
  const res = await request.get<ApiResponse<EquipmentVO>>({ url: `/api/v1/equipments/${id}` })
  return unwrapData<EquipmentVO>(res)
}

export const createEquipment = async (data: EquipmentSaveRequest): Promise<EquipmentVO> => {
  const res = await request.post<ApiResponse<EquipmentVO>>({ url: '/api/v1/equipments', data })
  return unwrapData<EquipmentVO>(res)
}

export const updateEquipment = async (
  id: number | string,
  data: EquipmentSaveRequest
): Promise<EquipmentVO> => {
  const res = await request.put<ApiResponse<EquipmentVO>>({
    url: `/api/v1/equipments/${id}`,
    data
  })
  return unwrapData<EquipmentVO>(res)
}

export const deleteEquipment = async (id: number | string): Promise<void> => {
  const res = await request.delete<ApiResponse<void>>({ url: `/api/v1/equipments/${id}` })
  return unwrapData<void>(res)
}
