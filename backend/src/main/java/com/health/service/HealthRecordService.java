package com.health.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.health.entity.HealthRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface HealthRecordService extends IService<HealthRecord> {
    HealthRecord getTodayRecord(Long userId);
    List<HealthRecord> getRecentRecords(Long userId, int days);
    Map<String, Object> getHealthStats(Long userId);
    HealthRecord saveOrUpdateRecord(HealthRecord record);
}
