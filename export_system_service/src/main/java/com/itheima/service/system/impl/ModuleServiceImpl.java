package com.itheima.service.system.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.dao.system.ModuleDao;
import com.itheima.domain.system.Module;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.service.system.ModuleService;

import java.util.List;

// 模块管理：左侧分类栏
@Service
public class ModuleServiceImpl implements ModuleService {

    @Autowired
    private ModuleDao moduleDao;

    // 查询所有
    @Override
    public List<Module> findAll() {
        return moduleDao.findAll();
    }

    // 新增
    @Override
    public void save(Module module) {
        moduleDao.save(module);
    }

    // 根据id查询
    @Override
    public Module findById(String id) {
        return moduleDao.findById(id);
    }

    //更新
    @Override
    public void update(Module module) {
        moduleDao.update(module);
    }

    // 删除
    @Override
    public void delete(String id) {
        moduleDao.delete(id);
    }

    // 分页查询
    public PageInfo<Module> findByPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Module> list = moduleDao.findAll();
        return new PageInfo<Module>(list, 5);
    }
}