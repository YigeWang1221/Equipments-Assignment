package com.example.objectlibdb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.objectlibdb.entity.LibUse;
import com.example.objectlibdb.entity.Usr;
import com.example.objectlibdb.mapper.LibInfoMapper;
import com.example.objectlibdb.mapper.LibMapper;
import com.example.objectlibdb.mapper.LibUseMapper;
import com.example.objectlibdb.mapper.UsrMapper;
import com.example.objectlibdb.service.UsrService;
import com.example.objectlibdb.service.impl.ex.CardIdNotExistException;
import com.example.objectlibdb.service.impl.ex.PassErrException;
import com.example.objectlibdb.service.impl.ex.UsrExistException;
import com.example.objectlibdb.service.impl.ex.UsrNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class UsrServiceImpl implements UsrService {
    @Autowired
    UsrMapper usrMapper;
    @Autowired
    LibUseMapper libUseMapper;
    @Override
    public Usr Login(String usrName, String password) {
        QueryWrapper<Usr> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("usr_name",usrName);
        Usr usr=usrMapper.selectOne(queryWrapper);
        if (usr==null){
            throw new UsrNotExistException("用户不存在");
        }
        System.out.println(usr.getUsrPassword());
        if(!password.equals(usr.getUsrPassword()))
            throw new PassErrException("密码错误");
        return usr;
    }

    @Override
    public Usr usrReg(String usrName, String password) {
        QueryWrapper<Usr> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("usr_name",usrName);
        Usr usr=usrMapper.selectOne(queryWrapper);
        if(usr!=null){
            throw new UsrExistException("用户名存在");
        }
        Usr usr1=new Usr();
        usr1.setUsrName(usrName);
        usr1.setUsrPassword(password);
        usrMapper.insert(usr1);
        return usr1;
    }

    @Override
    public String UsrIn(String cardId, int libId) throws ParseException {
        //先查card是不是正确
        QueryWrapper<Usr>queryWrapper1=new QueryWrapper<>();
        queryWrapper1.eq("usr_card",cardId);
        if(usrMapper.selectOne(queryWrapper1)==null){
            throw new CardIdNotExistException("not permit");
        }
        System.out.println(usrMapper.selectOne(queryWrapper1));
        System.out.println("test1");
        Usr usr=usrMapper.selectOne(queryWrapper1);
        LibUse libUse1=new LibUse();
        libUse1.setLibStatus(1);
        libUse1.setLibId(libId);
        libUse1.setUsrName(usr.getUsrName());
        libUse1.setUsrId(usr.getUsrId());
        Date date=new Date();
        String strDateFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        //Date date1=sdf.parse(date.toString());
        libUse1.setLibIntime(date.toString());
        libUseMapper.insert(libUse1);
        return "UsrIn";
    }

    @Override
    public String UsrOut(int libId,String name) {
        QueryWrapper<LibUse>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("lib_id",libId)
                .eq("usr_name",name);
        //取出最新的一条数据
        //queryWrapper.last("limit 1");
        List<LibUse> libUseList=libUseMapper.selectList(queryWrapper);
        LibUse libUse=libUseList.get(libUseList.size()-1);
        System.out.println(libUse);
        libUse.setLibStatus(0);
        Date date=new Date();
        libUse.setLibOuttime(date.toString());
        libUseMapper.insert(libUse);
        return "UsrOut";
    }

    @Override
    public boolean UsrJudge(String Name) {
        QueryWrapper<LibUse>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("usr_name",Name)
                .eq("lib_status",1);
        List<LibUse>list=libUseMapper.selectList(queryWrapper);
        System.out.println(list);
        if(list.size()==0){
            return false;
        }
        else
            return true;
    }
}
