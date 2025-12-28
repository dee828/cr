package com.example.business.controller.admin;

import com.example.common.response.R;
import com.example.business.request.DailyTrainStationListRequest;
import com.example.business.request.DailyTrainStationRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.DailyTrainStationResponse;
import com.example.business.service.DailyTrainStationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/daily-train-station")
public class DailyTrainStationAdminController {
    @Autowired
    private DailyTrainStationService dailyTrainStationService;

    @Operation(summary = "每日火车车站列表")
    @GetMapping("/list")
    public R<PageResponse<DailyTrainStationResponse>> list(@Valid DailyTrainStationListRequest request) {
        return R.ok(dailyTrainStationService.getAdminDailyTrainStationPage(request));
    }

    @Operation(summary = "新增/修改每日火车车站")
    @PostMapping("/save")
    public R<Boolean> save(@RequestBody @Valid DailyTrainStationRequest request) {
        if (request.getId() == null) {
            return R.ok(dailyTrainStationService.adminAdd(request));
        } else {
            return R.ok(dailyTrainStationService.adminUpdate(request));
        }
    }

    @Operation(summary = "删除每日火车车站")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(dailyTrainStationService.adminDelete(id));
    }

    @Operation(summary = "批量删除每日火车车站")
    @DeleteMapping
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return R.ok(dailyTrainStationService.adminDeleteBatch(ids));
    }
}
