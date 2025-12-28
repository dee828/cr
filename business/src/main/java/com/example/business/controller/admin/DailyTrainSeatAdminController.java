package com.example.business.controller.admin;

import com.example.common.response.R;
import com.example.business.request.DailyTrainSeatListRequest;
import com.example.business.request.DailyTrainSeatRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.DailyTrainSeatResponse;
import com.example.business.service.DailyTrainSeatService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/daily-train-seat")
public class DailyTrainSeatAdminController {
    @Autowired
    private DailyTrainSeatService dailyTrainSeatService;

    @Operation(summary = "每日车厢座位列表")
    @GetMapping("/list")
    public R<PageResponse<DailyTrainSeatResponse>> list(@Valid DailyTrainSeatListRequest request) {
        return R.ok(dailyTrainSeatService.getAdminDailyTrainSeatPage(request));
    }

    @Operation(summary = "新增/修改每日车厢座位")
    @PostMapping("/save")
    public R<Boolean> save(@RequestBody @Valid DailyTrainSeatRequest request) {
        if (request.getId() == null) {
            return R.ok(dailyTrainSeatService.adminAdd(request));
        } else {
            return R.ok(dailyTrainSeatService.adminUpdate(request));
        }
    }

    @Operation(summary = "删除每日车厢座位")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(dailyTrainSeatService.adminDelete(id));
    }

    @Operation(summary = "批量删除每日车厢座位")
    @DeleteMapping
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return R.ok(dailyTrainSeatService.adminDeleteBatch(ids));
    }
}
