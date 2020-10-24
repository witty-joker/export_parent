package com.itheima.web.aspect;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.domain.system.SysLog;
import com.itheima.web.controller.BaseController;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import com.itheima.service.system.SysLogService;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.UUID;

// 记录日志
@Aspect     // 定义切面aop
@Component  // 交给容器di
public class WriteLog extends BaseController {

    @Reference
    private SysLogService sysLogService;

    // 环绕通知 com.itheima.web.controller   切面的位置
    @Around("execution(* com.itheima.web.controller.*.*.*(..))")
    // ProceedingJoinPoint pjp 切点
    public Object log(ProceedingJoinPoint pjp) throws Throwable {

        // 创建日志对象
        SysLog sysLog = new SysLog();
        sysLog.setId(UUID.randomUUID().toString()); // id
        sysLog.setUserName(getUser().getUserName());    // userName
        sysLog.setIp(request.getRemoteAddr());  // Ip访问地址
        sysLog.setTime(new Date()); // 访问时间

        // 这里就是切点方法
        // MethodSignature 里面存储的就是当前切点方法中所有的信息： getSignature 获取签名（代表获取方法上所有的信息）
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();

        // 方法对象
        Method method = methodSignature.getMethod();
        sysLog.setMethod(method.getName()); // 根据方法对象方法名

        // 如果方法上存在RequestMapping注解，就获取RequestMapping注解中name属性的值
        // isAnnotationPresent 判断方法上是否有该注解。有就执行
        if (method.isAnnotationPresent(RequestMapping.class)) {

            // getAnnotation 获取注解 。RequestMapping 注解
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);

            sysLog.setAction(requestMapping.name());
        }
        sysLog.setCompanyId(getCompanyId());
        sysLog.setCompanyName(getCompanyName());

        // 日志添加进数据库
        sysLogService.save(sysLog);

        // 调用原方法逻辑
        return pjp.proceed();

    }
}
