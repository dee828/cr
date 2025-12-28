package com.example.business.controller;

import com.example.business.request.ConfirmOrderRequest;
import com.example.business.service.ConfirmOrderService;
import com.example.common.response.R;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("confirm-order")
public class ConfirmOrderController {
    @Autowired
    ConfirmOrderService confirmOrderService;

    @PostMapping("confirm")
    public R<Object> Confirm(@Valid @RequestBody ConfirmOrderRequest request) {
        confirmOrderService.confirm(request);
        return R.ok();
    }
}
