package com.itheima.service.system;

import com.github.pagehelper.PageInfo;
import com.itheima.domain.system.Module;
import com.itheima.domain.system.User;

import java.util.List;

// user用户service

public interface UserService {

    // companyId 此条件是用做做企业隔离的
    // 查询该企业所有用户
    List<User> findAll(String companyId);

    // 添加
    void save(User user);

    // 根据id查询
    User findById(String id);

    // 修改
    void update(User user);

    // 删除
    void delete(String id);

    // 分页查询
    PageInfo findByPage(String companyId, Integer pageNum, Integer pageSize);

    // 跳转分配角色页面
    List<String> findRoleIdsByUserId(String id);

    // 分配角色
    void changeRole(String userId, String[] roleIds);

    // 根据email获取用户信息 返回user对象
    User findByEmail(String email);

    // 根据用户user查询对应的权限
    List<Module> findModuleByUser(User user);
}
