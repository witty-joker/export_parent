package com.itheima.service.stat.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.stat.StatDao;
import com.itheima.service.stat.StatService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class StatServiceImpl implements StatService {

    @Autowired
    private StatDao statDao;

    @Override
    public List<Map> findFactoryCharts(String companyId) {
        return statDao.findFactoryCharts(companyId);
    }

    @Override
    public List<Map> findSellCharts(String companyId) {
        return statDao.findSellCharts(companyId);
    }

    @Override
    public List<Map> findOnlineCharts(String companyId) {
        return statDao.findOnlineCharts(companyId);
    }

    // 查询市场价最高的前10名产品（货物）（按市场价统计）
    @Override
    public List<Map> findPriceCharts(String companyId) {
        return statDao.findPriceCharts(companyId);
    }

    // 统计公司内每个部门的人数
    @Override
    public List<Map> findIrsCharts(String companyId) {
        return statDao.findIrsCharts(companyId);
    }

    // 统计公司内每个人签订的购销合同数
    @Override
    public List<Map> findContractCharts(String companyId) {
        return statDao.findContractCharts(companyId);
    }
}
