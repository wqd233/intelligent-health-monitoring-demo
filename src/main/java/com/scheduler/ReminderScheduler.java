package com.scheduler;

import com.service.ReminderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务类，用于定期扫描并发送提醒
 */
@Component
public class ReminderScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ReminderScheduler.class);

    @Autowired
    private ReminderService reminderService;

    /**
     * 每分钟执行一次，发送提醒
     */
    @Scheduled(fixedRate = 30000) // 60000 毫秒 = 1 分钟
    public void sendReminders() {
        logger.info("开始执行定时任务：发送提醒");
        reminderService.sendReminders();
        logger.info("定时任务执行完毕：发送提醒");
    }
}
