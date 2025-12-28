package com.example.user.controller.admin;

import com.example.common.response.R;
import com.example.user.request.PassengerListRequest;
import com.example.user.request.PassengerRequest;
import com.example.common.response.PageResponse;
import com.example.user.response.PassengerResponse;
import com.example.user.service.PassengerService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/passenger")
public class PassengerAdminController {
    @Autowired
    private PassengerService passengerService;

    @Operation(summary = "乘车人/乘客列表")
    @GetMapping("/list")
    public R<PageResponse<PassengerResponse>> list(@Valid PassengerListRequest request) {
        return R.ok(passengerService.getAdminPassengerPage(request));
    }

    @Operation(summary = "新增/修改乘车人/乘客")
    @PostMapping("/save")
    public R<Boolean> save(@RequestBody @Valid PassengerRequest request) {
        if (request.getId() == null) {
            return R.ok(passengerService.adminAdd(request));
        } else {
            return R.ok(passengerService.adminUpdate(request));
        }
    }

    @Operation(summary = "删除乘车人/乘客")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(passengerService.adminDelete(id));
    }

    @Operation(summary = "批量删除乘车人/乘客")
    @DeleteMapping
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return R.ok(passengerService.adminDeleteBatch(ids));
    }
}
