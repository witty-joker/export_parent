package com.itheima.domain.cargo;

import com.itheima.domain.BaseEntity;

import java.io.Serializable;

public class Message extends BaseEntity implements Serializable {
    private String mid;

    private String name;

    private String sender;

    private String pink;

    private String time;

    private String content;

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid == null ? null : mid.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender == null ? null : sender.trim();
    }

    public String getPink() {
        return pink;
    }

    public void setPink(String pink) {
        this.pink = pink == null ? null : pink.trim();
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time == null ? null : time.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", mid=").append(mid);
        sb.append(", name=").append(name);
        sb.append(", sender=").append(sender);
        sb.append(", pink=").append(pink);
        sb.append(", time=").append(time);
        sb.append(", content=").append(content);
        sb.append("]");
        return sb.toString();
    }
}