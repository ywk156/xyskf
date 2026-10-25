package com.health.controller;

import com.health.common.Result;
import com.health.entity.HealthRecord;
import com.health.service.HealthRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthRecordController {

    @Autowired
    private HealthRecordService healthRecordService;

    @GetMapping("/today")
    public Result<HealthRecord> getTodayRecord(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(healthRecordService.getTodayRecord(userId));
    }

    @GetMapping("/recent")
    public Result<List<HealthRecord>> getRecentRecords(@RequestParam(defaultValue = "7") int days, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(healthRecordService.getRecentRecords(userId, days));
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getHealthStats(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(healthRecordService.getHealthStats(userId));
    }

    @PostMapping("/save")
    public Result<HealthRecord> saveRecord(@RequestBody HealthRecord record, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        record.setUserId(userId);
        return Result.success(healthRecordService.saveOrUpdateRecord(record));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteRecord(@PathVariable Long id) {
        healthRecordService.removeById(id);
        return Result.success();
    }
}
