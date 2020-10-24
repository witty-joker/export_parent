package com.itheima.service.cargo.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.cargo.ExportDao;
import com.itheima.dao.cargo.ExportProductDao;
import com.itheima.domain.cargo.Export;
import com.itheima.domain.cargo.ExportExample;
import com.itheima.domain.cargo.ExportProduct;
import com.itheima.service.cargo.ExportJob;
import com.itheima.vo.ExportProductResult;
import com.itheima.vo.ExportResult;
import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

// 定时查询报运结果
@Service
public class ExportJobImpl implements ExportJob {

    @Autowired
    private ExportDao exportDao;

    @Autowired
    private ExportProductDao exportProductDao;

    @Override
    public void findExportResult() {
        /*
            查询报运结果分析：
                1 根据id以及报运地址URL获取电子报运信息
                2 根据报运单信息修改报运单信息
                3 根据电子报运信息修改报运货物集合信息
         */
        // 0 查询状态为1的报运单
        ExportExample exportExample = new ExportExample();
        exportExample.createCriteria().andStateEqualTo(1L);
        // 查询为1的报运单
        List<Export> exportList = exportDao.selectByExample(exportExample);

        // 遍历查询报运结果
        for (Export export : exportList) {
            try {
                // 1 根据id以及报运地址URL获取电子报运信息
                ExportResult exportResult = WebClient.create("http://localhost:5003/ws/export/user/" + export.getId()).get(ExportResult.class);

                // 2 根据报运单信息修改报运单信息
                export.setId(exportResult.getExportId());   // id
                export.setState(exportResult.getState());   // 状态
                export.setRemark(exportResult.getRemark()); // 备注
                // 更新
                exportDao.updateByPrimaryKeySelective(export);  // 动态sql才可

                // 3 根据电子报运信息修改报运货物集合信息
                for (ExportProductResult exportProductResult : exportResult.getProducts()) {
                    ExportProduct exportProduct = new ExportProduct();
                    exportProduct.setId(exportProductResult.getExportProductId());
                    exportProduct.setTax(exportProductResult.getTax());

                    // 更新
                    exportProductDao.updateByPrimaryKeySelective(exportProduct);
                }

            } catch (Exception e) {
                System.out.println("查询海关数据失败，请检查。");
            }
        }
    }
}
