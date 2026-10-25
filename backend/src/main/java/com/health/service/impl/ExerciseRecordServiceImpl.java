package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.entity.ExerciseRecord;
import com.health.mapper.ExerciseRecordMapper;
import com.health.service.ExerciseRecordService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExerciseRecordServiceImpl extends ServiceImpl<ExerciseRecordMapper, ExerciseRecord> implements ExerciseRecordService {

    @Override
    public List<ExerciseRecord> getRecentRecords(Long userId, int days) {
        LocalDate startDate = LocalDate.now().minusDays(days - 1);
        return list(new LambdaQueryWrapper<ExerciseRecord>()
                .eq(ExerciseRecord::getUserId, userId)
                .ge(ExerciseRecord::getExerciseDate, startDate)
                .orderByDesc(ExerciseRecord::getExerciseDate));
    }

    @Override
    public Map<String, Object> getExerciseStats(Long userId) {
        List<ExerciseRecord> records = getRecentRecords(userId, 30);
        Map<String, Object> stats = new HashMap<>();

        int totalDuration = 0;
        int totalCalories = 0;
        BigDecimal totalDistance = BigDecimal.ZERO;
        int totalCount = records.size();

        for (ExerciseRecord r : records) {
            totalDuration += r.getDuration() != null ? r.getDuration() : 0;
            totalCalories += r.getCalories() != null ? r.getCalories() : 0;
            totalDistance = totalDistance.add(r.getDistance() != null ? r.getDistance() : BigDecimal.ZERO);
        }

        stats.put("totalDuration", totalDuration);
        stats.put("totalCalories", totalCalories);
        stats.put("totalDistance", totalDistance.setScale(2, RoundingMode.HALF_UP));
        stats.put("totalCount", totalCount);
        stats.put("avgDuration", totalCount > 0 ? totalDuration / totalCount : 0);

        // 本周运动次数
        LocalDate weekStart = LocalDate.now().minusDays(6);
        long weekCount = records.stream()
                .filter(r -> !r.getExerciseDate().isBefore(weekStart))
                .count();
        stats.put("weekCount", weekCount);

        return stats;
    }

    @Override
    public List<Map<String, Object>> getExerciseTypeStats(Long userId) {
        List<ExerciseRecord> records = getRecentRecords(userId, 30);
        Map<String, List<ExerciseRecord>> grouped = records.stream()
                .collect(Collectors.groupingBy(ExerciseRecord::getExerciseType));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<ExerciseRecord>> entry : grouped.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", entry.getKey());
            item.put("count", entry.getValue().size());
            int totalDuration = entry.getValue().stream()
                    .mapToInt(r -> r.getDuration() != null ? r.getDuration() : 0)
                    .sum();
            item.put("totalDuration", totalDuration);
            result.add(item);
        }
        return result;
    }
}
