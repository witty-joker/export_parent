package com.itheima.mail.listener;

import com.alibaba.fastjson.JSONObject;
import com.itheima.utils.MailUtil;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

import java.util.Map;

// 消费者方监听器
// ChannelAwareMessageListener 是手动回执
public class MailListener implements ChannelAwareMessageListener {

    // 接收消息，发送邮件
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        // 1 获取回执参数  jSONObject 阿里的
        Map map = JSONObject.parseObject(message.getBody(), Map.class);

        // 2 读取map参数
        String to = (String) map.get("to");
        String title = (String) map.get("title");
        String content = (String) map.get("content");

        // 3 发送邮件
        MailUtil.sendMail(to, title, content);
        System.out.println(to + ":邮件发送成功");

        // 4 当消息正常消费完毕之后,使用代码手动发送回执  deliveryTag(交货标签)  multiple
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}

/*// 使用MessageListener 是自动回执
public class MailListener implements MessageListener {

    // 接收消息，发送邮件
    @Override
    public void onMessage(Message message) {
        // 1 获取回执参数  jSONObject 阿里的
        Map map = JSONObject.parseObject(message.getBody(), Map.class);

        // 2 读取map参数
        String to = (String) map.get("to");
        String title = (String) map.get("title");
        String content = (String) map.get("content");

        // 3 发送邮件
        MailUtil.sendMail(to, title, content);
    }
}*/
