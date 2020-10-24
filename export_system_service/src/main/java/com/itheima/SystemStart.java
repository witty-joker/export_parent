package com.itheima;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class SystemStart {
    /*
        可以和export_manager_web一样配置tomcat,需要修改端口号，
        上课时嫌这种方式麻烦，所以在开发或测试阶段可以使用main方法
        的方式启动spring的IOC容器，
     */
    public static void main(String[] args) throws IOException {
        // 加载配置文件，启动容器
        ClassPathXmlApplicationContext app = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext-*.xml");
        app.start();
        System.in.read(); //等待控制台回车。如果不回车就一直卡这儿不继续

    }
}
