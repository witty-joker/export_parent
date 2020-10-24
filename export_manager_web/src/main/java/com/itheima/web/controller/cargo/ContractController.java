package com.itheima.web.controller.cargo;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.hutool.core.lang.UUID;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.itheima.domain.cargo.Contract;
import com.itheima.domain.cargo.ContractExample;
import com.itheima.domain.cargo.ExtCproduct;
import com.itheima.domain.cargo.ExtCproductExample;
import com.itheima.service.cargo.ContractService;
import com.itheima.service.cargo.ExtCproductService;
import com.itheima.utils.DownloadUtil;
import com.itheima.vo.ContractProductVo;
import com.itheima.web.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

// contract 合同管理视图 ContractController
@Controller
@RequestMapping("/cargo/contract")
public class ContractController extends BaseController {

    @Reference
    private ContractService contractService;

    @Reference
    private ExtCproductService extCproductService;

    // 分页查询
    @RequestMapping(value = "/list", name = "合同列表查询")
    public String list(
            @RequestParam(defaultValue = "1", name = "page") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize
    ){
        // 获取ContractExample
        ContractExample contractExample = new ContractExample();

        // 1 封装条件
        ContractExample.Criteria criteria = contractExample.createCriteria();

        // 获取CompanyId()
        criteria.andCompanyIdEqualTo(getCompanyId());

        //==================细粒度权限控制==================
        // 判断用户等级 ，根据不同等级去拼接不同的条件
        Integer degree = getUser().getDegree();
        if (degree == 4) {
            // 普通员工 4
            criteria.andCreateByEqualTo(getUser().getId());
        } else if (degree == 3) {
            // 部门经理 3
            criteria.andCreateDeptEqualTo(getUser().getDeptId());
        } else if (degree == 2) {
            //总经理
            criteria.andCreateDeptLike(getUser().getDeptId() + "%");
        }

        //==================细粒度权限控制==================

        // 2 排序
        contractExample.setOrderByClause("create_time desc");

        PageInfo pageInfo = contractService.findByPage(pageNum, pageSize, contractExample);

        request.setAttribute("page", pageInfo);

        return "/cargo/contract/contract-list";
    }

    // 跳转新增合同页面
    @RequestMapping(value = "/toAdd", name = "跳转新增合同页面")
    public String toAdd() {
        /*
            分析：
                合同信息回显：查询所有信息。回显合同信息
                跳转新增页面
         */

        return "/cargo/contract/contract-add";
    }

    // 编辑合同内容
    @RequestMapping(value = "/toUpdate", name = "编辑合同内容")
    public String toUpdate(String id) {
        /*
            编辑合同内容分析：
                1 根据id获取合同内容
                2 将合同内容回显到页面
                3 跳转到合同编辑页面
         */
        // 1 根据id获取合同内容
        Contract contract = contractService.findById(id);

        // 2 将合同内容回显到页面
        request.setAttribute("contract", contract);

        // 3 跳转到合同编辑页面
        return "/cargo/contract/contract-update";
    }

    // 新增/修改共同使用
    @RequestMapping(value = "/edit", name = "新增/修改共同使用")
    public String edit(Contract contract) {

        // 判断contract中id是否为空，为空就新增
        if (StringUtils.isEmpty(contract.getId())) {
            // 1 添加主键
            contract.setId(UUID.randomUUID().toString());

            // 2. 设置合同信息
            contract.setCompanyId(getUser().getCompanyId());
            contract.setCompanyName(getUser().getCompanyName());

            // 3 创建id\创建部门id\创建时间
            contract.setCreateBy(getUser().getId());
            contract.setCreateDept(getUser().getDeptId());
            contract.setCreateTime(new Date());

            // 4 设置当前合同状态
            contract.setState(0);

            // 4 调用添加方法
            contractService.save(contract);
        } else {
            // 修改
            // 调用修改update
            contractService.update(contract);
        }

        // 共：重定向到list页面
        return "redirect:/cargo/contract/list.do";
    }
    
    // 删除合同信息 根据id进行删除
    @RequestMapping(value = "/delete", name = "删除合同信息")
    public String delete(String id) {
        // 根据id查询删除
        contractService.delete(id);

        return "redirect:/cargo/contract/list.do";
    }

    // 提交合同
    @RequestMapping(value = "/submit", name = "提交合同")
    public String submit(String id) {
        /*
            提交合同：
                1 修改合同的状态为1
                2 转发到list页面
         */
        // 1 修改合同的状态为1
        Contract contract = contractService.findById(id);

        // 修改
        contract.setId(id);
        contract.setState(1);
        contractService.update(contract);

        // 2 转发到list页面
        return "redirect:/cargo/contract/list.do";
    }

    // 取消合同
    @RequestMapping(value = "/cancel", name = "取消合同")
    public String cancel(String id) {
        /*
            提交合同：
                1 修改合同的状态为0
                2 转发到list页面
         */
        // 1 修改合同的状态为1
        Contract contract = contractService.findById(id);

        // 修改
        contract.setId(id);
        contract.setState(0);
        contractService.update(contract);

        // 2 转发到list页面
        return "redirect:/cargo/contract/list.do";
    }

    // 查看合同
    @RequestMapping(value = "/toView", name = "取消合同")
    public String toView(String id) {
        // 1 根据id获取合同内容
        Contract contract = contractService.findById(id);

        // 2 将合同内容回显到页面
        request.setAttribute("contract", contract);

        // 3 跳转到合同查看页面
        return "/cargo/contract/contract-view";
    }

    // =======================================出货表=======================================

    // 跳转到出货列表
    @RequestMapping(value = "/print", name = "跳转出货列表")
    public String print() {
        return "/cargo/print/contract-print";
    }

    // 出货货物导出
    @RequestMapping(value = "/printExcel", name = "导出货物列表")
    public void printExcel(String inputDate) {
        /*
            1 根据输入的参数查询货物列表
            2 封装列表为一个工作簿
                a 封装一个工作簿
                b 使用工作簿创建一个工作表
                c 使用工作表创建第0行，表名
                d 使用工作表创建第1行，表头
                e 使用工作表创建第n行
                f 使用行创建单元格
                g 向单元格设置数据样式
            3 文件下载
         */
        // 1 根据输入的参数查询货物列表
        List<ContractProductVo> list = contractService.findContractProductVo(inputDate, getCompanyId());
        // System.out.println(list);

        // 2 封装列表为一个工作簿
        //     a 封装一个工作簿
        Workbook workbook = new XSSFWorkbook();

        //     b 使用工作簿创建一个工作表
        Sheet sheet = workbook.createSheet();
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 8)); // 合并单元格
        for (int i = 1; i < 9; i++) {
            // 设置列宽
            sheet.setColumnWidth(i, 15 * 256);
        }

        //     c 使用工作表创建第0行，表名
        Row rowO = sheet.createRow(0);
        for (int i = 1; i < 9; i++) {
            Cell cell = rowO.createCell(i);
            // 这里设置第0行
            cell.setCellStyle(bigTitleStyle(workbook));
        }
        String title = inputDate.replaceAll("-0", "年").replaceAll("-", "年");
        rowO.getCell(1).setCellValue(title + "月份出货表");

        //     d 使用工作表创建第1行，表头
        Row row1 = sheet.createRow(1);
        for (int i = 1; i < 9; i++) {
            Cell cell = row1.createCell(i);
            // 设置样式
            cell.setCellStyle(littleTitleStyle(workbook));
        }
        row1.getCell(1).setCellValue("客户");
        row1.getCell(2).setCellValue("合同号");
        row1.getCell(3).setCellValue("货号");
        row1.getCell(4).setCellValue("数量");
        row1.getCell(5).setCellValue("工厂");
        row1.getCell(6).setCellValue("工厂交货期");
        row1.getCell(7).setCellValue("船期间");
        row1.getCell(8).setCellValue("贸易条款");

        //     e 使用工作表创建第n行
        for (int i = 0; i < list.size(); i++) {
            // 获取list中的每条数据
            ContractProductVo contractProductVo = list.get(i);

            //     f 使用行创建单元格
            Row rown = sheet.createRow(2 + i);
            for (int j = 1; j < 9; j++) {
                Cell cell = rown.createCell(j);
                // 设置样式
                cell.setCellStyle(textStyle(workbook));
            }

            //     g 向单元格设置数据
            rown.getCell(1).setCellValue(contractProductVo.getCustomName());
            rown.getCell(2).setCellValue(contractProductVo.getContractNo());
            rown.getCell(3).setCellValue(contractProductVo.getProductNo());
            rown.getCell(4).setCellValue(contractProductVo.getCnumber());
            rown.getCell(5).setCellValue(contractProductVo.getFactoryName());
            rown.getCell(6).setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(contractProductVo.getDeliveryPeriod()));
            rown.getCell(7).setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(contractProductVo.getShipTime()));
            rown.getCell(8).setCellValue(contractProductVo.getTradeTerms());

        }

        // 3 文件下载
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
            DownloadUtil.download(outputStream, response, title + "月出货表.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // 自定义模板导出货物列表
    @RequestMapping(value = "/myPrintExcel", name = "自定义模板导出货物列表")
    public void myPrintExcel(String inputDate) {
        /*
            1 根据输入的参数查询货物列表
            2 使用模板创建工作簿
                a 读取一个模板
                b 使用模板创建工作簿
                c 获取工作表
                d 获取第0行，替换文件
                e 第一行不用改变
                f 获取第二行每一个单元格样式，存储到一个数组中，备用
                g 遍历list结合，
                h 使用行创建单元格
                i 向单元格中设置数据
                j 向单元格中设置样式
            3 文件下载
         */
        // 1 根据输入的参数查询货物列表
        List<ContractProductVo> list = contractService.findContractProductVo(inputDate, getCompanyId());
        // System.out.println(list);

        // 2 使用模板创建一个工作簿
        //     a 读取一个模板
        String realPath = session.getServletContext().getRealPath("/make/xlsprint/tOUTPRODUCT.xlsx");

        //     b 使用模板创建工作簿
        Workbook workbook = null;
        try {
            workbook = new XSSFWorkbook(new FileInputStream(realPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //     c 获取工作表
        Sheet sheet = workbook.getSheetAt(0);

        //     d 获取第0行，替换文件
        Row row0 = sheet.getRow(0);
        String title = inputDate.replaceAll("-0", "年").replaceAll("-", "年");
        row0.getCell(1).setCellValue(title + "月份出货表");

        //     e 第一行不用改变
        //     f 获取第二行每一个单元格样式，存储到一个数组中，备用
        // 获取第二行的每一个单元格
        Row row2 = sheet.getRow(2);

        // 遍历第二行row2
        CellStyle[] cellStyles = new CellStyle[8];
        for (int i = 0; i < cellStyles.length; i++) {
            CellStyle cellStyle = row2.getCell(i + 1).getCellStyle();
            cellStyles[i] = cellStyle;
        }

        //     g 遍历list集合数据
        for (int i = 0; i < list.size(); i++) {
            // 获取行
            ContractProductVo contractProductVo = list.get(i);

            //     h 使用行创建单元格
            Row rown = sheet.createRow(2 + i);
            for (int j = 0; j < 9; j++) {
                Cell cell = rown.createCell(j);
                // 设置简单样式
                cell.setCellStyle(textStyle(workbook));
            }

            //     i 向单元格中设置数据
            rown.getCell(1).setCellValue(contractProductVo.getCustomName());
            rown.getCell(2).setCellValue(contractProductVo.getContractNo());
            rown.getCell(3).setCellValue(contractProductVo.getProductNo());
            rown.getCell(4).setCellValue(contractProductVo.getCnumber());
            rown.getCell(5).setCellValue(contractProductVo.getFactoryName());
            rown.getCell(6).setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(contractProductVo.getDeliveryPeriod()));
            rown.getCell(7).setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(contractProductVo.getShipTime()));
            rown.getCell(8).setCellValue(contractProductVo.getTradeTerms());

            //     j 向单元格中设置样式 模板中的样式
            for (int k = 0; k < cellStyles.length; k++) {
                rown.getCell(k + 1).setCellStyle(cellStyles[k]);
            }

        }

        // 3 文件下载
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
            DownloadUtil.download(outputStream, response, title + "月出货表.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // 百万出货货物导出
    @RequestMapping(value = "/millionData", name = "百万导出货物列表")
    public void millionData(String inputDate) {
        /*
            1 根据输入的参数查询货物列表
            2 封装列表为一个工作簿
                a 封装一个工作簿
                b 使用工作簿创建一个工作表
                c 使用工作表创建第0行，表名
                d 使用工作表创建第1行，表头
                e 使用工作表创建第n行
                f 使用行创建单元格
                g 向单元格设置数据样式
            3 文件下载
         */
        // 1 根据输入的参数查询货物列表
        List<ContractProductVo> list = contractService.findContractProductVo(inputDate, getCompanyId());
        // System.out.println(list);

        // 2 封装列表为一个工作簿
        //     a 封装一个工作簿
        Workbook workbook = new SXSSFWorkbook();

        //     b 使用工作簿创建一个工作表
        Sheet sheet = workbook.createSheet();
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 8)); // 合并单元格
        for (int i = 1; i < 9; i++) {
            // 设置列宽
            sheet.setColumnWidth(i, 15 * 256);
        }

        //     c 使用工作表创建第0行，表名
        Row rowO = sheet.createRow(0);
        for (int i = 1; i < 9; i++) {
            Cell cell = rowO.createCell(i);
            // 这里设置第0行
            // cell.setCellStyle(bigTitleStyle(workbook));
        }
        String title = inputDate.replaceAll("-0", "年").replaceAll("-", "年");
        rowO.getCell(1).setCellValue(title + "月份出货表");

        //     d 使用工作表创建第1行，表头
        Row row1 = sheet.createRow(1);
        for (int i = 1; i < 9; i++) {
            Cell cell = row1.createCell(i);
            // 设置样式
            // cell.setCellStyle(littleTitleStyle(workbook));
        }
        row1.getCell(1).setCellValue("客户");
        row1.getCell(2).setCellValue("合同号");
        row1.getCell(3).setCellValue("货号");
        row1.getCell(4).setCellValue("数量");
        row1.getCell(5).setCellValue("工厂");
        row1.getCell(6).setCellValue("工厂交货期");
        row1.getCell(7).setCellValue("船期间");
        row1.getCell(8).setCellValue("贸易条款");

        //     e 使用工作表创建第n行
        Integer n = 2;
        for (int i = 0; i < list.size(); i++) {
            // 模拟百万数据
            for (int x = 0; x < 7999; x++) {
                // 获取list中的每条数据
                ContractProductVo contractProductVo = list.get(i);

                //     f 使用行创建单元格
                Row rown = sheet.createRow(n++);
                for (int j = 1; j < 9; j++) {
                    Cell cell = rown.createCell(j);
                    // 设置样式
                    // cell.setCellStyle(textStyle(workbook));
                }

                //     g 向单元格设置数据
                rown.getCell(1).setCellValue(contractProductVo.getCustomName());
                rown.getCell(2).setCellValue(contractProductVo.getContractNo());
                rown.getCell(3).setCellValue(contractProductVo.getProductNo());
                rown.getCell(4).setCellValue(contractProductVo.getCnumber());
                rown.getCell(5).setCellValue(contractProductVo.getFactoryName());
                rown.getCell(6).setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(contractProductVo.getDeliveryPeriod()));
                rown.getCell(7).setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(contractProductVo.getShipTime()));
                rown.getCell(8).setCellValue(contractProductVo.getTradeTerms());

            }

        }

        // 3 文件下载
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
            DownloadUtil.download(outputStream, response, title + "月出货表.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // 出货货物导出
    @RequestMapping(value = "/easyPoiData", name = "导出货物列表")
    public void easyPoiData(String inputDate) {
        /*
            1 根据输入的参数查询货物列表
            2 定义excel配置
                a ExportParams参数对象
                b 设置页名称
                c 设置表名称
                d 设置版本
            3 创建workbook对象
            4 导出文件
            5 导出工作簿 关闭流
         */
        // 1 根据输入的参数查询货物列表
        List<ContractProductVo> list = contractService.findContractProductVo(inputDate, getCompanyId());

        // 2 定义excel配置
        //      a ExportParams参数对象
        ExportParams exportParams = new ExportParams();

        //      b 设置页名称
        exportParams.setSheetName("sheet1");

        //      c 设置表名称  设置为 2017年11月出货表
        String title = inputDate.replaceAll("-0", "年").replaceAll("-", "年");
        exportParams.setTitle(title + "月份出货表");

        //      d 设置版本 2007 之后
        exportParams.setType(ExcelType.XSSF);

        // 3 创建workbook对象
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, ContractProductVo.class, list);

        // 4 导出文件
        /*FileOutputStream out = null;
        try {
            workbook.write(out);
            out = new FileOutputStream("D:\\document\\" + title + "月份出货表");
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        // 5 导出工作簿 关闭流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
            DownloadUtil.download(outputStream, response, title + "月出货表.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    // ===================导出样式==================================
    //大标题的样式
    public CellStyle bigTitleStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);//字体加粗
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);                //横向居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);        //纵向居中
        return style;
    }

    //小标题的样式
    public CellStyle littleTitleStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);                //横向居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);        //纵向居中
        style.setBorderTop(BorderStyle.THIN);                        //上细线
        style.setBorderBottom(BorderStyle.THIN);                    //下细线
        style.setBorderLeft(BorderStyle.THIN);                        //左细线
        style.setBorderRight(BorderStyle.THIN);                        //右细线
        return style;
    }

    //文字样式
    public CellStyle textStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);                //横向居左
        style.setVerticalAlignment(VerticalAlignment.CENTER);        //纵向居中
        style.setBorderTop(BorderStyle.THIN);                        //上细线
        style.setBorderBottom(BorderStyle.THIN);                    //下细线
        style.setBorderLeft(BorderStyle.THIN);                        //左细线
        style.setBorderRight(BorderStyle.THIN);                        //右细线

        return style;
    }

}
