package com.health.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.health.entity.ExerciseRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ExerciseRecordService extends IService<ExerciseRecord> {
    List<ExerciseRecord> getRecentRecords(Long userId, int days);
    Map<String, Object> getExerciseStats(Long userId);
    List<Map<String, Object>> getExerciseTypeStats(Long userId);
}
