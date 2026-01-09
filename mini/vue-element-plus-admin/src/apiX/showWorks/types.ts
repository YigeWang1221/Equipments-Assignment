// 这里是作业展示服务的一些参数设置

// 获得优秀作业的列表
export interface GreatWorkList {}

// 优秀作业列表的请求参数
export interface GetGreatWorkParam {}

// 优秀作业的全文展示内容
export interface GreatWorkItem {}

// 优秀作业求其全文参数
export interface GetGreatWorkItemParam {}

//获得年份的返回值，list
export interface GetYears {
  year: number[]
}
