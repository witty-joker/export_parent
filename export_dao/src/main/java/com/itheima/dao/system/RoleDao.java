package com.itheima.dao.system;

import com.itheima.domain.system.Role;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

// 角色
@Component
public interface RoleDao {
    
    List<Role> findAll(String companyId);

    void save(Role role);

    Role findById(String id);

    void update(Role role);

    void delete(String id);

    // 查询 pe_role_module 中间表
    List<String> roleModelByRoleId(String roleId);

    // 1 删除该角色所有的权限
    void deleteRoleModuleByRoleId(String roleId);

    // 2 将新权限添加
    void changeModule(@Param("roleId") String roleId, @Param("moduleId") String moduleId);
}