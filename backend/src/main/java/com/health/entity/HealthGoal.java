package com.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("health_goal")
public class HealthGoal {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String goalType;
    private BigDecimal targetValue;
    private BigDecimal currentValue;
    private String unit;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
