/**
 * 统一响应结构：后端所有接口都返回 { code, message, data }
 * 成功时 code === 0
 */
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

/** 从响应包装中取出业务数据 */
export const unwrapData = <T>(res: unknown): T => {
  return (res as ApiResponse<T>).data
}

export type BackendRole = 'TEACHER' | 'STUDENT' | 'GUEST'

export interface UserVO {
  id: number
  name: string
  role: BackendRole
  stuId?: number | null
  classId?: number | null
  groupId?: number | null
  groupName?: string | null
  phone?: string | null
  username?: string | null
}

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResult {
  token: string
  user: UserVO
}

export interface ClassVO {
  id: number
  className: string
  classType: number
  classTypeName?: string
  year?: number
  startTime?: string
  endTime?: string
  teacherId?: number
  teacherName?: string
  studentCount?: number
  groupCount?: number
  workCount?: number
  status?: number
}

export interface ClassSaveRequest {
  className: string
  classType: number
  year?: number
  startTime?: string
  endTime?: string
}

export interface StudentVO {
  stuId: number
  stuName: string
  phone?: string
  college?: string
  adminClassName?: string
  classId?: number
  className?: string
  teachingClassName?: string
  groupId?: number
  groupName?: string
  status?: number
}

export interface StudentSaveRequest {
  stuId: number
  stuName: string
  phone?: string
  password?: string
  college?: string
  adminClassName?: string
  classId?: number
  teachingClassName?: string
  groupId?: number
}

export interface GroupVO {
  id: number
  classId: number
  className?: string
  groupName: string
  groupNum?: number
  teachingClassName?: string
  members?: string
  memberStuIds?: number[]
  memberNames?: string
}

export interface GroupSaveRequest {
  classId: number
  groupName: string
  teachingClassName?: string
  memberStuIds?: number[]
}

export interface WorkVO {
  id: number
  classId: number
  className?: string
  workName: string
  status: number
  statusName?: string
  startTime?: string
  endTime?: string
  detailUrl?: string
  groupTotal?: number
  groupSubmitted?: number
  createdAt?: string
  updatedAt?: string
}

export interface WorkDetailVO extends WorkVO {
  contentHtml?: string
  contentJson?: string
}

export interface WorkSaveRequest {
  classId: number
  workName: string
  contentHtml?: string
  contentJson?: string
  detailUrl?: string
  startTime?: string
  endTime?: string
}

export interface SubmissionSummaryVO {
  id: number
  teacherWorkId: number
  groupId: number
  groupName?: string
  groupNum?: number
  members?: string
  score?: number | null
  submitStatus?: number
  submitStatusName?: string
  submittedAt?: string
  updatedAt?: string
}

export interface SubmissionDetailVO {
  id: number
  teacherWorkId: number
  groupId: number
  groupName?: string
  groupNum?: number
  members?: string
  workDoc?: string
  videoUrl?: string
  zipUrl?: string
  score?: number | null
  submitStatus?: number
  submitStatusName?: string
  submittedAt?: string
  updatedAt?: string
}

export interface SubmissionSaveRequest {
  workDoc?: string
  videoUrl?: string
  zipUrl?: string
  submit?: boolean
}

export interface GreatWorkVO {
  id: number
  stuWorkId: number
  workName: string
  photoUrl?: string
  classType?: number
  year?: number
  members?: string
  createdAt?: string
}

export interface GreatWorkDetailVO extends GreatWorkVO {
  workDoc?: string
  videoUrl?: string
  zipUrl?: string
  score?: number | null
  className?: string
  groupName?: string
  groupNum?: number
  submittedAt?: string
}

export interface GreatWorkCreateRequest {
  workName: string
  photoUrl?: string
  classType?: number
  year?: number
}

export interface GreatWorkUpdateRequest {
  workName?: string
  photoUrl?: string
  classType?: number
}

export interface EquipmentVO {
  id: number
  name: string
  photoUrl?: string
  infoUrl?: string
  totalNumber: number
  usedNumber: number
  availableNumber: number
  status?: number
}

export interface EquipmentSaveRequest {
  name: string
  photoUrl?: string
  infoUrl?: string
  totalNumber: number
}

export interface BorrowVO {
  id: number
  equipmentId: number
  equipmentName?: string
  stuId?: number
  stuName?: string
  num: number
  status: number
  statusName?: string
  applyTime?: string
  approveTime?: string
  returnTime?: string
  availableNumber?: number
  operatorId?: number
  remark?: string
}

export interface FileVO {
  id: number
  url: string
  name?: string
  size?: number
  contentType?: string
  createdAt?: string
}

export interface ClassStatsVO {
  [key: string]: any
}

export interface EquipmentStatsVO {
  [key: string]: any
}

export interface AttendanceStatsVO {
  [key: string]: any
}
