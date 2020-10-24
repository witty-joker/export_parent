package com.itheima.service.cargo.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.dao.cargo.ContractDao;
import com.itheima.dao.cargo.ContractProductDao;
import com.itheima.dao.cargo.ExtCproductDao;
import com.itheima.domain.cargo.*;
import com.itheima.service.cargo.ContractProductService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class ContractProductServiceImpl implements ContractProductService {

    @Autowired
    private ContractProductDao contractProductDao;

    @Autowired
    private ContractDao contractDao;

    @Autowired
    private ExtCproductDao extCproductDao;

    // 添加
    @Override
    public void save(ContractProduct contractProduct) {
        /*
        添加分析：
            1 查询
            2 对附件无影响
            3 对货物有影响 根据数量和单价 计算总价 Double amount;   cnumber * price
            4 对合同有影响，设置订单数量Integer proNum;  附件数量[0]Integer extNum;
                Double totalAmount;//总金额=货物的总金额+附件的总金额
         */

        // ==================查询====================
        // 1 根据id查询合同信息
        Contract contract = contractDao.selectByPrimaryKey(contractProduct.getContractId());

        // ==================附件无影响====================
        // ==================货物修改====================
        // 1 计算出 货物总价
        Double amount = 0.0;
        amount = contractProduct.getPrice() * contractProduct.getCnumber();
        contractProduct.setAmount(amount);

        // 2 保存货物信息
        contractProductDao.insertSelective(contractProduct);

        // ==================合同修改====================
        // 货物种类
        contract.setProNum(contract.getProNum() + 1);
        // 货物加订单总价
        contract.setTotalAmount(contract.getTotalAmount() + amount);

        contractDao.updateByPrimaryKeySelective(contract);

    }

    // 修改货物信息
    @Override
    public void update(ContractProduct contractProduct) {
        /*
            修改货物信息分析：
                1 查询
                2 附件 无影响
                3 货物 有影响， 修改总金额
                4 合同 有影响 修改 订单总金额
         */
        // ==================查询====================
        // 1 根据id查询合同信息
        Contract contract = contractDao.selectByPrimaryKey(contractProduct.getContractId());

        // 2 根据当前货物id查询数据库货物信息
        ContractProduct contractProductOld = contractProductDao.selectByPrimaryKey(contractProduct.getId());

        // ==================附件无影响====================
        // ==================货物修改====================
        // 1 计算出 货物总价
        Double amountOid = contractProductOld.getAmount();
        if (amountOid == null) {
            amountOid = 0.0;
        }
        Double amount = contractProduct.getPrice() * contractProduct.getCnumber();
        contractProduct.setAmount(amount);

        // 2 保存货物信息
        contractProductDao.updateByPrimaryKeySelective(contractProduct);

        // ==================合同修改====================
        // 总金额 = 总金额 - 原货物小计 + 现货物小计
        contract.setTotalAmount(contract.getTotalAmount() - amountOid + amount);

        contractDao.updateByPrimaryKeySelective(contract);
    }

    // 删除
    @Override
    public void delete(String id) {
        // ==================查询====================
        // 1 根据id查处附件信息
        ExtCproductExample extCproductExample = new ExtCproductExample();
        ExtCproductExample.Criteria criteria = extCproductExample.createCriteria();
        criteria.andContractProductIdEqualTo(id);
        List<ExtCproduct> extCproductList = extCproductDao.selectByExample(extCproductExample);

        // 2 根据id货物合同信息
        ContractProduct contractProduct = contractProductDao.selectByPrimaryKey(id);
        String contractId = contractProduct.getContractId();

        Contract contract = contractDao.selectByPrimaryKey(contractId);

        // ==================附件删除====================
        Double extAmount = 0.0;
        if (extCproductList != null) {
            for (ExtCproduct extCproduct : extCproductList) {
                extCproductDao.deleteByPrimaryKey(extCproduct.getId());
                extAmount += extCproduct.getAmount();
            }
        }

        // ==================货物删除====================
        Double amount = contractProductDao.selectByPrimaryKey(id).getAmount();
        contractProductDao.deleteByPrimaryKey(id);

        // ==================合同修改====================
        // 修改货物
        contract.setProNum(contract.getProNum() - 1);
        // 修改附件数量
        contract.setExtNum(contract.getExtNum() - extCproductList.size());
        // 修改总金额
        contract.setTotalAmount(contract.getTotalAmount() - amount - extAmount);

        // 修改合同
        contractDao.updateByPrimaryKeySelective(contract);
    }

    @Override
    public ContractProduct findById(String id) {
        return contractProductDao.selectByPrimaryKey(id);
    }

    @Override
    public List<ContractProduct> findAll(ContractProductExample example) {
        return contractProductDao.selectByExample(example);
    }

    @Override
    public PageInfo findByPage(int pageNum, int pageSize, ContractProductExample example) {
        PageHelper.startPage(pageNum, pageSize);
        List<ContractProduct> list = contractProductDao.selectByExample(example);
        return new PageInfo(list, 10);
    }

    // 批量上传货物
    @Override
    public void patchSave(List<ContractProduct> list) {

        for (ContractProduct contractProduct : list) {
            // 调用save方法 保存
            this.save(contractProduct);
        }
    }
}
