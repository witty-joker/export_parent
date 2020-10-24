package com.itheima.web.controller.system;

import cn.hutool.core.lang.UUID;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.itheima.domain.system.Module;
import com.itheima.web.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.itheima.service.system.ModuleService;

import java.util.List;

// module 模块管理视图 ModuleController
@Controller
@RequestMapping("/system/module")
public class ModuleController extends BaseController {

    @Reference
    private ModuleService moduleService;

    // 分页查询
    @RequestMapping(value = "/list", name = "模块列表查询")
    public String list(
            @RequestParam(defaultValue = "1", name = "page") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize
    ){
        PageInfo pageInfo = moduleService.findByPage(pageNum, pageSize);

        request.setAttribute("page", pageInfo);

        return "/system/module/module-list";
    }

    // 跳转新增模块页面
    @RequestMapping(value = "/toAdd", name = "跳转新增模块页面")
    public String toAdd() {
        /*
            分析：
                模块信息回显：查询所有信息。回显模块信息 上级模块
                跳转新增页面
         */
        List<Module> moduleList = moduleService.findAll();
        request.setAttribute("menus", moduleList);

        return "/system/module/module-add";
    }

    // 新增/修改共同使用
    @RequestMapping(value = "/edit", name = "新增/修改共同使用")
    public String edit(Module module) {

        // 判断module中id是否为空，为空就新增
        if (StringUtils.isEmpty(module.getId())) {
            // 1 添加主键
            module.setId(UUID.randomUUID().toString());

            // 2 调用添加方法
            moduleService.save(module);
        } else {
            // 修改
            // 调用修改save
            moduleService.update(module);
        }

        // 共：重定向到list页面
        return "redirect:/system/module/list.do";
    }

    // 跳转修改模块
    @RequestMapping(value = "toUpdate", name = "跳转修改模块")
    public String toUpdate(String id) {
        /*
            修改模块页面需要回显信息
            回显后跳转修改页面
         */
        Module module = moduleService.findById(id);
        request.setAttribute("module", module);

        // 保存moduleList信息 上级模块
        List<Module> moduleList = moduleService.findAll();
        request.setAttribute("menus", moduleList);

        return "/system/module/module-update";
    }

    // 删除模块信息
    @RequestMapping(value = "delete", name = "删除模块信息")
    public String delete(String id) {
        moduleService.delete(id);

        return "redirect:/system/module/list.do";
    }

}
