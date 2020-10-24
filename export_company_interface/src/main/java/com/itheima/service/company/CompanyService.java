package com.itheima.service.company;

import com.github.pagehelper.PageInfo;
import com.itheima.domain.company.Company;

import java.util.List;

public interface CompanyService {

    // 新增保存
    void save(Company company);

    // 查询列表
    List<Company> findAll();

    // 修改回显
    Company findById(String id);

    // 修改
    void update(Company company);

    // 删除
    void delete(String id);

    // 分页查询
    PageInfo findByPage(Integer pageNum, Integer pageSize);
}
