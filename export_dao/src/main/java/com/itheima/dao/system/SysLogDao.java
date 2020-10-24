package com.itheima.dao.system;

import com.itheima.domain.system.SysLog;
import java.util.List;

// 日志
public interface SysLogDao {
    List<SysLog> findAll(String companyId);
    
    void save(SysLog log);
}