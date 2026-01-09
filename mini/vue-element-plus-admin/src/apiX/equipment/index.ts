import request from '@/axios'
import type {
  EquipmentType,
  newEquipmentType,
  backEquipmentType,
  borrowEquipmentType,
  PostFileType
} from '@/apiX/equipment/types'

/////////////////////////////////////////
//请求所有的器材
export const getEquipments = () => {
  return request.get<{
    code: string
    data: {
      list: EquipmentType[]
    }
  }>({ url: '' })
}
//学生借用器材
export const borrowEquipmen = (params: borrowEquipmentType) => {
  return request.post({ url: '', params })
}

//教师确认学生归还成功
export const returnEquipment = (params: backEquipmentType) => {
  return request.post({ url: '', params })
}

// 教师新建器材
export const newEquipment = (params: newEquipmentType) => {
  return request.post({ url: '', params })
}

// 教师更新器材的内容
export const updateEquipment = (params: EquipmentType) => {
  // 没有update类型的
  return request.post({ url: '', params })
}

//////////////////////////////////////////////////////////////////
// 下载附带的资料，这个怎么办
// 下载的时候直接使用downloadfile的自带方法实现
// downloadFile() {
//   const fileUrl = 'http://your-minio-url.com/path/to/file.zip'; // 替换为 MinIO 文件的 HTTP 链接
//   window.open(fileUrl, '_blank');
export const PostFile = (params: PostFileType) => {
  // 没有update类型的
  return request.post({ url: 'http://127.0.0.1:8090/upload', params })
}
