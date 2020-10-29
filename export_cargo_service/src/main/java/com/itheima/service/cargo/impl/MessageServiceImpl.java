package com.itheima.service.cargo.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.dao.cargo.MessageDao;
import com.itheima.domain.cargo.Contract;
import com.itheima.domain.cargo.Message;
import com.itheima.domain.cargo.MessageExample;
import com.itheima.service.cargo.MessageService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageDao messageDao;

    // 分页查询
    @Override
    public PageInfo findByPage(Integer pageNum, Integer pageSize, MessageExample messageExample) {
        PageHelper.startPage(pageNum, pageSize);
        List<Message> list = messageDao.selectByExample(messageExample);
        return new PageInfo(list, 10);
    }

}
