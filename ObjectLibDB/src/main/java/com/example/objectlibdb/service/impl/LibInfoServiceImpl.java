package com.example.objectlibdb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.objectlibdb.entity.Lib;
import com.example.objectlibdb.entity.LibInfo;
import com.example.objectlibdb.entity.LibUse;
import com.example.objectlibdb.mapper.LibInfoMapper;
import com.example.objectlibdb.mapper.LibMapper;
import com.example.objectlibdb.mapper.LibUseMapper;
import com.example.objectlibdb.service.LibInfoService;
import com.example.objectlibdb.service.impl.ex.LibEmptyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class LibInfoServiceImpl implements LibInfoService {
    @Autowired
    LibInfoMapper libInfoMapper;
    @Autowired
    LibUseMapper libUseMapper;

    @Override
    public void PushLibInfo(int libId, String PM, String tem, String hum) throws ParseException {
        LibInfo libInfo=new LibInfo();
        libInfo.setLibId(String.valueOf(libId));
        libInfo.setLibPm(PM);
        libInfo.setLibTemp(tem);
        libInfo.setLibHum(hum);
        Date date=new Date();
        String strDateFormat = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        System.out.println(date.toString());
        //Date date1=sdf.parse(date.toString());
        //Date date2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(date.toString());
        libInfo.setGetTime(date.toString());
        libInfoMapper.insert(libInfo);
        return ;
    }

    @Override
    public LibInfo GetLibInfo(int libId) {
        //获得最新一条数据
        QueryWrapper<LibInfo> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("lib_id",String.valueOf(libId));
//        queryWrapper.last("limit 1");
        List<LibInfo> libInfo= libInfoMapper.selectList(queryWrapper);
        int size=libInfo.size();
        System.out.println(libInfo.get(size-1));
        return libInfo.get(size-1);
    }

    @Override
    public List<LibInfo> GetAllLibInfo(int libId) {
        QueryWrapper<LibInfo> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("lib_id",String.valueOf(libId));
        List<LibInfo> list=libInfoMapper.selectList(queryWrapper);
        return list;
    }

    @Override
    public LibUse GetLibUsr(int libId) {
        QueryWrapper<LibUse>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("lib_id",libId)
                .eq("lib_status",1);
        if(libUseMapper.selectList(queryWrapper).size()==0){
            LibUse libUse=new LibUse();
            libUse.setLibId(libId);
            libUse.setUsrName("null");
            return libUse;
        }
        List<LibUse> libUse=libUseMapper.selectList(queryWrapper);
        return libUse.get(libUse.size()-1);
    }
}
