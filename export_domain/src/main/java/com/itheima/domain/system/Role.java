package com.itheima.domain.system;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

// 角色
@Data
public class Role implements Serializable {

    private String id;
    private String name;
    private String remark;
    private String companyId;
    private String companyName;
    private Integer orderNo;
    private String createBy;
    private String createDept;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
    private String updateBy;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updateTime;
}