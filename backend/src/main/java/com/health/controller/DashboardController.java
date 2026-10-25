package com.health.controller;

import com.health.common.Result;
import com.health.entity.ExerciseRecord;
import com.health.entity.HealthRecord;
import com.health.service.ExerciseRecordService;
import com.health.service.HealthRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private HealthRecordService healthRecordService;

    @Autowired
    private ExerciseRecordService exerciseRecordService;

    @GetMapping("/overview")
    public Result<Map<String, Object>> getDashboardOverview(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Object> data = new HashMap<>();

        // 今日健康数据
        HealthRecord today = healthRecordService.getTodayRecord(userId);
        data.put("today", today);

        // 近7天健康数据
        List<HealthRecord> weekRecords = healthRecordService.getRecentRecords(userId, 7);
        data.put("weekRecords", weekRecords);

        // 健康统计
        data.put("healthStats", healthRecordService.getHealthStats(userId));

        // 运动统计
        data.put("exerciseStats", exerciseRecordService.getExerciseStats(userId));

        // 运动类型分布
        data.put("exerciseTypeStats", exerciseRecordService.getExerciseTypeStats(userId));

        // 近30天运动记录
        List<ExerciseRecord> exerciseRecords = exerciseRecordService.getRecentRecords(userId, 30);
        data.put("exerciseRecords", exerciseRecords);

        return Result.success(data);
    }
}
