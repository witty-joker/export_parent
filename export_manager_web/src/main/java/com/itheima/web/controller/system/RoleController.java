package com.itheima.web.controller.system;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.itheima.domain.system.Module;
import com.itheima.domain.system.Role;
import com.itheima.web.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.itheima.service.system.ModuleService;
import com.itheima.service.system.RoleService;

import java.util.*;

// 角色
@Controller
@RequestMapping("/system/role")
public class RoleController extends BaseController {

    @Reference
    private RoleService roleService;

    @Reference
    private ModuleService moduleService;

    // 角色列表查询
    @RequestMapping(value = "/list", name = "角色列表查询")
    public String list (
         @RequestParam(defaultValue = "1", name = "page") Integer pageNum,
         @RequestParam(defaultValue = "5") Integer pageSize
    ) {
        PageInfo pageInfo = roleService.findByPage(getUser().getCompanyId(), pageNum, pageSize);

        request.setAttribute("page", pageInfo);

        return "/system/role/role-list";
    }

    //  跳转角色新增页面
    @RequestMapping(value = "toAdd", name = "跳转角色新增页面")
    public String toAdd() {
        return "/system/role/role-add";
    }

    // 跳转角色修改页面
    @RequestMapping(value = "toUpdate", name = "跳转角色修改页面")
    public String toUpdate(String id) {
        // 1 根据id查询当前角色信息
        Role role = roleService.findById(id);
        request.setAttribute("role", role);

        // 2 转发到修改页面
        return "/system/role/role-update";
    }

    // 新增和修改共同使用
    @RequestMapping(value = "/edit",  name = "角色新增和修改")
    public String edit(Role role) {
        if (StringUtils.isEmpty(role.getId())) { // 新增
            // 1 设置主键
            role.setId(UUID.randomUUID().toString());

            // 2 设置企业信息
            role.setCompanyId(getUser().getCompanyId());
            role.setCompanyName(getUser().getCompanyName());

            roleService.save(role);
        } else {    // 修改
            roleService.update(role);
        }

        // 重定向到list方法
        return "redirect:/system/role/list.do";
    }

    // 角色删除
    @RequestMapping(value = "/delete", name = "角色删除")
    public String delete(String id) {
        // 调用service删除
        roleService.delete(id);

        // 重定向到list方法
        return "redirect:/system/role/list.do";
    }

    // 进入角色分配权限
    @RequestMapping(value = "/roleModule", name = "进入角色分配权限")
    public String roleModule(@RequestParam("roleid") String roleId) throws JsonProcessingException {
        /*
        分析：
            1 需要回显角色名
            2 需要回显所有的角色
            3 需要回显已选角色
            4 将回显角色信息封装为json对象
            5 跳转页面
         */
        // 1 需要回显角色名
        Role role = roleService.findById(roleId);
        request.setAttribute("role", role);

        // 2 需要回显所有的module 信息。管理项目
        List<Module> moduleList = moduleService.findAll();

        // 3 需要回显已选module 管理项 :
        List<String> roleModelList = roleService.roleModelByRoleId(roleId);

        // 4 将回显角色信息封装为json对象
        List<Map> list = new ArrayList<>();
        // a 遍历所有项
        for (Module module : moduleList) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", module.getId());
            map.put("pId", module.getParentId());
            map.put("name", module.getName());

            // b 设置回显的消息为选中状态
            if (roleModelList.contains(module.getId())) {
                map.put("checked", true);
            }

            // c 添加到list集合
            list.add(map);

        }

        // d 将list，转换为json格式
        String value = new ObjectMapper().writeValueAsString(list);

        // e 保存到data中
        request.setAttribute("data", value);

        // 5 跳转页面
        return "/system/role/role-module";
    }

    // 角色分配权限
    @RequestMapping(value = "/changeModule", name = "角色分配权限")
    public String changeModule(@RequestParam("roleid") String roleId, String[] moduleIds) {
        /*
        获取到了roleId(角色id) 和 权限
            1 删除该角色所有的权限
            2 将新权限添加
            3 重定向到list页面
         */
        // 1 删除该角色所有的权限
        // 2 将新权限添加
        roleService.changeModule(roleId, moduleIds);

        // 3 重定向到list页面
        return "redirect:/system/role/list.do";
    }

}
