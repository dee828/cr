package com.example.user.controller.admin;

import com.example.common.response.R;
import com.example.user.request.UserAddRequest;
import com.example.user.request.UserListRequest;
import com.example.user.request.UserUpdateRequest;
import com.example.common.response.PageResponse;
import com.example.user.response.UserResponse;
import com.example.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/user")
public class UserAdminController {
    // 找到会做事的人
    @Autowired
    private UserService userService;

    // 让他帮我做具体的事
    @GetMapping("{id}")
    public R<UserResponse> show(@PathVariable Long id){
        return R.ok(userService.getUserById(id));
    }

    @GetMapping("list")
    public R<PageResponse<UserResponse>> list(@Valid UserListRequest request){
        return R.ok(userService.getUserPage(request));
    }

    @PostMapping("add")
    public R<Boolean> add(@RequestBody @Valid UserAddRequest request){
        boolean b = userService.addUser(request);
        return R.ok(b);
    }

    @DeleteMapping("{id}")
    public R<Boolean> delete(@PathVariable Long id){
        return R.ok(userService.deleteUser(id));
    }

    @DeleteMapping
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids){
        return R.ok(userService.deleteBatch(ids));
    }

    @PutMapping
    public R<Boolean> update(@RequestBody @Valid UserUpdateRequest request){
        return R.ok(userService.updateUser(request));
    }
}
