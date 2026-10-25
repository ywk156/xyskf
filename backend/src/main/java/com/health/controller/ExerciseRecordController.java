package com.health.controller;

import com.health.common.Result;
import com.health.entity.ExerciseRecord;
import com.health.service.ExerciseRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/exercise")
public class ExerciseRecordController {

    @Autowired
    private ExerciseRecordService exerciseRecordService;

    @GetMapping("/recent")
    public Result<List<ExerciseRecord>> getRecentRecords(@RequestParam(defaultValue = "30") int days, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(exerciseRecordService.getRecentRecords(userId, days));
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getExerciseStats(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(exerciseRecordService.getExerciseStats(userId));
    }

    @GetMapping("/type-stats")
    public Result<List<Map<String, Object>>> getExerciseTypeStats(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(exerciseRecordService.getExerciseTypeStats(userId));
    }

    @PostMapping("/save")
    public Result<ExerciseRecord> saveRecord(@RequestBody ExerciseRecord record, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        record.setUserId(userId);
        if (record.getId() != null) {
            exerciseRecordService.updateById(record);
        } else {
            exerciseRecordService.save(record);
        }
        return Result.success(record);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteRecord(@PathVariable Long id) {
        exerciseRecordService.removeById(id);
        return Result.success();
    }
}
