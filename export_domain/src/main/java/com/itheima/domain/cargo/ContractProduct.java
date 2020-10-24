package com.itheima.domain.cargo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.itheima.domain.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * 合同下货物的实体类
 */
@Data
public class ContractProduct extends BaseEntity implements Serializable {

    private String id;
    @Excel(name = "货号")
    private String productNo;        //货号
    private String productImage;    //图片路径

    @Excel(name = "货物描述")
    private String productDesc;        //货描

    @Excel(name = "装率")
    private String loadingRate;        //报运：装率    1/3

    @Excel(name = "箱数")
    private Integer boxNum;            //报运：箱数    100

    @Excel(name = "包装单位(PCS/SETS)")
    private String packingUnit;        //包装单位：PCS/SETS   支/箱

    @Excel(name = "数量")
    private Integer cnumber;        //数量                            300
    private Integer outNumber;        //报运：出货数量            200
    private Integer finished;        //报运：是否完成		no

    @Excel(name = "要求")
    private String productRequest;    //要求

    @Excel(name = "单价")
    private Double price;            //单价
    private Double amount;            //总金额，冗余
    private Integer orderNo;        //排序号
    private String contractId;      //合同号

    @Excel(name = "生产厂家")
    private String factoryName;        //厂家名称，冗余字段
    private String factoryId;

    private List<ExtCproduct> extCproducts;    //货物和附件，一对多

    public ContractProduct() {
    }

    public ContractProduct(Object[] objs) {
        this.factoryName = objs[0].toString();
        this.productNo = objs[1].toString();
        this.cnumber = ((Double) objs[2]).intValue();
        this.packingUnit = objs[3].toString();
        this.loadingRate = objs[4].toString();
        this.boxNum = ((Double) objs[5]).intValue();
        this.price = (Double) objs[6];            //单价
        this.productRequest = objs[7].toString();
        this.productDesc = objs[8].toString();
    }
}
