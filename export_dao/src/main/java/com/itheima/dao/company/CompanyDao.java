package com.itheima.dao.company;

import com.itheima.domain.company.Company;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CompanyDao {

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

/*    // 查询
    Long findTotal();

    // 分页查询
    List<Company> findList(@Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);*/
}
