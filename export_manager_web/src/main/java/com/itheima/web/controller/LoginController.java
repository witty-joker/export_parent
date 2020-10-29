package com.itheima.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.domain.ResultInfo;
import com.itheima.domain.system.Module;
import com.itheima.domain.system.User;
import com.itheima.utils.FileUploadUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.itheima.service.system.UserService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 用户登录退出等功能
@Controller
public class LoginController extends BaseController{

    @Reference
    private UserService userService;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    // 消息中间件
    @Autowired
    private AmqpTemplate template;

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
            User loginUser = userService.findByEmail(email);
            loginUser.setPassword(password);
            session.setAttribute("loginUser", loginUser);

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
        request.removeAttribute("loginMsg");
        return "home/main";
    }

    @RequestMapping("/home/home")
    public String home(){
        request.removeAttribute("loginMsg");
        return "home/home";
    }

    //退出账户
    @RequestMapping(value = "/logout",name="用户登出")
    public String logout(){
        // 使用Shiro中的Subject中间键退出 logout()
        SecurityUtils.getSubject().logout();   //登出
        return "redirect:login.jsp";
    }

    // 个人信息修改
    @RequestMapping(value = "/home/update", name = "修改个人信息")
    public String messageUpdate(User user, MultipartFile portrait, String smsCode) {
        /*
        分析：
            个人信息修改方法分析：
                1 获取user用户信息，和文件上传信息 MultipartFile portrait
                2 添加文件上传代码
                     try {
                            String upload = fileUploadUtil.upload(productPhoto);
                            extCproduct.setProductImage(upload);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                3 将文件域名保存到user对象的pic中
                4 调用service中的修改方法进行修改
                    dao层添加pic修改的语句
                    service进行修改
                    （或者删除后修改）
                5 删除session中保存的登录信息
                6 保存新信息到session
                7 回到登录页面
         */
        // ==============验证码逻辑==================

        // 1 获取session中的验证码
        String code = (String) session.getAttribute("smsCode");
        if (StringUtils.isEmpty(smsCode)) {
            smsCode = "0";
        }

        // 2 判断验证码是否正确
        if (!smsCode.equals(code)) {
             // 不相等拦截
            request.setAttribute("loginMsg", "a");
            return "/home/main";
        }

        // ==============验证码逻辑==================

        request.removeAttribute("loginMsg");
        // 0 获取原有的账户信息
        User rawUser = userService.findByEmail(user.getEmail());

        // 添加文件上传的代码 （判断是否上传了文件）
        if(StringUtils.isBlank(portrait.getOriginalFilename())) {
            user.setPic(rawUser.getPic());
        } else {
            try {
                String upload = fileUploadUtil.upload(portrait);
                user.setPic(upload);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 1 原来的邮箱密码
        String rowEmail = rawUser.getEmail();
        String rowPassword = rawUser.getPassword(); // 数据库获取的加密后的

        // 2 现在的邮箱密码
        String email = user.getEmail();
        String password = user.getPassword();   // 未加密的


        // 判断密码是否不为空 存在加密
        if (StringUtils.isNotEmpty(user.getPassword())) {
            // 存在 对密码加密 (密码, 盐, 散列次数(迭代加密)) 无论密码如何
            String md5Password = new Md5Hash(user.getPassword(), user.getEmail(), 2).toString();
            user.setPassword(md5Password);

            // ** 判断是否修改了邮箱或者密码
            if (!rowPassword.equals(md5Password) || !rowEmail.equals(email)) {
                /*
                修改了邮箱密码  加密保存
                    1 保存service修改
                    2 删除session中信息
                    4 跳转到登录页面
                 */
                // 调用service保存修改
                userService.update(user);

                // 删除session中的信息
                session.removeAttribute("loginUser");

                // 4 向消息中间件MQ发送消息  routingKey(路由键)  message(信息)
                String to = "977730190@qq.com";
                String title = "邮箱或密码已修改";
                String content = "您的账号已经在XXX平台修改信息成功,请使用当前" + email + "作用账号,使用" + password + "作为密码进行登录";

                Map<String, Object> map = new HashMap<>();
                map.put("to", to);
                map.put("title", title);
                map.put("content", content);
                template.convertAndSend("mail.send", map);

                // 回到登录页面(判断是否修改账户密码，修改密码，就跳转到登录页面，没有修改密码就在本页面)
                SecurityUtils.getSubject().logout();   //登出
                return "redirect:login.jsp";
            }
        }
        // 没有修改账户密码
        // 调用service保存修改
        userService.update(user);

        // 删除session中的信息
        session.removeAttribute("loginUser");

        // 更新session信息
        User loginUser = userService.findByEmail(email);
        loginUser.setPassword(password);
        session.setAttribute("loginUser", loginUser);

        // 回到登录页面(判断是否修改账户密码，修改密码，就跳转到登录页面，没有修改密码就在本页面)
        return "home/main";
    }


    // 通过微信code登录
    @RequestMapping(value="/weixinlogin")
    public String wxLogin(String code, String state){
        /*
            微信登录分析：
                1 通过code获取用户信息
                2 判断用户是否为空
                    a 为空返回登录页面，显示登录失败
                    b 判断用户是否为空，为空则跳转到绑定用户按钮
                    c 其他，跳转免密登录逻辑
         */
        User user = userService.wxLogin(code) ;
        if(user == null){
            // a 为空返回登录页面，显示登录失败
            session.setAttribute("error", "微信登录失败！");
            return "forward:/login";
        } else if (user.getEmail() == null) {
            // b 判断用户是否为空，为空则跳转到绑定用户按钮页面
            session.setAttribute("openid", user.getOpenid());
            return "forward:bindLogin.jsp";
        } else {
            // c 其他，跳转免密登录逻辑 (验证用户名密码是否正确)
            session.setAttribute("wxUser", user);
            return "redirect:avoidCodeLogin.do";
        }
    }

    // 微信用户绑定
    @RequestMapping(value="/bindLogin")
    public String bindLogin(String openid, String email,String password){
        /*
            微信用户绑定分析：
                1 根据跳转的页面获取openid、 email、 password
                2 查询用户是否存在 （不存在提示错误）
                3 判断密码是否正确，（错误跳转首页提示）
         */
        // 1 根据跳转的页面获取openid、 email、 password
        // 1 封装email和password到Token（令牌）
        AuthenticationToken token = new UsernamePasswordToken(email, new Md5Hash(password, email, 2).toString());

        // 2 获取subject对象
        Subject subject = SecurityUtils.getSubject();

        try {
            // 3 使用subject.login(token)方法
            subject.login(token);

            // 4 登录成功获取user对象.保存到session
            User user = (User) subject.getPrincipal();

            // ==============微信将openid保存到数据库中===================
            user.setOpenid(openid);
            userService.update(user);
            // ==============微信将openid保存到数据库中===================

            //  根据用户user查询对应的 权限列表 保存到session
            List<Module> modules = userService.findModuleByUser(user);
            session.setAttribute("modules", modules);

            // 用户名密码
            user.setPassword(password);
            session.setAttribute("loginUser", user);

            // 跳转主页面
            return "redirect:/home/main.do";
        } catch (Exception e) {
            request.setAttribute("error", "用户绑定失败，用户名或者密码错误。");
            return "forward:bindLogin.jsp";
        }

    }

    // 微信免密登录
    @RequestMapping(value="/avoidCodeLogin")
    public String avoidCodeLogin(){
        // 0 获取session中的用户信息 wxUser
        User wxUser = (User) session.getAttribute("wxUser");
        String email = wxUser.getEmail();
        String password = wxUser.getPassword();
        String openid = wxUser.getOpenid();

        // 1 封装email和password到Token（令牌）
        AuthenticationToken token = new UsernamePasswordToken(email, password);

        // 2 获取subject对象
        Subject subject = SecurityUtils.getSubject();

        try {
            // 3 使用subject.login(token)方法
            subject.login(token);

            // 4 登录成功获取user对象.保存到session
            User user = (User) subject.getPrincipal();

            // 用户名密码
            User loginUser = userService.findByEmail(email);
            loginUser.setPassword(password);
            session.setAttribute("loginUser", loginUser);

            //  根据用户user查询对应的 权限列表 保存到session
            List<Module> modules = userService.findModuleByUser(user);
            session.setAttribute("modules", modules);

            // 跳转主页面
            return "redirect:/home/main.do";
        } catch (Exception e) {
            request.setAttribute("error", "微信登录失败。");
            return "forward:login.jsp";
        }
    }

    // 获取短信验证码
    @ResponseBody
    @RequestMapping(value="/codeLogin")
    public ResultInfo codeLogin(String ajaxSendSms, String telephone){
        // 2 生成6位随机数
        String smsCode = RandomStringUtils.randomNumeric(6);

        // 3 调用service发送
        // ResultInfo resultInfo = userService.sendSms(telephone, smsCode);

        // 4 随机数设置session中
        if (smsCode == null) {
            smsCode = "";
        }
        session.setAttribute("smsCode", smsCode);

        System.out.println("验证码为：" + smsCode);

        return new ResultInfo(true, "发送成功");
    }

}
