package com.example.objectlibdb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.objectlibdb.entity.Card;
import com.example.objectlibdb.entity.Usr;
import com.example.objectlibdb.mapper.CardMapper;
import com.example.objectlibdb.mapper.UsrMapper;
import com.example.objectlibdb.service.CardService;
import com.example.objectlibdb.service.impl.ex.CardIdExistException;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.catalog.Catalog;
import java.util.List;

@Service
public class CardServiceImpl implements CardService {
    @Autowired
    CardMapper cardMapper;
    @Autowired
    UsrMapper usrMapper;

    @Override
    public Void InsertCardId(String CardId) {
        QueryWrapper<Card>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("cardInfo",CardId);
        List<Card>list=cardMapper.selectList(queryWrapper);
        System.out.println(list);
        if(list.size()!=0){
            //如果有了的话，那么就出问题了
            throw new CardIdExistException("Id is already existed");
        }
        Card card=new Card();
        card.setCardInfo(CardId);
        card.setStatus(0);
        cardMapper.insert(card);
        return null;
    }

    @Override
    public Void GiveItToTeach(int UsrId,String CardInfo) {
        //管理员关联到老师
        System.out.println(UsrId+" "+CardInfo);
        QueryWrapper<Usr>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("usr_id",UsrId);
        Usr usr=usrMapper.selectOne(queryWrapper);
        usr.setUsrCard(CardInfo);
        usrMapper.updateById(usr);

        QueryWrapper<Card>queryWrapper1=new QueryWrapper<>();
        queryWrapper1.eq("cardInfo",CardInfo);
        System.out.println(cardMapper.selectList(queryWrapper1));
        Card card=cardMapper.selectOne(queryWrapper1);
        card.setStatus(1);
        cardMapper.updateById(card);
        return null;
    }

    @Override
    public String GetNewCard() {
        QueryWrapper<Card> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("status",1);
        Card card=cardMapper.selectOne(queryWrapper);
        String CardId=card.getCardInfo();
        card.setStatus(2);
        cardMapper.updateById(card);
        return CardId;
    }
}
