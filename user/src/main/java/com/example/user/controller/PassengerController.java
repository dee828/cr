package com.example.user.controller;

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
@RequestMapping("passenger")
public class PassengerController {
    @Autowired
    private PassengerService passengerService;

    @Operation(summary = "新增/修改乘车人/乘客")
    @PostMapping("/save")
    public R<Boolean> save(@RequestBody @Valid PassengerRequest request) {
        return R.ok(passengerService.saveOrUpdate(request));
    }

    @Operation(summary = "我的乘车人/乘客")
    @GetMapping("/list")
    public R<PageResponse<PassengerResponse>> list(@Valid PassengerListRequest request) {
        return R.ok(passengerService.getPassengerPage(request));
    }

    @Operation(summary = "删除乘车人/乘客")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(passengerService.deletePassenger(id));
    }
}
