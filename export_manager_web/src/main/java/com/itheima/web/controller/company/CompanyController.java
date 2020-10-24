package com.itheima.web.controller.company;

import cn.hutool.core.lang.UUID;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.itheima.domain.company.Company;
import com.itheima.service.company.CompanyService;
import com.itheima.web.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/company")
public class CompanyController extends BaseController {

    @Reference  // 使用dubbo
    private CompanyService companyService;

   /* // 查询所有
    @RequestMapping(value = "/list", name = "企业列表查询")
    public String list(
            @RequestParam(defaultValue = "1", name = "page") Integer pageNum,
            @RequestParam(defaultValue = "1") Integer pageSize
    ) {
        PageInfo pageInfo = companyService.findByPage(pageNum, pageSize);

        request.setAttribute("page", pageInfo);

        // 跳转到页面，请求转发。拼接前后缀
        return "company/company-list";
    }*/

    /*
        @RequiresPermissions("企业管理") 代表只有用户有企业管理的权限，才能访问当前方法
        相当于XML中的 /company/list.do = perms["企业管理"]
     */
    // @RequiresPermissions("企业管理")
    // 分页查询所有
    @RequestMapping(value = "/list", name = "企业列表查询")
    public String list (
            @RequestParam (defaultValue = "1", name = "page") Integer pageNum,
            @RequestParam (defaultValue = "1") Integer pageSize
    ) {
        PageInfo pageInfo = companyService.findByPage(pageNum, pageSize);

        request.setAttribute("page", pageInfo);

        // 请求转发到页面
        return "company/company-list";
    }

    // 跳转到新增页面
    @RequestMapping(value = "/toAdd", name = "跳转到添加页面")
    public String toAdd() {
        /*
        跳转添加路径分析：
            直接请求转发跳转到添加页面
         */
        return "company/company-add";
    }

    // 新增/修改
    @RequestMapping(value = "/edit", name = "新增/修改保存")
    public String edit(Company company) {
        /*
        新增和修改使用同一方法执行
        保存分析：
            1 设置id
            2 调用service中的保存方法
            3 重定向到list页面
         */
        // 根据id判断是新增还是修改  isEmpty(company.getId) 判断id是否为空
        if (StringUtils.isEmpty(company.getId())) { // 新增
            // 1 设置id
            company.setId(UUID.randomUUID().toString());

            // 2 调用service中的保存方法
            companyService.save(company);

        } else {
            // 修改
            // 使用service
            companyService.update(company);
        }

        // 3 重定向到list页面
        return "redirect:/company/list.do";
    }

    // 跳转编辑企业页面
    @RequestMapping(value = "toUpdate", name = "跳转编辑页面")
    public String toUpdate(String id) {
        /*
        跳转编辑企业页面分析：
            1 根据id获取数据
            2 将数据保存到request域中，回显数据
            3 跳转到修改页面
         */
        // 1 根据id获取数据
        Company company = companyService.findById(id);

        // 2 将数据保存到request域中，回显数据
        request.setAttribute("company", company);

        // 3 跳转请求转发到修改页面
        return "company/company-update";
    }

    // 删除某一个企业信息
    @RequestMapping(value = "/delete", name = "删除单个企业信息")
    public String delete(String id) {
        /*
        删除某一个企业信息分析：
           1 调用service根据id删除该企业信息
           2 重定向到list
         */
        companyService.delete(id);
        return "redirect:/company/list.do";
    }
}
