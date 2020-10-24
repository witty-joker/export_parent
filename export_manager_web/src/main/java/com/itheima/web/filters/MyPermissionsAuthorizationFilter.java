package com.itheima.web.filters;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/*
 自定义Shiro过滤器
    1 继承AuthorizationFilter
 */
public class MyPermissionsAuthorizationFilter extends AuthorizationFilter {
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {

        // 获取Subject [用户和Shiro交互主体] 有当前用户的权限 ["企业管理", "新增部门", "删除部门"]
        Subject subject = getSubject(request, response);

        // 获取perms 定义的过滤权限标识数组  有访问需要的权限
        String[] perms = (String[]) mappedValue;

        // 普通变量 用户封装boolean值
        boolean isPermitted = true;

        // 有权限标识就进入 ["部门管理", "新增部门", "删除部门"]
        if (perms != null && perms.length > 0) {
            // 一个权限标识
            if (perms.length == 1) {
                if (!subject.isPermitted(perms[0])) {
                    isPermitted = false;
                }
            } else {
                // 多个权限标识符
                for (String perm : perms) {
                    if (subject.isPermitted(perm)) {
                        isPermitted = true;
                    }
                }
            }
        }

        // 没有配置权限标识，放行
        return isPermitted;
    }
}
