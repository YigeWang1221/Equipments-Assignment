package com.example.objectlibdb.controller;

import com.example.objectlibdb.entity.ResponseModel;
import com.example.objectlibdb.service.impl.ex.*;
import com.example.objectlibdb.until.JsonResult;
import io.swagger.annotations.Api;
import org.apache.tomcat.util.http.fileupload.impl.FileUploadIOException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/** 控制器类的基类 */
@Controller
@ResponseBody
public class BaseController {
    /** 操作成功的状态码 */
    public static final int OK = 200;

    /**异常分析*/
    @ExceptionHandler({ServiceException.class, FileUploadIOException.class})
    public JsonResult<Void> handleException(Throwable e) {
        JsonResult<Void> result = new JsonResult<Void>(e);
        if (e instanceof AdminNotExistException){
            result.setState(4000);
            result.setMessage("不存在该管理员");
        }
        if (e instanceof UsrNotExistException){
            result.setState(4001);
            result.setMessage("不存在该用户");
        }
        if (e instanceof UsrExistException){
            result.setState(4002);
            result.setMessage("该用户名已存在");
        }
        if (e instanceof PassErrException){
            result.setState(4003);
            result.setMessage("用户密码错误");
        }
        if (e instanceof LibNotExistException){
            result.setState(4004);
            result.setMessage("绑定的实验室不存在");
        }
        return result;
    }


    //获取当前登录CardId，参数session对象，返回id
    protected final String  getUsrCardIdFromSession(HttpSession session){
        return String.valueOf(session.getAttribute("UsrCardId").toString());
    }
    //获取当前登录用户的name，参数session，返回用户名
    protected final String getUsernameFromSession(HttpSession session){
        return session.getAttribute("UserName").toString();
    }
}
