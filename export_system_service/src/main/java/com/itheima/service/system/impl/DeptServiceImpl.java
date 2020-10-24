package com.itheima.service.system.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.dao.system.DeptDao;
import com.itheima.domain.system.Dept;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.itheima.service.system.DeptService;

import java.util.List;
import java.util.UUID;

// deptService 部门service
@Service
public class DeptServiceImpl implements DeptService {

    @Autowired
    private DeptDao deptDao;

    // 查询所有
    @Override
    public List<Dept> findAll(String companyId) {
        return deptDao.findAll(companyId);
    }

    // 添加
    @Override
    public void save(Dept dept) {
        deptDao.save(dept);
    }

    // 根据id查询
    @Override
    public Dept findById(String id) {
        return deptDao.findById(id);
    }

    // 更新修改
    @Override
    public void update(Dept dept) {
        deptDao.update(dept);
    }

    // 删除
    @Override
    public void deleteById(String id) {
        deptDao.deleteById(id);
    }

    // 分页查询
    @Override
    public PageInfo findByPage(String companyId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Dept> list = deptDao.findAll(companyId);

        return new PageInfo(list, 10);
    }

    // 删除部门验证是否有子
    @Override
    public List<Dept> findChildrenDept(String id) {
        return deptDao.findChildrenDept(id);
    }

    // 修改id的修改dept部门
    @Override
    public void updateAmendId(Dept dept, int i, int j) {
         /*
            根据父部门进行修改：
                1 根据id删除该对象数据库中的属性
                2 获取前端传递的数据对象，修改id值
                3 保存前端传递的数据对象
          */
        // 根据id查询该数据信息，如果父id没有改变则id值不变，仅仅修改
        // a 查询该数据
        Dept deptData = deptDao.findById(dept.getId());

        // 判断父id是否相同，相同就修改其他值
        if (StringUtils.equals(deptData.getCompanyId(), dept.getCompanyId())) {
            // 相同
            deptDao.update(dept);
            return;
        }

        // 父部门改变
        // 1 根据id删除该对象数据库中的属性
        deptDao.deleteById(dept.getId());

        // 2 获取前端传递的数据对象，修改id值
        // a 获取父部门id parentId
        String parentId = dept.getParent().getId();

        // b 判断修改id
        // 判断父部门id是否为null
        if (StringUtils.isEmpty(parentId)) {
            // 无父部门
            dept.setId("10" + i);
        } else {
            // 有父部门
            dept.setId(parentId + "10" + j);
        }

        // 3 保存新增前端传递的数据对象
        deptDao.save(dept);

    }

}
