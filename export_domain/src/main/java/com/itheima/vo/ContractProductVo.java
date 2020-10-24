package com.itheima.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class ContractProductVo implements Serializable {

    // 使用EastPOI实现Excel导出功能
    @Excel(name = "客户")
    private String customName;		//客户名称

    @Excel(name = "合同号")
    private String contractNo;		//合同号，订单号

    @Excel(name = "货号")
    private String productNo;		//货号

    @Excel(name = "数量")
    private Integer cnumber;		//数量

    @Excel(name = "厂家名称")
    private String factoryName;		//厂家名称

    @Excel(name = "交货期限", format = "yyyy-MM-dd", width = 15)
    private Date deliveryPeriod;	//交货期限

    @Excel(name = "船出发时期", format = "yyyy-MM-dd", width = 15)
    private Date shipTime;			//船期

    @Excel(name = "贸易条款")
    private String tradeTerms;		//贸易条款
}