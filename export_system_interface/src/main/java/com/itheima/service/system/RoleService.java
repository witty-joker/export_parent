package com.itheima.service.system;

import com.github.pagehelper.PageInfo;
import com.itheima.domain.system.Role;

import java.util.List;

// 角色
public interface RoleService {

    List<Role> findAll(String companyId);

    void save(Role role);

    Role findById(String id);

    void update(Role role);

    void delete(String id);

    PageInfo<Role> findByPage(String companyId, int pageNum, int pageSize);

    // 查询 pe_role_module
    List<String> roleModelByRoleId(String roleId);

    // 角色分配权限
    void changeModule(String roleId, String[] moduleIds);
}