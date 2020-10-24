package com.itheima.web.controller.cargo;


import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.lang.UUID;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.domain.cargo.*;
import com.itheima.service.cargo.ContractProductService;
import com.itheima.service.cargo.FactoryService;
import com.itheima.utils.FileUploadUtil;
import com.itheima.vo.ContractProductVo;
import com.itheima.web.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// 合同货物管理视图
@Controller
@RequestMapping("/cargo/contractProduct")
public class ContractProductController extends BaseController {

    @Reference
    private ContractProductService contractProductService;

    @Reference
    private FactoryService factoryService;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    // 查看货物
    @RequestMapping(value = "/list", name = "查看货物")
    public String list(
            @RequestParam(defaultValue = "1", name = "page") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize,
            String contractId) {
        /*
        查看货物分析：
             1 获取货物的ContractProductEx
             2 查询当前企业id、合同id下的所有货物信息
             3 保存到集合page中
             4 查询所有生产货物的厂家列表
             5 回传contractId
             6 跳转页面
         */
        // 1 获取货物的ContractProductExample
        ContractProductExample contractProductExample = new ContractProductExample();

        // 2 查询当前企业id、合同id下的所有货物信息
        ContractProductExample.Criteria criteria = contractProductExample.createCriteria();
        criteria.andContractIdEqualTo(contractId);
        criteria.andCompanyIdEqualTo(getCompanyId());

        PageInfo pageInfo = contractProductService.findByPage(pageNum, pageSize, contractProductExample);

        // 3 保存到集合page中 查询的是所有货物信息
        request.setAttribute("page", pageInfo);

        // 4 查询所有生产货物的 【厂家列表】
        FactoryExample factoryExample = new FactoryExample();
        // 查询生产货物的厂家
        factoryExample.createCriteria().andCtypeEqualTo("货物");

        List<Factory> factoryList = factoryService.findAll(factoryExample);

        request.setAttribute("factoryList", factoryList);

        // 5 回传contractId
        request.setAttribute("contractId", contractId);

        // 6 跳转页面
        return "/cargo/product/product-list";
    }

    // 新增/修改共同使用
    @RequestMapping(value = "/edit", name = "新增/修改共同使用")
    public String edit(ContractProduct contractProduct, MultipartFile productPhoto) {
        //TODO 修改时不修改图片会出现问题。

        // 添加文件上传代码 新增会设置，修改会出问题。因此需要设置判断
        try {
            String upload = fileUploadUtil.upload(productPhoto);
            contractProduct.setProductImage(upload);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 判断是否没有数据
        if (contractProduct.getFactoryName().equals("")) {
            return "redirect:/cargo/contractProduct/list.do?contractId=" + contractProduct.getContractId();
        }

        // 判断contractProduct中id是否为空，为空就新增
        if (StringUtils.isEmpty(contractProduct.getId())) {
            // 1 添加主键
            contractProduct.setId(UUID.randomUUID().toString());

            // 2. 设置企业信息
            contractProduct.setCompanyId(getUser().getCompanyId());
            contractProduct.setCompanyName(getUser().getCompanyName());

            // 4 调用添加方法
            contractProductService.save(contractProduct);
        } else {
            // 修改
            // 调用修改update
            contractProductService.update(contractProduct);
        }

        // 共：重定向到list页面
        return "redirect:/cargo/contractProduct/list.do?contractId=" + contractProduct.getContractId();
    }

    // 跳转修改货物信息
    @RequestMapping(value = "/toUpdate", name = "编辑合同内容")
    public String toUpdate(String id) {
        /*
            编辑合同内容分析：
                1 根据id获取合同内容
                2 将合同内容回显到页面
                3 跳转到合同编辑页面
         */
        // 1 根据id获取合同内容
        ContractProduct contractProduct = contractProductService.findById(id);

        // 2 将合同内容回显到页面
        request.setAttribute("contractProduct", contractProduct);

        // 3 获取所有生产厂家
        FactoryExample factoryExample = new FactoryExample();
        factoryExample.createCriteria().andCtypeEqualTo("货物");
        List<Factory> factoryList = factoryService.findAll(factoryExample);

        request.setAttribute("factoryList", factoryList);

        // 3 跳转到合同编辑页面
        return "/cargo/product/product-update";
    }

    // 删除货物
    @RequestMapping(value = "/delete", name = "删除货物信息")
    public String delete(String id, String contractId) {
        /*
            直接删除：业务逻辑放入service中。
            id用来删除，contractId用来返回到list页面上。
         */
        // 删除
        contractProductService.delete(id);

        // 重定向到list页面
        return "redirect:/cargo/contractProduct/list.do?contractId=" + contractId;
    }

    // 跳转上传货物页面
    @RequestMapping(value = "/toImport", name = "跳转上传货物信息页面")
    public String toImport(String contractId) {

        // 保存合同id
        request.setAttribute("contractId", contractId);

        // 请求转发到list页面
        return "/cargo/product/product-import";
    }

   /* // 上传货物信息表
    @RequestMapping(value = "/toImport", name = "上传货物信息")
    public String imports(String contractId, MultipartFile file) throws IOException {
        *//*
            批量上传货物信息分析：
                1 获取上传文件的工作簿
                2 获取工作表
                3 遍历工作表中的行
                4 遍历表中行的单元格
                5 跳转页面
         *//*
        // 1 获取上传文件的工作簿
        Workbook workbook = new XSSFWorkbook(file.getInputStream());

        // 2 通过工作簿获取工作表
        Sheet sheet = workbook.getSheetAt(0);

        // 2.1 设置保存表数据的集合
        List<ContractProduct> list = new ArrayList<>();

        // 3 遍历工作表中的的行[第一行为表头， 非数据，不要]
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            // 4 根据工作表获取行
            Row row = sheet.getRow(i);

            // 4.1 设置数组保存每行数据
            Object[] objs = new Object[9];

            // 5 遍历每行的单元格
            for (int j = 1; j < row.getLastCellNum(); j++) {
                // 6 根据行获取单元格
                Cell cell = row.getCell(j);

                // 7 *  将单元格中的数据保存到数组中
                objs[j - 1] = getCellValue(cell);
            }

            // 8 通过有参构造，获取ContractProduct对象
            ContractProduct contractProduct = new ContractProduct(objs);

            // 9 * 设置 其他参数
            contractProduct.setId(UUID.randomUUID().toString());
            contractProduct.setContractId(contractId);
            contractProduct.setCompanyId(getCompanyId());
            contractProduct.setCompanyName(getCompanyName());

            // 获取工厂ID
            FactoryExample factoryExample = new FactoryExample();
            factoryExample.createCriteria().andFactoryNameEqualTo("升华");
            List<Factory> factories = factoryService.findAll(factoryExample);
            contractProduct.setFactoryId(factories.get(0).getId());

            // 9 将对象保存到集合中
            list.add(contractProduct);

        }

        // 在service中设置保存方法
        contractProductService.patchSave(list);

        // 重定向到list页面
        return "redirect:/cargo/contractProduct/list.do?contractId=" + contractId;
    }

    //解析每个单元格的数据
    public static Object getCellValue(Cell cell) {
        Object obj = null;
        CellType cellType = cell.getCellType(); //获取单元格数据类型
        switch (cellType) {
            case STRING: {
                obj = cell.getStringCellValue();//字符串
                break;
            }
            //excel默认将日期也理解为数字
            case NUMERIC: {
                if (DateUtil.isCellDateFormatted(cell)) {
                    obj = cell.getDateCellValue();//日期
                } else {
                    obj = cell.getNumericCellValue(); // 数字
                }
                break;
            }
            case BOOLEAN: {
                obj = cell.getBooleanCellValue(); // 布尔
                break;
            }
            default: {
                break;
            }
        }
        return obj;
    }*/


    // 使用EasyPoi上传货物信息表
    @RequestMapping(value = "/import", name = "上传货物信息")
    public String easyPoiImport(String contractId, MultipartFile file) throws Exception {
        /*
            批量上传货物信息分析：
                1 解析excel的配置参数
                2 获取标题，索引
                3 实现解析
                4 存储数据库
                5 跳转页面
         */
        // 1 解析excel的配置参数
        ImportParams params = new ImportParams();

        // 2 获取标题，索引
        params.setTitleRows(1); // 大标题
        params.setHeadRows(1);  // 列索引行

        // 3 实现解析
        List<ContractProduct> list = ExcelImportUtil.importExcel(file.getInputStream(), ContractProduct.class, params);

        // 3.3 遍历添加其他数据
        for (ContractProduct contractProduct : list) {
            // 9 * 设置 其他参数
            contractProduct.setId(UUID.randomUUID().toString());
            contractProduct.setContractId(contractId);
            contractProduct.setCompanyId(getCompanyId());
            contractProduct.setCompanyName(getCompanyName());

            // 获取工厂ID
            FactoryExample factoryExample = new FactoryExample();
            factoryExample.createCriteria().andFactoryNameEqualTo(contractProduct.getFactoryName());
            List<Factory> factories = factoryService.findAll(factoryExample);
            contractProduct.setFactoryId(factories.get(0).getId());
        }

        // 4 在service中设置保存方法
        contractProductService.patchSave(list);

        // 5 重定向到list页面
        return "redirect:/cargo/contractProduct/list.do?contractId=" + contractId;
    }

}
