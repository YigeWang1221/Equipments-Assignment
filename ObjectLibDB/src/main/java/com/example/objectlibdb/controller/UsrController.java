package com.example.objectlibdb.controller;

import com.example.objectlibdb.entity.ResponseModel;
import com.example.objectlibdb.entity.Usr;
import com.example.objectlibdb.service.UsrService;
import com.example.objectlibdb.until.JsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.jsqlparser.statement.select.KSQLJoinWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.lang.module.ResolvedModule;
import java.text.ParseException;

@RestController
@RequestMapping("Usr")
@Api(tags = "Usr的管理操作")
public class UsrController extends BaseController{
    @Autowired
    UsrService usrService;

    @GetMapping("/logIn")
    @ApiOperation(value = "用户登陆,传入Name，Password")
    private JsonResult<Void> UsrLogin(
                                String name,
                                String password){
        Usr usr=usrService.Login(name,password);
        return new JsonResult<Void>(OK);
    }

    @PostMapping("/Reg")
    @ApiOperation(value = "用户注册,传入Name，Password")
    private JsonResult<Void> UsrReg(String name,String password){
        Usr usr=usrService.usrReg(name,password);
        return new JsonResult<Void>(200);
    }

    @PostMapping("/UsrIn")
    @ApiOperation(value = "用户进入实验室,传入CardId，lid")
    private JsonResult<Void> UsrIn(String CardId,int linId) throws ParseException {
        usrService.UsrIn(CardId,linId);
        return new JsonResult<Void>(200);
    }

    @PostMapping("/UsrOut")
    @ApiOperation(value = "用户出实验室")
    private JsonResult<Void> UsrOut(int libId,String UsrName){
        //如果人在实验室里面
        if(usrService.UsrJudge(UsrName)){
            usrService.UsrOut(libId,UsrName);
            return new JsonResult<Void>(200);
        }
        else {
            //状态201人不在实验室里面
            return new JsonResult<Void>(201);
        }
    }
}
