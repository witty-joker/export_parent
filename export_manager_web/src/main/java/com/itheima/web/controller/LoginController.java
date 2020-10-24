package com.itheima.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.domain.system.Module;
import com.itheima.domain.system.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.itheima.service.system.UserService;

import java.util.List;

// 用户登录退出等功能
@Controller
public class LoginController extends BaseController{

    @Reference
    private UserService userService;

    // 使用Shiro安全框架登录方式
    @RequestMapping(value = "/login", name = "用户登录")
    public String login(String email,String password) {
        /*
            Shiro安全框架方式登录分析：
                1 封装email和password到Token（令牌）
                2 创建subject对象，调用subject.login(token)
                3 捕获异常：
                    无异常 登录成功 :将user保存session中
                    异常：抛出异常，异常放入request域中
         */
        // 1 封装email和password到Token（令牌）
        AuthenticationToken token = new UsernamePasswordToken(email, new Md5Hash(password, email, 2).toString());

        // 2 获取subject对象
        Subject subject = SecurityUtils.getSubject();

        try {
            // 3 使用subject.login(token)方法
            subject.login(token);

            // 4 登录成功获取user对象.保存到session
            User user = (User) subject.getPrincipal();
            session.setAttribute("loginUser", user);

            //  根据用户user查询对应的 权限列表 保存到session
            List<Module> modules = userService.findModuleByUser(user);
            session.setAttribute("modules", modules);

            // 跳转主页面
            return "redirect:/home/main.do";
        } catch (Exception e) {
            request.setAttribute("error", "用户名或者密码错误。");
            return "forward:login.jsp";
        }

    }

/*    // 用户登录 原版
	@RequestMapping(value = "/login", name = "用户登录")
	public String login(String email,String password) {
	    *//*
	    获取页面发送信息：email password  登录分析：
	        1 根据email获取用户信息 返回user对象
	        2 没有获取用户信息 错误提示
	        3 成功。就判断密码是否正确
	        4 不正确就错误提示
	        5 正确就登录成功 根据用户查询对应的权限
	     *//*
        // 1 根据email获取用户信息 返回user对象
        User user = userService.findByEmail(email);

        // 2 判断是否未获取到
        if (user == null ) {
            // 3 未获取到错误提示。返回登录页面
            request.setAttribute("error", "邮箱未注册，不存在。");

            return "forward:login.jsp";
        }

        // 4 用户存在 。判断密码是否正确
        String md5Password = new Md5Hash(password, email, 2).toString();
        if (!StringUtils.equals(md5Password, user.getPassword())) {
            // 5 密码错误
            request.setAttribute("error", "密码错误啊，沙雕。");
            return "forward:login.jsp";
        }

        // 6 登录成功。向session中保存用户信息
        session.setAttribute("loginUser", user);

        // 7 根据用户user查询对应的 权限列表
        List<Module> modules = new ArrayList<Module>();
        modules = userService.findModuleByUser(user);
        session.setAttribute("modules", modules);

        // 跳转主页面
        return "redirect:/home/main.do";
	}*/

    @RequestMapping("/home/main")
    public String main(){
        return "home/main";
    }

    @RequestMapping("/home/home")
    public String home(){
	    return "home/home";
    }

    //退出账户
    @RequestMapping(value = "/logout",name="用户登出")
    public String logout(){
        // 使用Shiro中的Subject中间键退出 logout()
        SecurityUtils.getSubject().logout();   //登出
        return "redirect:login.jsp";
    }
}
