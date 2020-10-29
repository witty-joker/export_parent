package com.itheima.dao.cargo;

import com.itheima.domain.cargo.Message;
import com.itheima.domain.cargo.MessageExample;
import java.util.List;

public interface MessageDao {
    int deleteByPrimaryKey(String mid);

    int insert(Message record);

    int insertSelective(Message record);

    List<Message> selectByExample(MessageExample example);

    Message selectByPrimaryKey(String mid);

    int updateByPrimaryKeySelective(Message record);

    int updateByPrimaryKey(Message record);
}