package com.itheima.service.stat;

import java.util.List;
import java.util.Map;

/*
    统计分析
 */
public interface StatService {

    // 厂家销售统计 （统计每个厂家的销售额）
    List<Map> findFactoryCharts(String companyId);

    // 产品的销量排行榜（统计产品销售数量最高的前10名）
    List<Map> findSellCharts(String companyId);

    // 系统访问压力图（每个小时访问系统的人员数量）
    List<Map> findOnlineCharts(String companyId);

    // 查询市场价最高的前10名产品（货物）（按市场价统计）
    List<Map> findPriceCharts(String companyId);

    // 统计公司内每个部门的人数
    List<Map> findIrsCharts(String companyId);

    // 统计公司内每个人签订的购销合同数
    List<Map> findContractCharts(String companyId);
}
