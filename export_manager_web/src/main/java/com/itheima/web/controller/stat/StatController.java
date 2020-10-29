package com.itheima.web.controller.stat;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.hutool.core.lang.UUID;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.itheima.domain.cargo.Contract;
import com.itheima.domain.cargo.ContractExample;
import com.itheima.service.cargo.ContractService;
import com.itheima.service.cargo.ExtCproductService;
import com.itheima.service.stat.StatService;
import com.itheima.utils.DownloadUtil;
import com.itheima.vo.ContractProductVo;
import com.itheima.web.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

// 统计分析页面跳转
@Controller
@RequestMapping("/stat")
public class StatController extends BaseController {

    @Reference
    private StatService statService;

    // 统计分析页面跳转
    @RequestMapping(value = "/toCharts", name = "统计分析页面跳转")
    public String toCharts(String chartsType) {

        return "/stat/stat-" + chartsType;

    }

    // 厂家销售统计分析
    @ResponseBody
    @RequestMapping(value = "/factoryCharts", name = "厂家销售统计分析")
    public List<Map> factoryCharts() {
        /*
            需要查询厂家销售统计：
         */
        List<Map> factoryCharts = statService.findFactoryCharts(getCompanyId());

        return factoryCharts;

    }

    // 产品销售统计
    @ResponseBody
    @RequestMapping(value = "/sellCharts", name = "产品销售统计")
    public List<Map> sellCharts() {
        /*
            需要查询产品销售统计
         */
        List<Map> sellCharts = statService.findSellCharts(getCompanyId());

        return sellCharts;
    }

    // 每小时在线人数分析
    @ResponseBody
    @RequestMapping(value = "/onlineCharts", name = "每小时在线人数分析")
    public List<Map> onlineCharts() {

        /*
            每小时在线人数分析
         */
        List<Map> onlineCharts = statService.findOnlineCharts(getCompanyId());

        return onlineCharts;
    }

    // =============扩展功能================
    // 查询市场价最高的前10名产品（货物）（按市场价统计）
    @ResponseBody
    @RequestMapping(value = "/priceCharts", name = "查询市场价最高的前10名产品（货物）（按市场价统计）")
    public List<Map> priceCharts() {
        /*
            查询市场价最高的前10名产品（货物）（按市场价统计）
         */
        List<Map> priceCharts = statService.findPriceCharts(getCompanyId());

        return priceCharts;
    }

    // 统计公司内每个部门的人数
    @ResponseBody
    @RequestMapping(value = "/irsCharts", name = "统计公司内每个部门的人数")
    public List<Map> irsCharts() {
        /*
            统计公司内每个部门的人数
         */
        List<Map> irsCharts = statService.findIrsCharts(getCompanyId());

        return irsCharts;
    }

    // 统计公司内每个人签订的购销合同数
    @ResponseBody
    @RequestMapping(value = "/contractCharts", name = "统计公司内每个人签订的购销合同数")
    public List<Map> contractCharts() {
        /*
            统计公司内每个人签订的购销合同数
         */
        List<Map> contractCharts = statService.findContractCharts(getCompanyId());

        return contractCharts;
    }

    // 航运地图展示
    @ResponseBody
    @RequestMapping(value = "/shippingCharts", name = "航运地图展示")
    public List<Map> shippingCharts() {
        /*
            航运地图展示
         */
        List<Map> shippingCharts = new ArrayList<>();
        // List<Map> shippingCharts = statService.findShippingCharts(getCompanyId());

        return shippingCharts;
    }
}
