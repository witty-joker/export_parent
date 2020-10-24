package com.itheima.web.utils;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.service.system.UserService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/*
原因: Realm先于dubbo创建在。而创建的时候就会去dubbo中获取service对象, 所以获取不到

解决方案: 从Dubbo中获取userService获取后交给spring容器管理。
SaasRelm使用的时候再从spring容器中获取。

解决SaasRelm先创建的工具类
 */
@Component
public class SpringUtil implements ApplicationContextAware {

    @Reference
    private UserService userService;

    @Bean
    public UserService userService() {
        return userService;
    }

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringUtil.applicationContext == null) {
            SpringUtil.applicationContext = applicationContext;
        }
    }

    //获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    //通过name获取 Bean.
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }
}
