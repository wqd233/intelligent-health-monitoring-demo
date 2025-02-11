package com.entity;

import java.util.Date;

/**
 * 提醒实体类
 */
public class ReminderEntity {
    private Long id; // 主键
    private String username; // 用户名
    private String reminderDetail; // 提醒内容
    private Date reminderTime; // 提醒时间
    private String status; // 提醒状态（PENDING/SENT）

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getReminderDetail() {
        return reminderDetail;
    }

    public void setReminderDetail(String reminderDetail) {
        this.reminderDetail = reminderDetail;
    }

    public Date getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(Date reminderTime) {
        this.reminderTime = reminderTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
