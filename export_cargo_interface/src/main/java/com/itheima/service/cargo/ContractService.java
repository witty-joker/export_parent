package com.itheima.service.cargo;

import com.github.pagehelper.PageInfo;
import com.itheima.domain.cargo.Contract;
import com.itheima.domain.cargo.ContractExample;
import com.itheima.vo.ContractProductVo;

import java.util.List;
import java.util.Map;

// 购销合同
public interface ContractService {

	//根据id查询
    Contract findById(String id);

    //保存
    void save(Contract contract);

    //更新
    void update(Contract contract);

    //删除
    void delete(String id);

    /*
     *   查询所有
     * */
    List<Contract> findAll(ContractExample example);


    //分页查询
	public PageInfo findByPage(int pageNum, int pageSize, ContractExample example);

	// 1 根据输入的参数查询货物列表
    List<ContractProductVo> findContractProductVo(String inputDate, String companyId);
}
