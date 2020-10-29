package com.itheima.service.cargo;

import com.github.pagehelper.PageInfo;
import com.itheima.domain.cargo.MessageExample;

// 货物
public interface MessageService {

    // 分页查询
    PageInfo findByPage(Integer pageNum, Integer pageSize, MessageExample messageExample);
}
