package com.itheima.web.realm;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.domain.system.Module;
import com.itheima.domain.system.User;
import com.itheima.web.utils.SpringUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import com.itheima.service.system.UserService;

import java.util.List;

// 自定义Realm域（查询数据库或者配置文件）
public class SaasRealm extends AuthorizingRealm {

    /*
        现象: 在SaasRelm中, 使用@Refrence注入的userService为空

        原因: Realm先于dubbo创建在。而创建的时候就会去dubbo中
            获取service对象, 所以获取不到

        解决方案: 从Dubbo中获取userService获取后交给spring容器管理。
            SaasRelm使用的时候再从spring容器中获取。
     */
    // @Reference
    // private UserService userService;

    // 认证数据准备 doGetAuthenticationInfo 获取认证信息
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.printf("=========================认证=========================");
        /*
        登录查询：
            1 获取email
            2 根据email获取用户user
            3 获取密码password
         */
        // 1 获取email    authentication Token 认证令牌
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;
        // 通过令牌获取email
        String email = usernamePasswordToken.getUsername();

        // 2 根据email获取用户user
        // 使用工具类，让userService在使用时创建
        UserService userService = (UserService) SpringUtil.getBean("userService");

        User user = userService.findByEmail(email);

        // 3 获取密码password
        if (user == null) {
            return new SimpleAuthenticationInfo();
        }

        /*
            Object principal,   主角  user
            Object credentials, 密码  user.getPassword()
            String realmName,   单签realm名称  this.getName()
         */
        // 将信息发送到Shiro框架中
        return new SimpleAuthenticationInfo(user, user.getPassword(), this.getName());
    }

    // 授权数据准备 doGetAuthorizationInfo 获取授权信息
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.printf("=========================授权=========================");
        /*
            授权当前用户的权限信息：
                1 获取user用户信息
                2 使用userService查询当前用户的权限信息 list<Module>
                3 遍历集合将权限信息交给Shiro安全框架
                4 返回simpleAuthorizationInfo
         */
        // 1 获取user用户信息
        User user = (User) principalCollection.getPrimaryPrincipal();

        // 2 使用userService查询当前用户的权限信息 list<Module>
        // 使用工具类，让userService在使用时创建
        UserService userService = (UserService) SpringUtil.getBean("userService");

        List<Module> moduleList = userService.findModuleByUser(user);

        // 3 遍历集合将权限信息交给Shiro安全框架
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        for (Module module : moduleList) {
            // addStringPermission  存入String类型到session
            simpleAuthorizationInfo.addStringPermission(module.getName());
        }

        // 打印获取的用户权限信息
        System.out.println(simpleAuthorizationInfo.getStringPermissions());

        return simpleAuthorizationInfo;
    }
}
