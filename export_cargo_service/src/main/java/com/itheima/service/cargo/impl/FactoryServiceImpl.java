package com.itheima.service.cargo.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.dao.cargo.FactoryDao;
import com.itheima.domain.cargo.Factory;
import com.itheima.domain.cargo.FactoryExample;
import com.itheima.service.cargo.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

// ### Service实现类代码的补全
//
// 以FactoryServiceImpl实现类演示其使用方式,
// 其它的实现类中均以写好对应的实现
@Service
public class FactoryServiceImpl implements FactoryService {

    // 一定注意Dao的所欲引入都是用@Autowired
    @Autowired
    private FactoryDao factoryDao;

    // 添加
    @Override
    public void save(Factory factory) {
        factoryDao.insertSelective(factory);
    }

    // 更新
    @Override
    public void update(Factory factory) {
        factoryDao.updateByPrimaryKeySelective(factory);
    }

    // 根据id删除
    @Override
    public void delete(String id) {
        factoryDao.deleteByPrimaryKey(id);
    }

    // 根据id查询
    @Override
    public Factory findById(String id) {
        return factoryDao.selectByPrimaryKey(id);
    }

    // 查询所有
    @Override
    public List<Factory> findAll(FactoryExample example) {
        return factoryDao.selectByExample(example);
    }

    // 分页查询
    @Override
    public PageInfo findByPage(int pageNum, int pageSize, FactoryExample example) {
        PageHelper.startPage(pageNum, pageSize);
        List<Factory> factories = factoryDao.selectByExample(example);

        return new PageInfo(factories, 10);
    }
}
