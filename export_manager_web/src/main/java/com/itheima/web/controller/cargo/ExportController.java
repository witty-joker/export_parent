package com.itheima.web.controller.cargo;


import cn.hutool.core.bean.BeanUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.itheima.domain.cargo.ContractExample;
import com.itheima.domain.cargo.Export;
import com.itheima.domain.cargo.ExportExample;
import com.itheima.domain.cargo.ExportProduct;
import com.itheima.service.cargo.ContractService;
import com.itheima.service.cargo.ExportJob;
import com.itheima.service.cargo.ExportProductService;
import com.itheima.service.cargo.ExportService;
import com.itheima.web.controller.BaseController;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.*;

// 合同管理
@Controller
@RequestMapping("/cargo/export")
public class ExportController extends BaseController {

    // 合同对象
    @Reference
    private ContractService contractService;

    // 报运单对象
    @Reference
    private ExportService exportService;

    // 报运货物列表
    @Reference
    private ExportProductService exportProductService;

    // 定时查询报运结果
    @Reference
    private ExportJob exportJob;

    // 已提交合同查看
    @RequestMapping(value = "/contractList", name = "已提交合同查看")
    public String contractList(
            @RequestParam(defaultValue = "1", name = "page") Integer pageName,
            @RequestParam(defaultValue = "5") Integer pageSize) {
        // 获取合同用例
        ContractExample contractExample = new ContractExample();
        // 设置用例
        ContractExample.Criteria criteria = contractExample.createCriteria();
        criteria.andCompanyIdEqualTo(getCompanyId());
        criteria.andStateEqualTo(1);

        // 分页查询
        PageInfo pageInfo = contractService.findByPage(pageName, pageSize, contractExample);

        request.setAttribute("page", pageInfo);

        return "/cargo/export/export-contractList";
    }

    // 出口报运单查看
    @RequestMapping(value = "/list", name = "出口报运单查看")
    public String list(
            @RequestParam(defaultValue = "1", name = "page") Integer pageName,
            @RequestParam(defaultValue = "5") Integer pageSize) {
        // 获取报运单用例
        ExportExample exportExample = new ExportExample();
        // 设置用例
        ExportExample.Criteria criteria = exportExample.createCriteria();
        criteria.andCompanyIdEqualTo(getCompanyId());   //

        // 分页查询
        PageInfo pageInfo = exportService.findByPage(pageName, pageSize, exportExample);

        request.setAttribute("page", pageInfo);

        return "/cargo/export/export-list";
    }

    // 合同添加报运 toExport
    @RequestMapping(value = "toExport", name = "合同添加报运")
    public String toExport(String id) {

        request.setAttribute("id", id);

        return "/cargo/export/export-toExport";
    }

    // 新增/修改出口报运单
    @RequestMapping(value = "edit", name = "新增/修改出口报运单")
    public String edit(Export export) { // export 报运单对象
        /*
            新增出口报运单分析：
                1 判断是新增还是修改
                2 添加id
                3 添加制单使劲
                4 设置报运单状态， //0-草稿  1-已上报  2-已报运  3-装箱 4-委托 5-发票 6-财务
                5 调用service新增
                6 调用service修改
                7 重定向到list页面
         */
        // 1 判断是新增还是修改
        if (StringUtils.isEmpty(export.getId())) {
            // 2 添加id
            export.setId(UUID.randomUUID().toString());

            // 3 添加制单是时间
            export.setInputDate(new Date());

            // 设置企业信息
            export.setCompanyId(getCompanyId());
            export.setCompanyName(getCompanyName());

            // 4 设置报运单状态， 0 草稿 1 已上报 2 已报运
            export.setState(0);

            // 5 调用service新增
            exportService.save(export);

        } else {
            // 6 调用service修改
            exportService.update(export);
        }

        // 7 重定向到list页面
        return "redirect:/cargo/export/list.do";
    }

    // 跳转报运单编辑页面
    @RequestMapping(value = "/toUpdate", name = "跳转报运单编辑页面")
    public String toUpdate(String id) {
        // 1 根据id查询报运单信息  export
        Export export = exportService.findById(id);

        // 1.1 保存export
        request.setAttribute("export", export);

        // 2 根据报运单查询当前报运单货物信息  eps
        List<ExportProduct> exportProductList = exportProductService.findByExportId(id);

        // 3 保存货物到request
        request.setAttribute("eps", exportProductList);

        // 4 请求转发到修改页面
        return "/cargo/export/export-update";
    }

    // 跳转报运单查看  export
    @RequestMapping(value = "/toView", name = "跳转报运单编辑页面")
    public String toView(String id) {
        // 1 根据id查询报运单信息  export
        Export export = exportService.findById(id);

        // 1.1 保存export
        request.setAttribute("export", export);

        // 2 请求转发到修改页面
        return "/cargo/export/export-view";
    }

    // 删除报运单  export
    @RequestMapping(value = "/delete", name = "删除报运单")
    public String delete(String id) {
        // 1 调用service删除报运单
        exportService.delete(id);

        // 2 重定向到list.do
        return "redirect:/cargo/export/list.do";
    }

    // 电子报运
    @RequestMapping(value = "/exportE", name = "电子报运")
    public String exportE(String id) {
        // 1 使用service的电子报运
        exportService.exportE(id);

        // 2 重定向到list.do
        return "redirect:/cargo/export/list.do";
    }

    // 查询报运结果  findExportResult
    @RequestMapping(value = "/findExportResult", name = "查询报运结果")
    public String findExportResult(String id) {
        // 1 调用service查询报运结果
        exportService.findExportResult(id);

        // 定时查询报运结果
        // exportJob.findExportResult();

        // 2 重定向到list.do
        return "redirect:/cargo/export/list.do";
    }

    // 设置下载报运单
    @RequestMapping(value = "/exportPdf", name = "报运单下载")
    public void exportPdf(String id) throws JRException, IOException {
        //0. 获取模板
        String realPath = session.getServletContext().getRealPath("/jasper/export.jasper");

        //1. 获取数据 条件 报运单id    结果 报运单信息和报运单下货物信息
        //1-1) 报运单信息
        Export export = exportService.findById(id);
        Map<String, Object> map = BeanUtil.beanToMap(export);

        //1-2) 货物信息
        List<ExportProduct> list = exportProductService.findByExportId(id);
        JRDataSource dataSource = new JRBeanCollectionDataSource(list);


        //2  向模板填充数据
        JasperPrint jasperPrint = JasperFillManager.fillReport(realPath, map, dataSource);


        //3.输出到浏览器
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }

    // ============================================
    // 下载PDF 测试1  exportPdf1
    @RequestMapping(value = "/exportPdf1", name = "测试")
    public void exportPdf1(String id) throws JRException, IOException {
        //1. 获取模板
        String realPath = session.getServletContext().getRealPath("/jasper/demo1.jasper");

        // 2 向模板填充数据   sourceFileName(路径)    params(map集合)    dataSource(本质上时候list)
        HashMap<String, Object> map = new HashMap<>();
        JRDataSource dataSource = new JREmptyDataSource();
        JasperPrint jasperPrint = JasperFillManager.fillReport(realPath, map, dataSource);

        //3.输出到浏览器
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }

    // 测试2 exportPdf3
    @RequestMapping(value = "/exportPdf2", name = "测试2")
    public void exportPdf2(String id) throws JRException, IOException {
        //1. 获取模板
        String realPath = session.getServletContext().getRealPath("/jasper/demo2.jasper");

        // 2 向模板填充数据   sourceFileName(路径)    params(map集合)    dataSource(本质上时候list)
        HashMap<String, Object> map = new HashMap<>();
        map.put("username", "安妮");
        map.put("age", "17");
        map.put("address", "小熊里");
        map.put("company", "微软");

        JRDataSource dataSource = new JREmptyDataSource();
        JasperPrint jasperPrint = JasperFillManager.fillReport(realPath, map, dataSource);

        //3.输出到浏览器
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }

    // 测试3 exportPdf3
    @RequestMapping(value = "/exportPdf3", name = "测试2")
    public void exportPdf3(String id) throws JRException, IOException {
        //1. 获取模板
        String realPath = session.getServletContext().getRealPath("/jasper/demo2.jasper");

        // 2 向模板填充数据   sourceFileName(路径)    params(map集合)    dataSource(本质上时候list)
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("userName", "安妮");
            map.put("email", "17@qq.com");
            map.put("deptName", "小熊里");
            map.put("companyName", "微软");

            // 添加到list
            list.add(map);
        }

        // 将list转换为JRDataSource
        JRDataSource dataSource = new JRBeanCollectionDataSource(list);

        JasperPrint jasperPrint = JasperFillManager.fillReport(realPath, new HashMap<>(), dataSource);

        //3.输出到浏览器
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }
    // ============================================

}
