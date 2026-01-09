package com.example.objectlibdb.service;

//import com.example.objectlibdb.entity.Lib;
import com.example.objectlibdb.entity.Admin;
import com.example.objectlibdb.entity.Lib;
import com.example.objectlibdb.entity.Usr;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AdminService {
    //管理员登入
    Admin AdmLogin(String name, String password);

    //查看所有的用户信息
    List<Usr> GetUsrList();

    //查看单一用户信息
    Usr GetUsr(int usrId);

    //查看实验室列表
    List<Lib> GetAllLib();

    //查看单一实验室
    Lib GetLib(int LibId);

    //找到没卡的用户
    List<Usr> GetNoCardUsr();

    //给没卡的用户卡号
    Usr GivePermit(int UsrId,String CardId);

    //接收发送上来的卡号,状态设置为0
    String getCardId(String CardInfo);

}
