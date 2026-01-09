// login进入的类型
export interface UserLoginType {
  username: string
  password: string
}

// 登陆后储存的类型
export interface UserType {
  // name和id是一体的
  username: string
  password: string
  // 角色 student /teacher / guest
  role: string
  // 当前的小组id
  groupId: number
}

// 访客访问的类型 -1 请求短信
export interface GuestApplyType {
  //电话号码
  telephone: number
}
// 访客访问的类型 -2 和短信一起请求进入
export interface GuestAndMessageType {
  // 电话号码,短信验证码
  telephone: number
  Message: number
}
