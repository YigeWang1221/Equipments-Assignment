import request from '@/axios'
import { unwrapData } from './types'
import type { ApiResponse, AttendanceStatsVO, ClassStatsVO, EquipmentStatsVO } from './types'

export const getClassStats = async (classId: number | string): Promise<ClassStatsVO> => {
  const res = await request.get<ApiResponse<ClassStatsVO>>({
    url: `/api/v1/stats/classes/${classId}`
  })
  return unwrapData<ClassStatsVO>(res)
}

export const getEquipmentStats = async (): Promise<EquipmentStatsVO> => {
  const res = await request.get<ApiResponse<EquipmentStatsVO>>({
    url: '/api/v1/stats/equipments'
  })
  return unwrapData<EquipmentStatsVO>(res)
}

export interface AttendanceQuery {
  classId?: number | string
  from?: string
  to?: string
}

export const getAttendanceStats = async (params?: AttendanceQuery): Promise<AttendanceStatsVO> => {
  const res = await request.get<ApiResponse<AttendanceStatsVO>>({
    url: '/api/v1/stats/attendance',
    params
  })
  return unwrapData<AttendanceStatsVO>(res)
}
