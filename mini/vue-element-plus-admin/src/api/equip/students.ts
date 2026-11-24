import request from '@/axios'
import { unwrapData } from './types'
import type { ApiResponse, ClassVO, GroupVO, StudentSaveRequest, StudentVO } from './types'

export interface StudentQuery {
  classId?: number | string
}

export const getStudents = async (params?: StudentQuery): Promise<StudentVO[]> => {
  const res = await request.get<ApiResponse<StudentVO[]>>({ url: '/api/v1/students', params })
  return unwrapData<StudentVO[]>(res)
}

export const getStudentsByClass = async (classId: number | string): Promise<StudentVO[]> => {
  return getStudents({ classId })
}

export const createStudent = async (data: StudentSaveRequest): Promise<StudentVO> => {
  const res = await request.post<ApiResponse<StudentVO>>({ url: '/api/v1/students', data })
  return unwrapData<StudentVO>(res)
}

export const updateStudent = async (
  stuId: number | string,
  data: StudentSaveRequest
): Promise<StudentVO> => {
  const res = await request.put<ApiResponse<StudentVO>>({
    url: `/api/v1/students/${stuId}`,
    data
  })
  return unwrapData<StudentVO>(res)
}

export const deleteStudent = async (
  stuId: number | string,
  classId?: number | string
): Promise<void> => {
  const res = await request.delete<ApiResponse<void>>({
    url: `/api/v1/students/${stuId}`,
    params: { classId }
  })
  return unwrapData<void>(res)
}

export const getStudentClass = async (stuId: number | string): Promise<ClassVO> => {
  const res = await request.get<ApiResponse<ClassVO>>({
    url: `/api/v1/students/${stuId}/class`
  })
  return unwrapData<ClassVO>(res)
}

export const getStudentGroup = async (stuId: number | string): Promise<GroupVO> => {
  const res = await request.get<ApiResponse<GroupVO>>({
    url: `/api/v1/students/${stuId}/group`
  })
  return unwrapData<GroupVO>(res)
}

export const getMyClass = async (): Promise<ClassVO> => {
  const res = await request.get<ApiResponse<ClassVO>>({ url: '/api/v1/students/me/class' })
  return unwrapData<ClassVO>(res)
}

export const getMyGroup = async (): Promise<GroupVO> => {
  const res = await request.get<ApiResponse<GroupVO>>({ url: '/api/v1/students/me/group' })
  return unwrapData<GroupVO>(res)
}
