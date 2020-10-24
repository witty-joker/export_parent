package com.itheima.web.controller;

import com.itheima.domain.system.User;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// 抽取BaseController
public class BaseController {

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected HttpServletResponse response;

    @Autowired
    protected HttpSession session;

    /*//TODO 模拟一下企业id和企业name ，等到登录后，改为真实
    protected String getCompanyId() {
        return "1";
    }

    protected String getCompanyName() {
        return "大脸猫皮具外贸有限公司";
    }*/

    // 用户登录
    protected User getUser() {
        User user = (User) session.getAttribute("loginUser");

        if (user == null) {
            throw new RuntimeException("session查询超时。");
        }
        return user;
    }

    protected String getCompanyId() {
        return getUser().getCompanyId();
    }

    protected String getCompanyName() {
        return getUser().getCompanyName();
    }

}
