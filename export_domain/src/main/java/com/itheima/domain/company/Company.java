package com.itheima.domain.company;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

// 公司信息实体类
// 实现Serializable序列化接口
@Data
public class Company implements Serializable {

    // 1 属性名必须是驼峰式
    // 2 注意属性数据的类型 Date
    private String id;
    private String name;
    // 类型转换器：
    // 1 编码形式：实现麻烦，只需要实现一次
    // 2 注解形式：实现简单，每个实体类中，只要有类型需要转换都需要加注解
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date expirationDate;
    private String address;
    private String licenseId;
    private String representative;
    private String phone;
    private String companySize;
    private String industry;
    private String remarks;
    private Integer state;
    private Double balance;
    private String city;
    private Integer isDeleted;

}
