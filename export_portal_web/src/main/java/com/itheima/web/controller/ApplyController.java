package com.itheima.web.controller;

import cn.hutool.core.lang.UUID;
import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.domain.company.Company;
import com.itheima.service.company.CompanyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

// controller包下创建ApplyController
@Controller
public class ApplyController {

    @Reference  // 使用dubbo
    private CompanyService companyService;

    @RequestMapping("/apply")
    @ResponseBody
    public String apply(Company company) {
        try {
            company.setId(UUID.randomUUID().toString());  //id 赋值成一个随机id
            company.setState(0); // 0未审核  1审核
            companyService.save(company);
            return "1"; //表示成功
        } catch (Exception e) {
            e.printStackTrace();
            return "0"; // 其他表示不成功
        }
    }

}
