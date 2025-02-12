package com.controller;

import com.annotation.IgnoreAuth;
import com.entity.ReminderEntity;
import com.service.ReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.*;

import java.beans.PropertyEditorSupport;
import java.time.Instant;
import java.time.format.DateTimeParseException;


/**
 * 提醒控制器类
 */
@RestController
@RequestMapping("/reminder")
public class ReminderController {

    @Autowired
    private ReminderService reminderService;

    // 获取提醒列表（新增）
    @IgnoreAuth
    @GetMapping("/list")
    public Map<String, Object> getList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        // 调用服务层方法获取提醒列表，不传入状态参数
        List<ReminderEntity> list = reminderService.getList(page, size);
        // 调用服务层方法统计提醒总数，不传入状态参数
        int total = reminderService.count();

        return new HashMap<String, Object>(){{
            put("code", 200);
            put("data", list);
            put("total", total);
        }};
    }

    // 更新提醒（新增）
    @IgnoreAuth
    @PostMapping("/update")
    public Map<String, Object> update(@RequestBody ReminderEntity reminder) {
        int count = reminderService.updateReminder(reminder);
        return Collections.singletonMap("code", count > 0 ? 200 : 500);
    }

    // 删除提醒（新增）
    @IgnoreAuth
    @PostMapping("/delete/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        int count = reminderService.deleteById(id);
        return Collections.singletonMap("code", count > 0 ? 200 : 500);
    }

    // 状态变更（新增）
    @IgnoreAuth
    @PostMapping("/status")
    public Map<String, Object> changeStatus(
            @RequestParam Long id,
            @RequestParam String status) {

        int count = reminderService.updateStatus(id, status);
        return Collections.singletonMap("code", count > 0 ? 200 : 500);
    }

    // 保持原有创建接口
    @IgnoreAuth
    // 修改create方法
    @PostMapping("/create")
    public Map<String, Object> create(@Valid @RequestBody ReminderEntity reminder) {
        reminderService.createReminder(reminder);
        return Collections.singletonMap("code", 200);
    }


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // 处理ISO8601带时区时间格式
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                try {
                    Instant instant = Instant.parse(text);
                    setValue(Date.from(instant));
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("时间格式错误，请使用ISO8601格式");
                }
            }
        });
    }
}

