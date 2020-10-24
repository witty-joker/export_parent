package com.itheima.web.controller.system;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.itheima.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.itheima.service.system.SysLogService;

// 查询日志
@Controller
@RequestMapping("/system/log")
public class SysLogController extends BaseController {

    @Reference
    private SysLogService sysLogService;

    @RequestMapping(value = "/list", name = "查询日志")
    public String list(
            @RequestParam(defaultValue = "1", name = "page") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        PageInfo pageInfo = sysLogService.findByPage(getCompanyId(), pageNum, pageSize);

        request.setAttribute("page", pageInfo);

        return "/system/log/log-list";
    }

}
