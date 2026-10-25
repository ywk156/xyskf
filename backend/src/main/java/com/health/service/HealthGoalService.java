package com.health.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.health.entity.HealthGoal;

import java.util.List;

public interface HealthGoalService extends IService<HealthGoal> {
    List<HealthGoal> getUserGoals(Long userId);
    HealthGoal createGoal(HealthGoal goal);
    HealthGoal updateGoal(HealthGoal goal);
}
