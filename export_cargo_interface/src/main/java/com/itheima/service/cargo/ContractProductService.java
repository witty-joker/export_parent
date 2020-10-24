package com.itheima.service.cargo;

import com.github.pagehelper.PageInfo;
import com.itheima.domain.cargo.ContractProduct;
import com.itheima.domain.cargo.ContractProductExample;

import java.util.List;
import java.util.Map;

// 货物
public interface ContractProductService {

    /**
     * 保存
     */
    void save(ContractProduct contractProduct);

    /**
     * 更新
     */
    void update(ContractProduct contractProduct);

    /**
     * 删除
     */
    void delete(String id);

    /**
     * 根据id查询
     */
    ContractProduct findById(String id);

    /*
     *   查询所有
     * */
    List<ContractProduct> findAll(ContractProductExample example);

    /**
     * 分页查询
     */
    PageInfo findByPage(int pageNum, int pageSize, ContractProductExample example);

    /**
     * 批量上传货物
     * @param list
     */
    void patchSave(List<ContractProduct> list);
}
