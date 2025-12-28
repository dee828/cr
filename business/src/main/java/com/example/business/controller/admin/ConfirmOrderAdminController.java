package com.example.business.controller.admin;

import com.example.common.response.R;
import com.example.business.request.ConfirmOrderListRequest;
import com.example.business.request.ConfirmOrderRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.ConfirmOrderResponse;
import com.example.business.service.ConfirmOrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/confirm-order")
public class ConfirmOrderAdminController {
    @Autowired
    private ConfirmOrderService confirmOrderService;

    @Operation(summary = "确认订单列表")
    @GetMapping("/list")
    public R<PageResponse<ConfirmOrderResponse>> list(@Valid ConfirmOrderListRequest request) {
        return R.ok(confirmOrderService.getAdminConfirmOrderPage(request));
    }

    @Operation(summary = "新增/修改确认订单")
    @PostMapping("/save")
    public R<Boolean> save(@RequestBody @Valid ConfirmOrderRequest request) {
        if (request.getId() == null) {
            return R.ok(confirmOrderService.adminAdd(request));
        } else {
            return R.ok(confirmOrderService.adminUpdate(request));
        }
    }

    @Operation(summary = "删除确认订单")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(confirmOrderService.adminDelete(id));
    }

    @Operation(summary = "批量删除确认订单")
    @DeleteMapping
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return R.ok(confirmOrderService.adminDeleteBatch(ids));
    }
}
