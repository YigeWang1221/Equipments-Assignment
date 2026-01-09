// 这里是展示优秀作业的平台
import request from '@/axios'
import {
  GreatWorkList,
  GetGreatWorkParam,
  GreatWorkItem,
  GetGreatWorkItemParam,
  GetYears
} from './types'

//请求列表的参数
export const getGreatWorkList = (params: GetGreatWorkParam) => {
  return request.get<GreatWorkList>({ url: '', params })
}

//请求文件的主题
export const getGreatWorkItem = (params: GetGreatWorkItemParam) => {
  return request.get<GreatWorkItem>({ url: '', params })
}

//请求年份的列表
export const getyearsList = () => {
  return request.get<GetYears>({ url: '' })
}
