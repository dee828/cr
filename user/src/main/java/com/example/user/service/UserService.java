package com.example.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.user.entity.User;
import com.example.user.request.RegisterRequest;
import com.example.user.request.UserAddRequest;
import com.example.user.request.UserListRequest;
import com.example.user.request.UserUpdateRequest;
import com.example.common.response.PageResponse;
import com.example.user.response.UserResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface UserService extends IService<User> {
    Boolean register(RegisterRequest request);

    void ensureEmailNotRegistered(String email);

    PageResponse<UserResponse> getUserPage(UserListRequest request);

    UserResponse getUserById(Long id);

    Boolean addUser(UserAddRequest request);

    Boolean deleteUser(Long id);

    Boolean deleteBatch(List<Long> ids);

    Boolean updateUser(UserUpdateRequest request);
}
