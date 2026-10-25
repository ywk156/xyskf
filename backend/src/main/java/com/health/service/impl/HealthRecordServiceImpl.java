package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.entity.HealthRecord;
import com.health.mapper.HealthRecordMapper;
import com.health.service.HealthRecordService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HealthRecordServiceImpl extends ServiceImpl<HealthRecordMapper, HealthRecord> implements HealthRecordService {

    @Override
    public HealthRecord getTodayRecord(Long userId) {
        return getOne(new LambdaQueryWrapper<HealthRecord>()
                .eq(HealthRecord::getUserId, userId)
                .eq(HealthRecord::getRecordDate, LocalDate.now()));
    }

    @Override
    public List<HealthRecord> getRecentRecords(Long userId, int days) {
        LocalDate startDate = LocalDate.now().minusDays(days - 1);
        return list(new LambdaQueryWrapper<HealthRecord>()
                .eq(HealthRecord::getUserId, userId)
                .ge(HealthRecord::getRecordDate, startDate)
                .orderByAsc(HealthRecord::getRecordDate));
    }

    @Override
    public Map<String, Object> getHealthStats(Long userId) {
        List<HealthRecord> records = getRecentRecords(userId, 7);
        Map<String, Object> stats = new HashMap<>();

        int totalSteps = 0;
        int totalCalories = 0;
        BigDecimal totalDistance = BigDecimal.ZERO;
        BigDecimal totalSleep = BigDecimal.ZERO;
        int sleepCount = 0;
        int totalWater = 0;
        int recordCount = records.size();

        for (HealthRecord r : records) {
            totalSteps += r.getSteps() != null ? r.getSteps() : 0;
            totalCalories += r.getCalories() != null ? r.getCalories() : 0;
            totalDistance = totalDistance.add(r.getDistance() != null ? r.getDistance() : BigDecimal.ZERO);
            totalWater += r.getWaterIntake() != null ? r.getWaterIntake() : 0;
            if (r.getSleepDuration() != null) {
                totalSleep = totalSleep.add(r.getSleepDuration());
                sleepCount++;
            }
        }

        stats.put("totalSteps", totalSteps);
        stats.put("avgSteps", recordCount > 0 ? totalSteps / recordCount : 0);
        stats.put("totalCalories", totalCalories);
        stats.put("avgCalories", recordCount > 0 ? totalCalories / recordCount : 0);
        stats.put("totalDistance", totalDistance.setScale(2, RoundingMode.HALF_UP));
        stats.put("avgDistance", recordCount > 0 ? totalDistance.divide(BigDecimal.valueOf(recordCount), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        stats.put("avgSleep", sleepCount > 0 ? totalSleep.divide(BigDecimal.valueOf(sleepCount), 1, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        stats.put("totalWater", totalWater);
        stats.put("avgWater", recordCount > 0 ? totalWater / recordCount : 0);
        stats.put("recordCount", recordCount);

        return stats;
    }

    @Override
    public HealthRecord saveOrUpdateRecord(HealthRecord record) {
        HealthRecord exist = getOne(new LambdaQueryWrapper<HealthRecord>()
                .eq(HealthRecord::getUserId, record.getUserId())
                .eq(HealthRecord::getRecordDate, record.getRecordDate()));
        if (exist != null) {
            record.setId(exist.getId());
            updateById(record);
        } else {
            save(record);
        }
        return record;
    }
}
