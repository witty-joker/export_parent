package com.itheima.service.system.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.dao.system.RoleDao;
import com.itheima.domain.system.Role;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.service.system.RoleService;

import java.util.List;

// 角色
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Override
    public List<Role> findAll(String companyId) {
        return roleDao.findAll(companyId);
    }

    @Override
    public void save(Role role) {
        roleDao.save(role);
    }

    @Override
    public Role findById(String id) {
        return  roleDao.findById(id);
    }

    @Override
    public void update(Role role) {
        roleDao.update(role);
    }

    @Override
    public void delete(String id) {
        roleDao.delete(id);
    }

    public PageInfo<Role> findByPage(String companyId,int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Role> list = roleDao.findAll(companyId);
        return new PageInfo<Role>(list,5);
    }

    //  查询 pe_role_module 中间表
    @Override
    public List<String> roleModelByRoleId(String roleId) {
        return roleDao.roleModelByRoleId(roleId);
    }

    // 角色分配权限
    @Override
    public void changeModule(String roleId, String[] moduleIds) {
        /*
        获取到了roleId(角色id) 和 权限
            1 删除该角色所有的权限
            2 将新权限添加
         */
        // 1 删除该角色所有的权限
        roleDao.deleteRoleModuleByRoleId(roleId);

        // 2 将新权限添加
        if (moduleIds != null && moduleIds.length > 0) {
            for (String moduleId : moduleIds) {
                roleDao.changeModule(roleId, moduleId);
            }
        }

    }
}