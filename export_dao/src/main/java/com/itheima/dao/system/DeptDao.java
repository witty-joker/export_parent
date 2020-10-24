package com.itheima.dao.system;

import com.itheima.domain.system.Dept;

import java.util.List;

// deptDao 部门管理
public interface DeptDao {

    // companyId 此条件是用做做企业隔离的
    // 查询该企业所有部门
    List<Dept> findAll(String companyId);

    // 添加
    void save(Dept dept);

    // 根据id查询
    Dept findById(String id);

    // 修改
    void update(Dept dept);

    // 删除
    void deleteById(String id);

    // 删除部门验证是否有子
    List<Dept> findChildrenDept(String id);
}
