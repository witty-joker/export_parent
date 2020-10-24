package com.itheima.test;

import com.itheima.dao.company.CompanyDao;
import com.itheima.domain.company.Company;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-dao.xml")
public class DaoTest {

    @Autowired
    private CompanyDao companyDao;

    @Test
    public void test01() throws Exception {
        List<Company> companyList = companyDao.findAll();

        for (Company company : companyList) {
            System.out.println(company);
        }

    }

}
