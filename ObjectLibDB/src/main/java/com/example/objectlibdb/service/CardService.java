package com.example.objectlibdb.service;

import org.springframework.stereotype.Service;

@Service
public interface CardService {
    //管理员给CardId 000-0000-000
    Void InsertCardId(String CardId);

    //管理员赋予个人之后改变状态
    Void GiveItToTeach(int UsrId,String CardInfo);

    //获得新卡
    String GetNewCard();

}
