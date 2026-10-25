package com.health.controller;

import com.health.common.Result;
import com.health.entity.HealthGoal;
import com.health.service.HealthGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/goal")
public class HealthGoalController {

    @Autowired
    private HealthGoalService healthGoalService;

    @GetMapping("/list")
    public Result<List<HealthGoal>> getUserGoals(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(healthGoalService.getUserGoals(userId));
    }

    @PostMapping("/save")
    public Result<HealthGoal> createGoal(@RequestBody HealthGoal goal, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        goal.setUserId(userId);
        return Result.success(healthGoalService.createGoal(goal));
    }

    @PutMapping("/update")
    public Result<HealthGoal> updateGoal(@RequestBody HealthGoal goal) {
        return Result.success(healthGoalService.updateGoal(goal));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteGoal(@PathVariable Long id) {
        healthGoalService.removeById(id);
        return Result.success();
    }
}
