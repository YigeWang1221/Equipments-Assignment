package com.example.objectlibdb.controller;

import com.example.objectlibdb.entity.Lib;
import com.example.objectlibdb.entity.LibInfo;
import com.example.objectlibdb.entity.ResponseModel;
import com.example.objectlibdb.service.LibInfoService;
import com.example.objectlibdb.until.JsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("LibInfo")
@Api(tags = "实验室信息管理")
public class LibInfoController extends BaseController{
    @Autowired
    LibInfoService libInfoService;

    @GetMapping("/GetLibInfo")
    @ApiOperation(value = "获得实验室的信息")
    private JsonResult<LibInfo> getLibInfo(int libId){
        LibInfo libInfo=libInfoService.GetLibInfo(libId);
        return new JsonResult<LibInfo>(OK,libInfo);
    }

    @GetMapping("/GetLibInfoList")
    @ApiOperation(value = "获得实验室的所有信息")
    private JsonResult<List<LibInfo>> getLibInfoList(int libId){
        List<LibInfo>list=new ArrayList<>();
        list=libInfoService.GetAllLibInfo(libId);
        return new JsonResult<List<LibInfo>>(200,list);
    }

    @PostMapping("/PushLibInfo")
    @ApiOperation(value = "上传实验室信息")
    private JsonResult<Void> pushLibInfo(int libId,String PM,String tem,String hum) throws ParseException {
        libInfoService.PushLibInfo(libId,PM,tem,hum);
        return new JsonResult<Void>(200);
    }
}
