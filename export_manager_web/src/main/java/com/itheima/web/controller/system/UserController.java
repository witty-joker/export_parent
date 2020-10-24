package com.itheima.web.controller.system;

import cn.hutool.core.lang.UUID;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.itheima.domain.system.Dept;
import com.itheima.domain.system.Role;
import com.itheima.domain.system.User;
import com.itheima.utils.MailUtil;
import com.itheima.web.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.itheima.service.system.DeptService;
import com.itheima.service.system.RoleService;
import com.itheima.service.system.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// user 用户管理视图 UserController
@Controller
@RequestMapping("/system/user")
public class UserController extends BaseController {

    @Reference // 部门
    private DeptService deptService;

    @Reference // 角色
    private UserService userService;

    @Reference
    private RoleService roleService;

    // 消息中间件
    @Autowired
    private AmqpTemplate template;

    // 分页查询
    @RequestMapping(value = "/list", name = "用户列表查询")
    public String list(
            @RequestParam(defaultValue = "1", name = "page") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize
    ){
        PageInfo pageInfo = userService.findByPage(getUser().getCompanyId(), pageNum, pageSize);

        request.setAttribute("page", pageInfo);

        return "/system/user/user-list";
    }

    // 跳转新增用户页面
    @RequestMapping(value = "/toAdd", name = "跳转新增用户页面")
    public String toAdd() {
        /*
            分析：
                用户信息回显：查询所有信息。回显用户信息
                跳转新增页面
         */
        List<Dept> deptList = deptService.findAll(getUser().getCompanyId());
        request.setAttribute("deptList", deptList);

        return "/system/user/user-add";
    }

    // 新增/修改共同使用  这里密码使用MD5加密
    @RequestMapping(value = "/edit", name = "新增/修改共同使用")
    public String edit(User user) {
        // 判断密码是否不为空 存在加密
        String oldPassword = user.getPassword();
        if (StringUtils.isNotEmpty(user.getPassword())) {
            // 存在 对密码加密 (密码, 盐, 散列次数(迭代加密))
            String md5Password = new Md5Hash(user.getPassword(), user.getEmail(), 2).toString();
            user.setPassword(md5Password);
        }


        // 判断user中id是否为空，为空就新增
        if (StringUtils.isEmpty(user.getId())) {
            // 1 添加主键
            user.setId(UUID.randomUUID().toString());

            // 2. 设置企业信息
            user.setCompanyId(getUser().getCompanyId());
            user.setCompanyName(getUser().getCompanyName());

            // 3 调用添加方法
            userService.save(user);


           /* // 4 开始发送邮件
            String to = user.getEmail();
            String title = "XXX平台--用户注册成功";
            String content = "恭喜您,您的账号已经在XXX平台开通成功,请使用当前邮箱作用账号,使用" + oldPassword + "作为密码进行登录";
            MailUtil.sendMail(to, title, content);*/

            // 4 向消息中间件MQ发送消息  routingKey(路由键)  message(信息)
            String to = user.getEmail();
            String title = "XXX平台--用户注册成功";
            String content = "恭喜您,您的账号已经在XXX平台开通成功,请使用当前邮箱作用账号,使用" + oldPassword + "作为密码进行登录";

            Map<String, Object> map = new HashMap<>();
            map.put("to", to);
            map.put("title", title);
            map.put("content", content);
            template.convertAndSend("mail.send", map);

        } else {
            // 修改
            // 调用修改save
            userService.update(user);
        }

        // 共：重定向到list页面
        return "redirect:/system/user/list.do";
    }

    // 跳转修改用户
    @RequestMapping(value = "/toUpdate", name = "跳转修改用户")
    public String toUpdate(String id) {
        /*
            修改用户页面需要回显信息
            回显后跳转修改页面
         */
        User user = userService.findById(id);
        request.setAttribute("user", user);

        // 保存userList信息
        List<Dept> deptList = deptService.findAll(getUser().getCompanyId());
        request.setAttribute("deptList", deptList);

        // 不再回显密码
        user.setPassword(null);

        return "/system/user/user-update";
    }

    // 删除用户信息
    @RequestMapping(value = "/delete", name = "删除用户信息")
    public String delete(String id) {
        userService.delete(id);

        return "redirect:/system/user/list.do";
    }

    // 跳转分配角色页面
    @RequestMapping(value = "/roleList", name = "跳转分配角色页面")
    public String roleList(String id) {
        /*
        分析：
            1 显示查询用户的信息（查询用户信息）
            2 显示所有的角色，等待勾选（查询所有角色）
            3 回显当前用户已经方向的角色（查询中间表）
            4 跳转到用户分配角色的页面
         */
        // 1 显示查询用户的信息（查询用户信息）
        User user = userService.findById(id);
        request.setAttribute("user", user);

        // 2 显示所有的角色，等待勾选（查询所有角色） 查询 保存
        List<Role> roleList = roleService.findAll(getUser().getCompanyId());
        request.setAttribute("roleList", roleList);

        // 3 回显当前用户已经拥有的角色（查询中间表）  role_id
        List<String> userRoleStr = userService.findRoleIdsByUserId(id);
        request.setAttribute("userRoleStr", userRoleStr);

        // 4 跳转到用户分配角色的页面
        return "/system/user/user-role";
    }

    // 分配角色
    @RequestMapping(value = "/changeRole", name = "分配角色")
    public String changeRole(@RequestParam("userid") String userId, String[] roleIds) {
        /*
        分析：
            调用service中的方法
         */
        userService.changeRole(userId, roleIds);

        return "redirect:/system/user/list.do";
    }

}
