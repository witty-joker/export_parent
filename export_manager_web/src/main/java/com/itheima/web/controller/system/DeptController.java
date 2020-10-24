package com.itheima.web.controller.system;

import cn.hutool.core.lang.UUID;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.itheima.domain.system.Dept;
import com.itheima.web.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.itheima.service.system.DeptService;

import java.util.List;

// dept 部门管理视图 DeptController
@Controller
@RequestMapping("/system/dept")
public class DeptController extends BaseController {

    @Reference
    private DeptService deptService;

    int i = 5;
    int j = 5;

    // 分页查询
    @RequestMapping(value = "/list", name = "部门列表查询")
    public String list(
            @RequestParam(defaultValue = "1", name = "page") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize
    ){
        PageInfo pageInfo = deptService.findByPage(getUser().getCompanyId(), pageNum, pageSize);

        request.setAttribute("page", pageInfo);

        return "/system/dept/dept-list";
    }

    // 跳转新增部门页面
    @RequestMapping(value = "/toAdd", name = "跳转新增部门页面")
    public String toAdd() {
        /*
            分析：
                部门信息回显：查询所有信息。回显部门信息
                跳转新增页面
         */
        List<Dept> deptList = deptService.findAll(getUser().getCompanyId());
        request.setAttribute("deptList", deptList);

        return "/system/dept/dept-add";
    }

    // 新增/修改共同使用
    @RequestMapping(value = "/edit", name = "新增/修改共同使用")
    public String edit(Dept dept) {
        // 新增修改错误解决 ：如果上级部门为""就置为null
        // 获取上级部门id
        String parentId = dept.getParent().getId();

        if (StringUtils.isEmpty(dept.getParent().getId())) {
            dept.getParent().setId(null);
        }

        // 判断dept中id是否为空，为空就新增
        if (StringUtils.isEmpty(dept.getId())) {
            // 1 添加主键
            // dept.setId(UUID.randomUUID().toString());
            if (parentId == null) {
                // 为null就没有父部门
                dept.setId("10" + i++);
            } else {
                // 不为空就有父部门
                dept.setId(parentId + "10" + j++);
            }

            // 2. 设置企业信息
            dept.setCompanyId(getUser().getCompanyId());
            dept.setCompanyName(getUser().getCompanyName());

            // 3 调用添加方法
            deptService.save(dept);
        } else {
            /*
                根据父部门进行修改：
                    1 根据id删除该对象数据库中的属性
                    2 获取前端传递的数据对象，修改id值
                    3 保存前端传递的数据对象
             */
            /*// 1 根据id删除该对象数据库中的属性
            deptService.deleteById(dept.getId());

            // 2 获取前端传递的数据对象，修改id值
            // 1 添加主键
            if (parentId == null) {
                // 为null就没有父部门
                dept.setId("10" + i++);
            } else {
                // 不为空就有父部门
                dept.setId(parentId + "10" + j++);
            }

            // 3 保存前端传递的数据对象
            deptService.save(dept);*/

            /**
             * 多次操作数据库必须放到service层执行
             */

            // 调用修改save
            i += 1;
            j += 1;

            // 2. 设置企业信息
            dept.setCompanyId(getUser().getCompanyId());
            dept.setCompanyName(getUser().getCompanyName());

            // 修改
            deptService.updateAmendId(dept, i, j);
        }

        // 共：重定向到list页面
        return "redirect:/system/dept/list.do";
    }

    // 跳转修改部门
    @RequestMapping(value = "/toUpdate", name = "跳转修改部门")
    public String toUpdate(String id) {
        /*
            修改部门页面需要回显信息
            回显后跳转修改页面
         */
        Dept dept = deptService.findById(id);
        request.setAttribute("dept", dept);

        // 保存deptList信息
        List<Dept> deptList = deptService.findAll(getUser().getCompanyId());
        request.setAttribute("deptList", deptList);

        return "/system/dept/dept-update";
    }

    // 删除部门信息
    @RequestMapping(value = "/delete", name = "删除部门信息")
    public String delete(String id) {
        deptService.deleteById(id);

        return "redirect:/system/dept/list.do";
    }

    /*// 删除部门验证是否有子
    @ResponseBody
    @RequestMapping(value = "/deleteSon", name = "删除部门信息")
    public ModelAndView deleteSon(String id) {
        ModelAndView modelAndView = new ModelAndView();

        *//*分析：
            1 获取所有数据
            2 遍历数据 获取父parent_id
            3 判断id是否出现过父parent_id中
            4 不出现则true
            5 出现则为false*//*

        // 1 所有数据
        List<Dept> deptList = deptService.findAll(getCompanyId());

        for (int i = 0; i < deptList.size(); i++) {
            Dept parent = deptList.get(i).getParent();
            if (parent != null) {
                String parentId = parent.getId();
                if (parentId.equals(id)) {
                    modelAndView.addObject("resp", true);
                    modelAndView.setViewName("/system/dept/dept-list");
                    return modelAndView;
                }
            }
        }
        modelAndView.addObject("resp", false);
        modelAndView.setViewName("/system/dept/dept-list");
        return modelAndView;
    }*/

    // 删除部门验证是否有子
    @ResponseBody
    @RequestMapping(value = "/deleteSon", name = "删除部门验证是否有子")
    public List<Dept> deleteSon(String id) {
        List<Dept> childrenDeptList = deptService.findChildrenDept(id);

        return childrenDeptList;
    }

}
