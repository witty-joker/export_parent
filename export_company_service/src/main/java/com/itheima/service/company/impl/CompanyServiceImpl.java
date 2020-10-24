package com.itheima.service.company.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.dao.company.CompanyDao;
import com.itheima.domain.company.Company;
import com.itheima.service.company.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service    // 使用dubbo的
public class CompanyServiceImpl implements CompanyService {

    // 使用spring的
    @Autowired
    private CompanyDao companyDao;

    // 新增保存
    @Override
    public void save(Company company) {
        companyDao.save(company);
    }

    // 查询所有
    @Override
    public List<Company> findAll() {
        return companyDao.findAll();
    }

    // 修改回显
    @Override
    public Company findById(String id) {
        return companyDao.findById(id);
    }

    // 修改
    @Override
    public void update(Company company) {
        companyDao.update(company);
    }

    // 删除
    @Override
    public void delete(String id) {
        companyDao.delete(id);
    }

    /*// 分页查询
    @Override
    public PageInfo findByPage(Integer pageNum, Integer pageSize) {
        // 1 查询total ，总记录数 findTotal
        Long total = companyDao.findTotal();

        // 2 查询list，数据集合 findList (开始索引， 每页个数)
        Integer startIndex = (pageNum - 1) * pageSize;
        List<Company> list = companyDao.findList(startIndex, pageSize);

        return new PageInfo(pageNum, pageSize, list, total);
    }*/

    // 分页查询
    @Override
    public PageInfo findByPage(Integer pageNum, Integer pageSize) {
        // 1 设置pageNum和pageSize
        PageHelper.startPage(pageNum, pageSize);

        // 2 调用一个查询所有方法
        List<Company> list = companyDao.findAll();

        // 3 直接返回PageInfo   list 数据，10是页码 前5后4；如果不写默认8 前4后3
        return new PageInfo(list, 8);
    }

}
