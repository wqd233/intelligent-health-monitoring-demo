package com.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * 提醒实体类
 */
public class ReminderEntity {
    private Long id; // 主键
//    private String username; // 用户名
//    private String reminderDetail; // 提醒内容
//    private Date reminderTime; // 提醒时间
//    private String status; // 提醒状态（PENDING/SENT）
@NotBlank(message = "用户名不能为空")
@Size(max = 50, message = "用户名最长50个字符")
private String username;

    @NotBlank(message = "提醒内容不能为空")
    @Size(max = 500, message = "提醒内容最长500个字符")
    private String reminderDetail;

    @NotNull(message = "提醒时间不能为空")
    private Date reminderTime;

    @NotBlank(message = "状态不能为空")
    @Pattern(regexp = "PENDING|SENT", message = "状态值只能是PENDING或SENT")
    private String status;

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
