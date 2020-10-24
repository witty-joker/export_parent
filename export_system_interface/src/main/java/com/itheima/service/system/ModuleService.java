package com.itheima.service.system;

import com.github.pagehelper.PageInfo;
import com.itheima.domain.system.Module;

import java.util.List;

// 模块管理：左侧分类栏
public interface ModuleService {

    // 查询所有
    List<Module> findAll();

    // 新增
    void save(Module module);

    // 根据id查询
    Module findById(String id);

    // 更新
    void update(Module module);

    // 删除
    void delete(String id);

    // 分页查询
    PageInfo<Module> findByPage(int pageNum, int pageSize);
}