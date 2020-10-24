package com.itheima.service.cargo.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.dao.cargo.ContractDao;
import com.itheima.dao.cargo.ContractProductDao;
import com.itheima.dao.cargo.ExtCproductDao;
import com.itheima.domain.cargo.Contract;
import com.itheima.domain.cargo.ContractProduct;
import com.itheima.domain.cargo.ExtCproduct;
import com.itheima.domain.cargo.ExtCproductExample;
import com.itheima.service.cargo.ExtCproductService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class ExtCproductServiceImpl implements ExtCproductService {

    @Autowired
    private ExtCproductDao extCproductDao;

    @Autowired
    private ContractProductDao contractProductDao;

    @Autowired
    private ContractDao contractDao;

    // 添加附件
    @Override
    public void save(ExtCproduct extCproduct) {
        // ==================查询====================
        // 0 查询合同信息
        Contract contract = contractDao.selectByPrimaryKey(extCproduct.getContractId());

        // 1 查询当前货物信息
        ContractProduct contractProduct = contractProductDao.selectByPrimaryKey(extCproduct.getContractProductId());

        // ==================附件添加====================
        // 设置附件小计
        Double amount = 0.0;
        amount = extCproduct.getCnumber() * extCproduct.getPrice();

        extCproduct.setAmount(amount);
        // 添加
        extCproductDao.insertSelective(extCproduct);

        // ==================货物修改，附件添加====================
        List<ExtCproduct> extCproducts = contractProduct.getExtCproducts();
        extCproducts.add(extCproduct);

        // 保存修改
        contractProductDao.updateByPrimaryKeySelective(contractProduct);

        // ==================合同修改====================
        // 设置附件数量
        contract.setExtNum(contract.getExtNum() + 1);

        // 设置总价
        contract.setTotalAmount(contract.getTotalAmount() + amount);

        // 设置合同
        contractDao.updateByPrimaryKeySelective(contract);
    }

    // 修改附件
    @Override
    public void update(ExtCproduct extCproduct) {
        // ==================查询====================
        // 0 查询合同信息
        Contract contract = contractDao.selectByPrimaryKey(extCproduct.getContractId());

        // 1 查询当前货物信息
        ContractProduct contractProduct = contractProductDao.selectByPrimaryKey(extCproduct.getContractProductId());

        // ==================附件修改====================
        // 设置附件小计
        Double amount = 0.0;
        amount = extCproduct.getCnumber() * extCproduct.getPrice();

        extCproduct.setAmount(amount);
        // 修改
        extCproductDao.updateByPrimaryKeySelective(extCproduct);

        // ==================货物修改，附件修改====================
        Double outAmount = 0.0;

        List<ExtCproduct> extCproducts = contractProduct.getExtCproducts();
        for (ExtCproduct cproduct : extCproducts) {
            // 删除
            if (cproduct.getId().equals(extCproduct.getId())){
                // 查询现有价格
                outAmount = cproduct.getAmount();
                // 删除原附件
                extCproducts.remove(cproduct);
                break;
            }
        }

        // 添加
        extCproducts.add(extCproduct);

        // 设置
        contractProduct.setExtCproducts(extCproducts);

        // 保存修改
        contractProductDao.updateByPrimaryKeySelective(contractProduct);

        // ==================合同修改，====================

        // 设置总价  - 原价 + 现价
        contract.setTotalAmount(contract.getTotalAmount() - outAmount + amount);

        // 设置合同
        contractDao.updateByPrimaryKeySelective(contract);
    }

    // 删除附件
    @Override
    public void delete(String id) {
        // ==================查询====================
        // 查询附件
        ExtCproduct extCproduct = extCproductDao.selectByPrimaryKey(id);

        // 0 查询合同信息
        Contract contract = contractDao.selectByPrimaryKey(extCproduct.getContractId());

        // 1 查询当前货物信息
        ContractProduct contractProduct = contractProductDao.selectByPrimaryKey(extCproduct.getContractProductId());

        // ==================附件删除====================
        // 获取附件小计
        Double amount = 0.0;
        amount = extCproduct.getAmount();

        // 删除附加
        extCproductDao.deleteByPrimaryKey(id);


        // ==================货物修改，附件删除====================

        List<ExtCproduct> extCproducts = contractProduct.getExtCproducts();
        for (ExtCproduct cproduct : extCproducts) {
            // 删除
            if (cproduct.getId().equals(extCproduct.getId())){
                // 删除原附件
                extCproducts.remove(cproduct);
                break;
            }
        }
        contractProduct.setExtCproducts(extCproducts);

        // 保存修改
        contractProductDao.updateByPrimaryKeySelective(contractProduct);

        // ==================合同修改，附件数量 价格修改====================
        // 设置附件数量 - 1
        contract.setExtNum(contract.getExtNum() - 1);

        // 设置总价  附件小计
        contract.setTotalAmount(contract.getTotalAmount() - amount);

        // 设置合同
        contractDao.updateByPrimaryKeySelective(contract);
    }

    @Override
    public ExtCproduct findById(String id) {
        return extCproductDao.selectByPrimaryKey(id);
    }

    @Override
    public List<ExtCproduct> findAll(ExtCproductExample example) {
        return extCproductDao.selectByExample(example);
    }

    @Override
    public PageInfo findByPage(int pageNum, int pageSize, ExtCproductExample example) {
        PageHelper.startPage(pageNum, pageSize);
        List<ExtCproduct> list = extCproductDao.selectByExample(example);
        return new PageInfo(list, 10);
    }
}
