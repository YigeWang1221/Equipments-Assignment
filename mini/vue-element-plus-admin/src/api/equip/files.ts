import request from '@/axios'
import { unwrapData } from './types'
import type { ApiResponse, FileVO } from './types'

export const uploadFile = async (file: File): Promise<FileVO> => {
  const res = await request.post<ApiResponse<FileVO>>({
    url: '/api/v1/files',
    data: { file },
    headers: { 'Content-Type': 'multipart/form-data' }
  })
  return unwrapData<FileVO>(res)
}
