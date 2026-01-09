import request from '@/axios'
import { initHomeworkType, updateHomeWorkType } from '@/apiX/teacherHomework/types'

//新建作业
export const initHomework = (params: initHomeworkType) => {
  return request.post({ url: '', params })
}

//跟新作业
export const updateHomework = (params: updateHomeWorkType) => {
  return request.post({ url: '', params })
}
