// todo 作业功能的全部内容，现在只是大致的写一下

//////////////////////////////////////////////////////////
//学生部分
// 返回的作业列表的值
export interface WorkList {}

// 用来查询作业是否存在的值
export interface JudgeExist {}

//新建一个学生作业
export interface InitStuWork {
  test: string
}

// 返回的作业文本的值
export interface WorkItem {
  item: string
}

// stuWorkId
export interface StuWorkId {
  id: number
}
