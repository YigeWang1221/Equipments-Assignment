package com.example.objectlibdb.service;

import com.example.objectlibdb.entity.Usr;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public interface UsrService {

    //用户客户端的登入
    Usr Login(String usrName,String password);

    //用户的注册
    Usr usrReg(String usrName, String password);

    //用户刷卡进入
    String UsrIn(String cardId,int libId) throws ParseException;

    //用户出去（这里用的是按钮）
    String UsrOut(int libId,String name);

    //判断人是不是在实验室里面
    boolean UsrJudge(String Name);

}
