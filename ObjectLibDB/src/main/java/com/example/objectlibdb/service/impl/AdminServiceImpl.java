package com.example.objectlibdb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.objectlibdb.entity.Admin;
import com.example.objectlibdb.entity.Card;
import com.example.objectlibdb.entity.Lib;
import com.example.objectlibdb.entity.Usr;
import com.example.objectlibdb.mapper.AdminMapper;
import com.example.objectlibdb.mapper.LibMapper;
import com.example.objectlibdb.mapper.UsrMapper;
import com.example.objectlibdb.service.AdminService;
import com.example.objectlibdb.service.impl.ex.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminMapper adminMapper;
    @Autowired
    UsrMapper usrMapper;
    @Autowired
    LibMapper libMapper;
    @Override
    public Admin AdmLogin(String name, String password) {
        QueryWrapper<Admin> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("admin_name",name);
        Admin admin=adminMapper.selectOne(queryWrapper);
        if(admin==null){
            throw new AdminNotExistException("管理员不存在");
        }
        if (!password.equals(admin.getUsrPassword())){
            throw new PassErrException("密码错误");
        }

        return admin;
    }

    @Override
    public List<Usr> GetUsrList() {
        List<Usr>list=usrMapper.selectList(null);

        return list;
    }

    @Override
    public Usr GetUsr(int usrId) {
        QueryWrapper<Usr>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("usr_id",usrId);
        Usr usr=usrMapper.selectOne(queryWrapper);
        if (usr==null){
            throw new UsrNotExistException("用户不存在");
        }
        return usr;
    }

    @Override
    public List<Lib> GetAllLib() {
        List<Lib>list=libMapper.selectList(null);
        return list;
    }

    @Override
    public Lib GetLib(int LibId) {
        QueryWrapper<Lib> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("lib_id",LibId);
        Lib lib=libMapper.selectOne(queryWrapper);
        if(lib==null){
            throw new LibNotExistException("实验室不存在");
        }
        return lib;
    }

    @Override
    public List<Usr> GetNoCardUsr() {
        List<Usr>list=new ArrayList<>();
        QueryWrapper<Usr> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("usr_card","0");
        list=usrMapper.selectList(queryWrapper);
        return list;
    }

    @Override
    public Usr GivePermit(int UsrId, String CardId) {
        QueryWrapper<Usr>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("usr_id",UsrId);
        Usr usr=usrMapper.selectOne(queryWrapper);
        usr.setUsrCard(CardId);
        usrMapper.update(usr,queryWrapper);
        return usr;
    }

    @Override
    public String getCardId(String CardInfo) {
        Card card=new Card();
        card.setCardInfo(CardInfo);
        card.setStatus(0);
        return CardInfo;
    }
}
