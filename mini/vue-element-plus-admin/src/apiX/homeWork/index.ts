// todo api的请求地址和格式上的变化
import request from '@/axios'
import { InitStuWork, WorkItem, WorkList, StuWorkId, JudgeExist } from './types'

////////////////////////////////////////
//学生部分

// 根据小组id 取得作业列表
export const getWorkLists = (params: string) => {
  return request.get<WorkList>({ url: '', params })
}

//点击的时候判定是不是已经创建了
export const isWorkExist = (params: JudgeExist) => {
  return request.get<boolean>({ url: '', params })
}

//这里是学生作业的初始化，上传并获得初始模板
export const initStuHomeWork = (params: InitStuWork) => {
  return request.get<WorkItem>({ url: '', params })
}

//根据选择的作业列表的值，返回作业的具体内容(stuWorkId)
export const getWorkItem = (params: StuWorkId) => {
  return request.get<WorkItem>({ url: '', params })
}

// 学生提交作业接口
export const handleHomeWork = (params: WorkItem) => {
  return request.post({ url: '', params })
}

// 学生上传视频和压缩包的接口
export const uploadFiles = (params: any) => {
  return request.post<string>({ url: '', params })
}
