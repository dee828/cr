package com.example.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.core.UserContext;
import com.example.common.exception.CustomValidationException;
import com.example.user.entity.User;
import com.example.user.mapper.UserMapper;
import com.example.user.request.RegisterRequest;
import com.example.user.request.UserAddRequest;
import com.example.user.request.UserListRequest;
import com.example.user.request.UserUpdateRequest;
import com.example.common.response.PageResponse;
import com.example.user.response.UserResponse;
import com.example.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService{

    @Override
    public Boolean register(RegisterRequest request) {
        // 先验证邮箱有没有被注册
        // 谁谁谁帮我验证邮箱是否注册
        this.ensureEmailNotRegistered(request.getEmail());

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // todo：密码一定要加密
        user.setCreatedAt(LocalDateTime.now());

        return this.save(user);
    }

    public void ensureEmailNotRegistered(String email){
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("email", email);
        Optional<User> userOpt = this.getOneOpt(qw);
        if (userOpt.isPresent()){
            // 如果存在就是邮箱已被注册
            throw new CustomValidationException("邮箱已被注册", 400);
        }
    }

    @Override
    public PageResponse<UserResponse> getUserPage(UserListRequest request) {
        // 分页
        Page<User> userPage = Page.of(request.getPage(), request.getSize());

        // 查询
        QueryWrapper<User> qw = null;
        // 如果用户有输入搜索关键词，拼接模糊查询的sql语句
        if(StringUtils.hasText(request.getKeyword())){
            qw = new QueryWrapper<>();
            qw.or().like("id", request.getKeyword());
            qw.or().like("email", request.getKeyword());
            qw.or().like("name", request.getKeyword());
            qw.or().like("mobile", request.getKeyword());
        }

        Page<User> userPage1 = this.page(userPage, qw);

        List<User> list = userPage1.getRecords();
        List<UserResponse> userResponses = BeanUtil.copyToList(list, UserResponse.class);

        PageResponse<UserResponse> response = new PageResponse<>();
        response.setTotal(userPage1.getTotal());
        response.setList(userResponses);

        return response;
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = this.getOptById(id).orElseThrow();

        UserResponse userResponse = BeanUtil.copyProperties(user, UserResponse.class);

        return userResponse;
    }

    @Override
    public Boolean addUser(UserAddRequest request) {
        // 验证这个邮箱是否被添加过
        this.ensureEmailNotRegistered(request.getEmail());

        User user = new User();
        // 设置用户提交过来的数据
        user.setEmail(request.getEmail());
        // todo 密码一定要加密
        user.setPassword(request.getPassword());
        user.setName(request.getName());
        user.setMobile(request.getMobile());

        // 不是用户提交的但是必须的其他数据
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setCreatedBy(UserContext.get());

        boolean b = this.save(user);

        return b;
    }

    @Override
    public Boolean deleteUser(Long id) {
        return this.removeById(id);
    }

    @Override
    public Boolean deleteBatch(List<Long> ids) {
        if(ids == null || ids.isEmpty()){
            return false;
        }
        return this.removeByIds(ids);
    }

    @Override
    public Boolean updateUser(UserUpdateRequest request) {
        User user = this.getOptById(request.getId()).orElseThrow();

        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setMobile(request.getMobile());

        if(StringUtils.hasText(request.getPassword())){
            user.setPassword(request.getPassword());
        }

        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(UserContext.get());


        boolean b = this.updateById(user);

        return b;
    }
}
