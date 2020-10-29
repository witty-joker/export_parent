package com.itheima.web.controller.message;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.itheima.domain.cargo.Message;
import com.itheima.domain.cargo.MessageExample;
import com.itheima.service.cargo.MessageService;
import com.itheima.service.stat.StatService;
import com.itheima.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

// 信息管理
@Controller
@RequestMapping("/manage")  // 改一下
public class MessageController extends BaseController {

    @Reference
    private MessageService messageService;

    // 邮件查询
    @RequestMapping(value = "/message", name = "邮件查询")
    public String message(
            @RequestParam(defaultValue = "1", name = "page") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize) {
        /*
            查询数据库中的数据：
                1 调用MessageService中的list数据
                2 保存到request域中。请求转发到前端页面
         */
        // 1 调用MessageService中的list数据 获取信息用例messageExample
        MessageExample messageExample = new MessageExample();

        // 设置用例，分页查询
        PageInfo pageInfo = messageService.findByPage(pageNum, pageSize, messageExample);

        // 2 保存到request域中。请求转发到前端页面
        request.setAttribute("page", pageInfo);

        return "/system/log/log-message";
    }


}
