package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.entity.HealthGoal;
import com.health.mapper.HealthGoalMapper;
import com.health.service.HealthGoalService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HealthGoalServiceImpl extends ServiceImpl<HealthGoalMapper, HealthGoal> implements HealthGoalService {

    @Override
    public List<HealthGoal> getUserGoals(Long userId) {
        return list(new LambdaQueryWrapper<HealthGoal>()
                .eq(HealthGoal::getUserId, userId)
                .orderByDesc(HealthGoal::getCreateTime));
    }

    @Override
    public HealthGoal createGoal(HealthGoal goal) {
        save(goal);
        return goal;
    }

    @Override
    public HealthGoal updateGoal(HealthGoal goal) {
        updateById(goal);
        return getById(goal.getId());
    }
}
