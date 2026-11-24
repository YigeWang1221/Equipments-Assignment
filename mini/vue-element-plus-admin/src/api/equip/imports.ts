import request from '@/axios'
import { unwrapData } from './types'
import type { ApiResponse } from './types'

export const importStudents = async (classId: number | string, file: File): Promise<any> => {
  const res = await request.post<ApiResponse<any>>({
    url: '/api/v1/imports/students',
    data: { file, classId },
    headers: { 'Content-Type': 'multipart/form-data' }
  })
  return unwrapData<any>(res)
}

export const importGroups = async (classId: number | string, file: File): Promise<any> => {
  const res = await request.post<ApiResponse<any>>({
    url: '/api/v1/imports/groups',
    data: { file, classId },
    headers: { 'Content-Type': 'multipart/form-data' }
  })
  return unwrapData<any>(res)
}

const downloadBlob = async (url: string): Promise<Blob> => {
  const res: any = await request.get({ url, responseType: 'blob' })
  return res.data as Blob
}

export const downloadStudentsTemplate = (): Promise<Blob> => {
  return downloadBlob('/api/v1/imports/templates/students.xlsx')
}

export const downloadGroupsTemplate = (): Promise<Blob> => {
  return downloadBlob('/api/v1/imports/templates/groups.xlsx')
}
