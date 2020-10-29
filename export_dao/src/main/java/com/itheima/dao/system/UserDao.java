package com.itheima.dao.system;

import com.itheima.domain.system.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 用户dao
public interface UserDao {

    // 查询所有
    List<User> findAll(String companyId);

    // 添加
    void save(User user);

    // 根据id查询
    User findById(String id);

    // 更新
    void update(User user);

    // 删除
    void delete(String id);

    // 跳转分配角色页面
    List<String> findRoleIdsByUserId(String id);

    // 删除该用户所有角色关联关系
    void deleteUserRoleByUserId(String userId);

    //  添加用户的角色
    void changeRole(@Param("userId") String userId, @Param("roleId") String roleId);

    // 根据email获取用户信息 返回user对象
    User findByEmail(String email);

    // 2.根据openId判断用户是否存在
    User findByOpenid(String openid);
}
