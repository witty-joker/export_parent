package com.itheima.service.system;

import com.github.pagehelper.PageInfo;
import com.itheima.domain.system.Dept;

import java.util.List;

// deptService 部门service
public interface DeptService {

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

    // 分页查询
    PageInfo findByPage(String companyId, Integer pageNum, Integer pageSize);

    // 删除部门验证是否有子
    List<Dept> findChildrenDept(String id);

    // 修改id的修改dept部门
    void updateAmendId(Dept dept, int i, int j);
}
