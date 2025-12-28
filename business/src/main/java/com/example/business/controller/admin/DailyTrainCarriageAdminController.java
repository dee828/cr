package com.example.business.controller.admin;

import com.example.common.response.R;
import com.example.business.request.DailyTrainCarriageListRequest;
import com.example.business.request.DailyTrainCarriageRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.DailyTrainCarriageResponse;
import com.example.business.service.DailyTrainCarriageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/daily-train-carriage")
public class DailyTrainCarriageAdminController {
    @Autowired
    private DailyTrainCarriageService dailyTrainCarriageService;

    @Operation(summary = "每日火车车厢列表")
    @GetMapping("/list")
    public R<PageResponse<DailyTrainCarriageResponse>> list(@Valid DailyTrainCarriageListRequest request) {
        return R.ok(dailyTrainCarriageService.getAdminDailyTrainCarriagePage(request));
    }

    @Operation(summary = "新增/修改每日火车车厢")
    @PostMapping("/save")
    public R<Boolean> save(@RequestBody @Valid DailyTrainCarriageRequest request) {
        if (request.getId() == null) {
            return R.ok(dailyTrainCarriageService.adminAdd(request));
        } else {
            return R.ok(dailyTrainCarriageService.adminUpdate(request));
        }
    }

    @Operation(summary = "删除每日火车车厢")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(dailyTrainCarriageService.adminDelete(id));
    }

    @Operation(summary = "批量删除每日火车车厢")
    @DeleteMapping
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return R.ok(dailyTrainCarriageService.adminDeleteBatch(ids));
    }
}
