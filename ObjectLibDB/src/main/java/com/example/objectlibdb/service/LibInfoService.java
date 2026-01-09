package com.example.objectlibdb.service;

import com.example.objectlibdb.entity.Lib;
import com.example.objectlibdb.entity.LibInfo;
import com.example.objectlibdb.entity.LibUse;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

@Service
public interface LibInfoService {
    //从nodeMcu上面得到对应实验室的环境信息
    //前端上传的几个数据都是用 String类型的
    void PushLibInfo(int libId,String PM,String tem,String hum) throws ParseException;

    //获取实验室的环境信息--当前
    LibInfo GetLibInfo(int libId);

    //获取该实验室长期的环境信息
    List<LibInfo> GetAllLibInfo(int libId);

    //获得使用的人
    LibUse GetLibUsr(int libId);

}
