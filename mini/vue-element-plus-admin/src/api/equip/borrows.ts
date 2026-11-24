import request from '@/axios'
import { unwrapData } from './types'
import type { ApiResponse, BorrowVO } from './types'

export interface BorrowQuery {
  status?: number | string
  stuId?: number | string
  equipmentId?: number | string
}

export const createBorrow = async (
  equipmentId: number | string,
  num: number
): Promise<BorrowVO> => {
  const res = await request.post<ApiResponse<BorrowVO>>({
    url: `/api/v1/equipments/${equipmentId}/borrows`,
    data: { num }
  })
  return unwrapData<BorrowVO>(res)
}

export const getBorrows = async (params?: BorrowQuery): Promise<BorrowVO[]> => {
  const res = await request.get<ApiResponse<BorrowVO[]>>({
    url: '/api/v1/borrows',
    params
  })
  return unwrapData<BorrowVO[]>(res)
}

export const getMyBorrows = async (params?: BorrowQuery): Promise<BorrowVO[]> => {
  const res = await request.get<ApiResponse<BorrowVO[]>>({
    url: '/api/v1/borrows/mine',
    params
  })
  return unwrapData<BorrowVO[]>(res)
}

export const approveBorrow = async (id: number | string): Promise<void> => {
  const res = await request.post<ApiResponse<void>>({
    url: `/api/v1/borrows/${id}/approve`
  })
  return unwrapData<void>(res)
}

export const rejectBorrow = async (id: number | string): Promise<void> => {
  const res = await request.post<ApiResponse<void>>({
    url: `/api/v1/borrows/${id}/reject`
  })
  return unwrapData<void>(res)
}

export const returnBorrow = async (id: number | string): Promise<void> => {
  const res = await request.post<ApiResponse<void>>({
    url: `/api/v1/borrows/${id}/return`
  })
  return unwrapData<void>(res)
}

export const cancelBorrow = async (id: number | string): Promise<void> => {
  const res = await request.post<ApiResponse<void>>({
    url: `/api/v1/borrows/${id}/cancel`
  })
  return unwrapData<void>(res)
}
