package com.itheima.service.cargo.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.dao.cargo.*;
import com.itheima.domain.cargo.*;
import com.itheima.service.cargo.ExportService;
import com.itheima.vo.ExportProductResult;
import com.itheima.vo.ExportProductVo;
import com.itheima.vo.ExportResult;
import com.itheima.vo.ExportVo;
import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ExportServiceImpl implements ExportService {

    @Autowired
    private ExportDao exportDao; // 报运dao

    @Autowired
    private ExportProductDao exportProductDao;//报运单商品dao

    @Autowired
    private ExtEproductDao extEproductDao;  //报运单附件Dao

    @Autowired
    private ContractDao contractDao; // 合同dao

    @Autowired
    private ContractProductDao contractProductDao; //合同货物dao

    @Autowired
    private ExtCproductDao extCproductDao;  //合同附件Dao

    //保存
    public void save(Export export) {
        /*
            报运单新增保存分析：
                // ==========保存报运单=========
                1 根据打断字段获取合同id列表
                2 查询多个合同组成合同集合
                3 创建可变字符串
                4 遍历保存报运货物列表到可变字符串
                5 遍历保存货物数量
                6 遍历保存附件数量
                7 调用dao保存报运单

                // =======复制保存货物到出口货物单=======
                a 根据合同id集合查询货物列表
                b 遍历保存货物信息
                    1 复制货物信息
                    2 添加id
                    3 设置报运单id

                // =======复制保存附件到出口附件单=======
                a 根据合同id集合查询附件列表
                b 遍历保存负附件信息
                    1 复制货物信息
                    2 添加id
                    3 设置报运单id

         */
        // ==========保存报运单=========
        // 1 根据打断字段获取合同id列表
        List<String> contractIdList = Arrays.asList(export.getContractIds().split(","));

        // 2 查询多个合同组成合同集合
        ContractExample contractExample = new ContractExample();
        contractExample.createCriteria().andIdIn(contractIdList);
        List<Contract> contractList = contractDao.selectByExample(contractExample);

        // 3 创建可变字符串
        StringBuilder sb = new StringBuilder();
        Integer proNum = 0;
        Integer extNum = 0;

        // 4 遍历保存报运货物列表到可变字符串
        for (Contract contract : contractList) {
            // ContractNo 合同号，订单号
            sb.append(contract.getContractNo()).append(" ");
            // 5 遍历保存货物数量
            proNum += contract.getProNum();
            // 6 遍历保存附件数量
            extNum += contract.getExtNum();
        }

        export.setCustomerContract(sb.toString());
        export.setProNum(proNum);
        export.setExtNum(extNum);

        // 7 调用dao保存报运单
        exportDao.insertSelective(export);

        // =======复制保存货物到出口货物单=======
        // a 根据合同id集合查询货物列表
        ContractProductExample contractProductExample = new ContractProductExample();
        contractProductExample.createCriteria().andContractIdIn(contractIdList);
        List<ContractProduct> contractProductList = contractProductDao.selectByExample(contractProductExample);

        // b 遍历保存货物信息
        // 1 复制货物信息
        for (ContractProduct contractProduct : contractProductList) {
            ExportProduct exportProduct = new ExportProduct();
            BeanUtils.copyProperties(contractProduct, exportProduct);

            // 2 添加id
            exportProduct.setId(UUID.randomUUID().toString());

            // 3 设置报运单id
            exportProduct.setExportId(export.getId());

            // 保存 货物信息
            exportProductDao.insertSelective(exportProduct);
        }

        // =======复制保存附件到出口附件单=======
        // a 根据合同id集合查询附件列表
        ExtCproductExample extCproductExample = new ExtCproductExample();
        extCproductExample.createCriteria().andContractIdIn(contractIdList);
        List<ExtCproduct> extCproductList = extCproductDao.selectByExample(extCproductExample);

        // b 遍历保存负附件信息

        for (ExtCproduct extCproduct : extCproductList) {
            // 1 复制货物信息
            ExtEproduct extEproduct = new ExtEproduct();
            BeanUtils.copyProperties(extCproduct, extEproduct);

            // 2 添加id
            extEproduct.setId(UUID.randomUUID().toString());

            // 3 设置报运单id
            extEproduct.setExportId(export.getId());

            // 保存附件信息
            extEproductDao.insertSelective(extEproduct);
        }
    }

    //更新
    public void update(Export export) {
        /*
            报运单更新分析：
                1 修改报运单信息
                2 修改报运单下货物信息
         */
        // 1 修改报运单信息
        exportDao.updateByPrimaryKeySelective(export);

        // 2 修改报运单下货物信息
        List<ExportProduct> exportProductList = export.getExportProducts();
        for (ExportProduct exportProduct : exportProductList) {
            exportProductDao.updateByPrimaryKeySelective(exportProduct);
        }
    }

    //删除
    public void delete(String id) {
        /*
            删除报运单分析：
                1 根据报运单id删除附件信息
                2 根据报运单id删除货物信息
                3 删除报运单信息
         */
        // 1 根据报运单id删除附件信息
        ExtEproductExample extEproductExample = new ExtEproductExample();
        extEproductExample.createCriteria().andExportIdEqualTo(id);
        List<ExtEproduct> eproductList = extEproductDao.selectByExample(extEproductExample);
        // 遍历删除
        for (ExtEproduct extEproduct : eproductList) {
            extEproductDao.deleteByPrimaryKey(extEproduct.getId());
        }

        // 2 根据报运单id删除货物信息
        ExportProductExample exportProductExample = new ExportProductExample();
        exportProductExample.createCriteria().andExportIdEqualTo(id);
        List<ExportProduct> exportProductList = exportProductDao.selectByExample(exportProductExample);
        for (ExportProduct exportProduct : exportProductList) {
            exportProductDao.deleteByPrimaryKey(exportProduct.getId());
        }

        // 3 删除报运单信息
        exportDao.deleteByPrimaryKey(id);
    }

    //根据id查询
    public Export findById(String id) {
        return exportDao.selectByPrimaryKey(id);
    }

    //分页
    public PageInfo findByPage(int pageNum, int pageSize, ExportExample example) {
        PageHelper.startPage(pageNum, pageSize);
        List<Export> list = exportDao.selectByExample(example);
        return new PageInfo(list);
    }

    // 海关报运
    @Override
    public void exportE(String id) {
        /*
            海关报运分析：
                1 查询报运单信息，封装为ExportVo
                2 查询报运单下的货物信息，封装为ExportProductVo
                3 调用海关平台，然后将ExportVo发送出去
                4 修改单签报运单的状态(0-草稿， 1-已上报， 2-已报运)
         */
        // 1 查询报运单信息，封装为ExportVo 海关报运单格式
        // a 获取报运单
        Export export = exportDao.selectByPrimaryKey(id);

        // b 将报运单封装到海关报运单格式
        ExportVo exportVo = new ExportVo();
        BeanUtils.copyProperties(export, exportVo);
        exportVo.setExportId(id);   // 报运单id
        exportVo.setExportDate(new Date()); // 报运申报时间


        // 2 查询报运单下的货物信息，封装为ExportProductVo
        ExportProductExample exportProductExample = new ExportProductExample();
        ExportProductExample.Criteria criteria = exportProductExample.createCriteria();
        criteria.andExportIdEqualTo(id);

        List<ExportProduct> exportProductList = exportProductDao.selectByExample(exportProductExample);
        for (ExportProduct exportProduct : exportProductList) {
            ExportProductVo exportProductVo = new ExportProductVo();

            // 将数据封装到海关货物清单
            BeanUtils.copyProperties(exportProduct, exportProductVo);
            exportProductVo.setExportProductId(exportProduct.getId());
            exportVo.getProducts().add(exportProductVo);
        }

        // 3 调用海关平台，然后将ExportVo发送出去  post添加
        WebClient.create("http://localhost:5003/ws/export/user").post(exportVo);

        // 4 修改单签报运单的状态(0-草稿， 1-已上报， 2-已报运)
        export.setState(1);
        // 更新到数据库
        exportDao.updateByPrimaryKeySelective(export);
    }

    // 查询报运结果
    @Override
    public void findExportResult(String id) {

        /*
            查询报运结果分析：
                1 根据id以及报运地址URL获取电子报运信息
                2 根据报运单信息修改报运单信息
                3 根据电子报运信息修改报运货物集合信息
         */
        try {
            // 1 根据id以及报运地址URL获取电子报运信息
            ExportResult exportResult = WebClient.create("http://localhost:5003/ws/export/user/" + id).get(ExportResult.class);

            // 2 根据报运单信息修改报运单信息
            Export export = new Export();
            export.setId(id);
            export.setState(exportResult.getState());
            export.setRemark(exportResult.getRemark());
            // 更新
            exportDao.updateByPrimaryKeySelective(export);

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
