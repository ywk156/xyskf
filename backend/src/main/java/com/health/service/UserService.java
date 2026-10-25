package com.health.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.health.dto.LoginDTO;
import com.health.entity.User;

import java.util.Map;

public interface UserService extends IService<User> {
    Map<String, Object> login(LoginDTO loginDTO);
    User register(User user);
    User getCurrentUser(Long userId);
    User updateProfile(User user);
}
