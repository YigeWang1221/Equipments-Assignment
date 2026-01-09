package com.example.objectlibdb.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.objectlibdb.entity.*;
import com.example.objectlibdb.mapper.CardMapper;
import com.example.objectlibdb.service.AdminService;
import com.example.objectlibdb.service.CardService;
import com.example.objectlibdb.service.LibInfoService;
import com.example.objectlibdb.until.JsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.ls.LSException;
import reactor.netty.udp.UdpServer;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("Admin")
@Api(tags = "Admin的管理操作")
public class AdminController extends BaseController{
    @Autowired
    AdminService adminService;
    @Autowired
    CardService cardService;
    @Autowired
    LibInfoService libInfoService;

    @GetMapping("/Login")
    @ApiOperation(value = "管理员登陆")
    private JsonResult<Void> AdminLogin(
                                  String name ,
                                  String password){
        Admin admin=adminService.AdmLogin(name,password);
        return new JsonResult<Void>(OK);
    }

    @GetMapping("/GetAllUsr")
    @ApiOperation(value = "获得所有的usr列表")
    private JsonResult<List<Usr>> GetUsrList(){
        List<Usr>list=new ArrayList<>();
        list=adminService.GetUsrList();
        return new JsonResult<List<Usr>>(OK,list);
    }

    @GetMapping("/GetNoCardUsrList")
    @ApiOperation(value = "获得没有卡的usr")
    private JsonResult<List<Usr>> GetNoCard(){
        List<Usr> list=new ArrayList<>();
        list=adminService.GetNoCardUsr();
        return new JsonResult<List<Usr>>(OK,list);
    }

    @GetMapping("/GetUsr")
    @ApiOperation(value = "通过id获得usr")
    private JsonResult<Usr> getUsr(int usrId){
        Usr usr=adminService.GetUsr(usrId);
        return new JsonResult<Usr>(OK,usr);
    }

    @PostMapping("/InsertNewCardIdAndGiveIt")
    @ApiOperation(value = "管理员手动输入卡号 卡的长度是000-0000-000 10位，位数中的-不用写")
    private JsonResult<Void> giveCardId(String CardId,int usrId){
        cardService.InsertCardId(CardId);
        cardService.GiveItToTeach(usrId,CardId);
        return new JsonResult<Void>(200);
    }

    @GetMapping("GetNewId")
    @ApiOperation("拿新id后改变status=2")
    private JsonResult<String> getNewId(){
        String CardId=cardService.GetNewCard();
        return new JsonResult<String >(200,CardId);
    }

//    @PutMapping("/GivePermit")
//    @ApiOperation(value = "给新用户卡号")
//    private JsonResult<String> givePermit(int usrId){
//        QueryWrapper<Card>queryWrapper=new QueryWrapper<>();
//        queryWrapper.eq("status1",0);
//        String CardId=cardMapper.selectOne(queryWrapper).getCardId();
//        adminService.GivePermit(usrId,CardId);
//        return new JsonResult<String>(200,CardId);
//    }

    @GetMapping("/GetLibList")
    @ApiOperation(value = "获得实验室list")
    private JsonResult<List<Lib>> getLibList(){
        List<Lib>list=new ArrayList<>();
        list=adminService.GetAllLib();
        return new JsonResult<List<Lib>>(OK,list);
    }

    @GetMapping("/GetLib")
    @ApiOperation(value = "获得实验室")
    private JsonResult<Lib> getLib(int libId){
        Lib lib=new Lib();
        lib=adminService.GetLib(libId);
        return new JsonResult<Lib>(OK,lib);
    }

    @PostMapping("/PostCardId")
    @ApiOperation(value = "通过nodeMcu上传CardId写入到数据库")
    private JsonResult<String> getCardId(String CardId){
        adminService.getCardId(CardId);
        return new JsonResult<String>(OK,CardId);
    }
    @GetMapping("/GetLibUsr")
    @ApiOperation(value = "获得在实验室里面的人")
    private JsonResult<LibUse> getLibUsr(int LibId){
        LibUse libUse=libInfoService.GetLibUsr(LibId);
        return new JsonResult<LibUse>(OK,libUse);
    }

}
