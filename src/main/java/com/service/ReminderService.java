package com.service;

import com.entity.ReminderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 提醒服务类
 */
@Service
public class ReminderService {

    @Autowired
    private JdbcTemplate jdbcTemplate; // 使用 Spring JDBC

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(ReminderService.class);

    /**
     * 创建提醒
     *
     * @param reminder 提醒实体对象
     */
    public void createReminder(ReminderEntity reminder) {
        String sql = "INSERT INTO reminder (username, reminder_detail, reminder_time, status) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, reminder.getUsername(), reminder.getReminderDetail(), reminder.getReminderTime(), "PENDING");
    }

    /**
     * 发送提醒
     * 定时任务调用此方法，检查并发送符合条件的提醒
     */
    public void sendReminders() {
        logger.info("正在发送提醒...");
        Date now = new Date();
        String sql = "SELECT * FROM reminder WHERE reminder_time <= ? AND status = 'PENDING'";

        List<ReminderEntity> reminders = jdbcTemplate.query(sql, new Object[]{now}, new RowMapper<ReminderEntity>() {
            @Override
            public ReminderEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReminderEntity reminder = new ReminderEntity();
                reminder.setId(rs.getLong("id"));
                reminder.setUsername(rs.getString("username"));
                reminder.setReminderDetail(rs.getString("reminder_detail"));
                reminder.setReminderTime(rs.getTimestamp("reminder_time"));
                reminder.setStatus(rs.getString("status"));
                return reminder;
            }
        });

        for (ReminderEntity reminder : reminders) {
            String email = getEmailByUsername(reminder.getUsername());
            logger.info("获取到的邮箱地址: {}", email);

            if (email == null || email.isEmpty()) {
                logger.error("无效的邮箱地址: {}", reminder.getUsername());
                continue;
            }

            String subject = "提醒事项: " + reminder.getReminderDetail();
            String text = "您的提醒事项: " + reminder.getReminderDetail() + " 时间已到。";

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/intelligent-health-monitoring-demo/yonghu/sendMail")
                    .queryParam("toEmail", email)
                    .queryParam("subject", subject)
                    .queryParam("text", text);

            String result = restTemplate.getForObject(builder.toUriString(), String.class);
            logger.info("邮件发送结果: {}", result);

            if (result.contains("error")) {
                logger.error("邮件发送失败: {}", result);
            } else {
                String updateSql = "UPDATE reminder SET status = 'SENT' WHERE id = ?";
                jdbcTemplate.update(updateSql, reminder.getId());
            }
        }
    }

    /**
     * 根据用户名获取邮箱
     *
     * @param username 用户名
     * @return 用户邮箱
     */
    private String getEmailByUsername(String username) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/intelligent-health-monitoring-demo/yonghu/getEmailByUsername")
                .queryParam("username", username);

        return restTemplate.getForObject(builder.toUriString(), String.class);
    }
}