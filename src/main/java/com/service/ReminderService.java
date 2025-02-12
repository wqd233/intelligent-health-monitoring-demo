package com.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.entity.ReminderEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ReminderService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(ReminderService.class);

//    public void createReminder(ReminderEntity reminder) {
//        String sql = "INSERT INTO reminder (username, reminder_detail, reminder_time, status) VALUES (?, ?, ?, ?)";
//        jdbcTemplate.update(sql, reminder.getUsername(),
//                reminder.getReminderDetail(), reminder.getReminderTime(), "PENDING");
//    }
public void createReminder(ReminderEntity reminder) {
    // 修改后的SQL，使用传入的status值
    String sql = "INSERT INTO reminder (username, reminder_detail, reminder_time, status) VALUES (?, ?, ?, ?)";
    jdbcTemplate.update(sql,
            reminder.getUsername(),
            reminder.getReminderDetail(),
            reminder.getReminderTime(),
            reminder.getStatus()); // 使用传入的status值
}

    public void sendReminders() {
        // 获取当前时间
        Date now = new Date();
        // 定义日期格式化对象，格式与数据库 datetime 类型匹配
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTimeStr = sdf.format(now);
        logger.info("当前时间: {}", currentTimeStr);

        String sql = "SELECT * FROM reminder WHERE reminder_time <= ? AND status = 'PENDING'";

        List<ReminderEntity> reminders = jdbcTemplate.query(sql, new Object[]{currentTimeStr}, new RowMapper<ReminderEntity>() {
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

        // 展示筛选出多少条数据可以发送邮件
        int reminderCount = reminders.size();
        logger.info("筛选出 {} 条数据可以发送邮件", reminderCount);
        logger.info("筛选出的string: {}", reminders.toString());

        for (ReminderEntity reminder : reminders) {
            try {
                String email = getEmailByUsername(reminder.getUsername());
                logger.info("传入的username: {}", reminder.getUsername());
                if (email != null && !email.isEmpty()) {
                    logger.info("获取到的邮箱地址: {}", email);
                    sendEmail(reminder, email);
                    updateReminderStatus(reminder.getId());
                    // 展示哪一条的状态被修改
                    logger.info("已将提醒 ID 为 {} 的状态修改为 SENT", reminder.getId());
                } else {
                    logger.error("无效的邮箱地址: {}", reminder.getUsername());
                }
            } catch (Exception e) {
                logger.error("处理提醒时发生异常: {}", e.getMessage());
            }
        }
    }

    private void sendEmail(ReminderEntity reminder, String email) throws Exception {
        String subject = "提醒事项: " + reminder.getReminderDetail();
        String text = "您的提醒事项: " + reminder.getReminderDetail() + " 时间已到。";

        String url = UriComponentsBuilder
                .fromHttpUrl("http://localhost:8080/intelligent-health-monitoring-demo/yonghu/sendMail")
                .queryParam("toEmail", email)
                .queryParam("subject", subject)
                .queryParam("text", text)
                .toUriString();

        logger.info("邮件发送请求 URL: {}", url);

        String result = restTemplate.getForObject(url, String.class);
        logger.info("邮件发送结果: {}", result);

        if (result != null && result.contains("error")) {
            throw new RuntimeException("邮件发送失败: " + result);
        }
    }

    private void updateReminderStatus(Long id) {
        String updateSql = "UPDATE reminder SET status = 'SENT' WHERE id = ?";
        jdbcTemplate.update(updateSql, id);
    }

    private String getEmailByUsername(String username) {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl("http://localhost:8080/intelligent-health-monitoring-demo/yonghu/getEmailByUsername")
                    .queryParam("username", username)
                    .toUriString();

            String jsonResult = restTemplate.getForObject(url, String.class);
            logger.debug("邮箱查询响应: {}", jsonResult);

            JsonNode root = objectMapper.readTree(jsonResult);
            if (root.get("code").asInt() != 0) {
                logger.error("邮箱查询失败: {}", root.get("msg").asText());
                return null;
            }

            JsonNode emailNode = root.path("data").path("email");
            if (emailNode.isMissingNode() || emailNode.asText().isEmpty()) {
                logger.error("邮箱地址不存在于响应中");
                return null;
            }

            String email = emailNode.asText();
            logger.info("从 API 获取到的邮箱地址: {}", email);
            return email;
        } catch (Exception e) {
            logger.error("解析邮箱地址失败: {}", e.getMessage());
            return null;
        }
    }

    // 新增查询方法
    public List<ReminderEntity> getList(int page, int size) {
        String sql = "SELECT * FROM reminder WHERE status in ('SENT','PENDING') ORDER BY reminder_time DESC LIMIT ?,?";
        return jdbcTemplate.query(sql, new Object[]{ (page-1)*size, size}, (rs, rowNum) -> {
            ReminderEntity entity = new ReminderEntity();
            entity.setId(rs.getLong("id"));
            entity.setUsername(rs.getString("username"));
            entity.setReminderDetail(rs.getString("reminder_detail"));
            entity.setReminderTime(rs.getTimestamp("reminder_time"));
            entity.setStatus(rs.getString("status"));
            return entity;
        });
    }

    // 新增统计方法
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(1) FROM reminder WHERE status = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, status);
    }
    // 统计所有提醒的数量
    public int count() {
        String sql = "SELECT COUNT(1) FROM reminder";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    // 新增更新方法
    public int updateReminder(ReminderEntity reminder) {
        String sql = "UPDATE reminder SET reminder_detail = ?, reminder_time = ? WHERE id = ?";
        return jdbcTemplate.update(sql,
                reminder.getReminderDetail(),
                reminder.getReminderTime(),
                reminder.getId());
    }

    // 新增删除方法
    public int deleteById(Long id) {
        String sql = "DELETE FROM reminder WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    // 新增状态更新方法
    public int updateStatus(Long id, String status) {
        String sql = "UPDATE reminder SET status = ? WHERE id = ?";
        return jdbcTemplate.update(sql, status, id);
    }
}