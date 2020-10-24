package com.itheima.dao.system;

import com.itheima.domain.system.Module;
import org.springframework.stereotype.Component;

import java.util.List;

// 模块管理：左侧分类栏
public interface ModuleDao {
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

    // 查找权限
    List<Module> findByBelong(Integer belong);

    // 查找权限
    List<Module> findByUserId(String userId);
}