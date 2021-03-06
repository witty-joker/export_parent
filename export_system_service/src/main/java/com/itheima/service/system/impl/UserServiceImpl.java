package com.itheima.service.system.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.dao.system.ModuleDao;
import com.itheima.dao.system.UserDao;
import com.itheima.domain.ResultInfo;
import com.itheima.domain.system.Module;
import com.itheima.domain.system.User;
import com.itheima.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.service.system.UserService;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;

// userService 部门service
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private ModuleDao moduleDao;

    // 查询所有
    @Override
    public List<User> findAll(String companyId) {
        return userDao.findAll(companyId);
    }

    // 添加
    @Override
    public void save(User user) {
        userDao.save(user);
    }

    // 根据id查询
    @Override
    public User findById(String id) {
        return userDao.findById(id);
    }

    // 更新修改
    @Override
    public void update(User user) {
        userDao.update(user);
    }

    // 删除
    @Override
    public void delete(String id) {
        userDao.delete(id);
    }

    // 分页查询
    @Override
    public PageInfo findByPage(String companyId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = userDao.findAll(companyId);

        return new PageInfo(list, 10);
    }

    // 跳转分配角色页面
    @Override
    public List<String> findRoleIdsByUserId(String id) {

        return userDao.findRoleIdsByUserId(id);
    }

    // 分配角色
    @Override
    public void changeRole(String userId, String[] roleIds) {
        /*
            1 删除用户与角色所有的关联（中间表）
            2 添加新的关联信息
         */
        // 1 删除用户与角色所有的关联（中间表）
        userDao.deleteUserRoleByUserId(userId);

        // 2 添加新的关联信息 用户 = 角色
        if (roleIds != null && roleIds.length > 0) {
            for (String roleId : roleIds) {
                // 添加用户的角色
                userDao.changeRole(userId, roleId);
            }
        }

    }

    // 根据email获取用户信息 返回user对象
    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    // 根据用户user查询对应的权限
    @Override
    public List<Module> findModuleByUser(User user) {
        // 1 确定身份
        Integer degree = user.getDegree();

        // 2 查找权限
        if (degree == 0) {  // saas 管理员
            return moduleDao.findByBelong(0);
        } else if (degree == 1) {   // 企业管理员
            return moduleDao.findByBelong(1);
        } else {    // 企业普通员工
            return moduleDao.findByUserId(user.getId());
        }
    }

    // 通过微信code登录
    // 微信登录参数
    @Value("${wx.appid}")
    private String appid;
    @Value("${wx.secret}")
    private String secret;
    @Value("${wx.accessTokenUrl}")
    private String accessTokenUrl;
    @Value("${wx.wxInfoUrl}")
    private String wxInfoUrl;

    // 微信登录逻辑
    @Override
    public User wxLogin(String code) {
        User user = null;
        //1.[微信获取openid失败拦截] 根据code获取access_token和openId 。
        String atUtl = accessTokenUrl + "?code="+code+"&appid="+appid+"&secret="+secret+"&grant_type=authorization_code";
        System.out.println(atUtl);

        Map<String, Object> map = HttpUtils.sendGet(atUtl);
        Object access_token = map.get("access_token");
        Object openid = map.get("openid").toString();
        if(access_token == null && openid == null) {
            // 2 当查询不到当前用户的openid时 ，返回为空[微信获取openid失败]
            return user;
        }

        // 2.根据openId判断用户是否存在
        user = userDao.findByOpenid((String) openid);
        if (user != null) {
            // 用户存在 返回用户信息
            return user;
        } else {
            // 用户不存在，需要绑定用户
            // 1 存入openid
            user = new User();
            user.setOpenid((String) openid);

            // 2 返回user对象
            return user;
        }
    }

}
