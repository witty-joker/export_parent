package com.itheima.domain.system;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

// 日志
@Data
public class SysLog implements Serializable {
    private String id;  // 主键
    private String userName;    // session中获取
    private String ip;  // request中获取
    private Date time;  // 当前时间
    private String method;  // 方法名，通过环绕通知的切点获取到
    private String action;  // 操作、通过环绕通知的切点可以获取到
    private String companyId;   // session 获取
    private String companyName; // session 获取
}