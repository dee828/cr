package com.example.business.controller.admin;

import com.example.common.response.R;
import com.example.business.request.DailyTrainListRequest;
import com.example.business.request.DailyTrainRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.DailyTrainResponse;
import com.example.business.service.DailyTrainService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("admin/daily-train")
public class DailyTrainAdminController {
    @Autowired
    private DailyTrainService dailyTrainService;

    @Operation(summary = "每日车次列表")
    @GetMapping("/list")
    public R<PageResponse<DailyTrainResponse>> list(@Valid DailyTrainListRequest request) {
        return R.ok(dailyTrainService.getAdminDailyTrainPage(request));
    }

    @Operation(summary = "新增/修改每日车次")
    @PostMapping("/save")
    public R<Boolean> save(@RequestBody @Valid DailyTrainRequest request) {
        if (request.getId() == null) {
            return R.ok(dailyTrainService.adminAdd(request));
        } else {
            return R.ok(dailyTrainService.adminUpdate(request));
        }
    }

    @Operation(summary = "删除每日车次")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(dailyTrainService.adminDelete(id));
    }

    @Operation(summary = "批量删除每日车次")
    @DeleteMapping
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return R.ok(dailyTrainService.adminDeleteBatch(ids));
    }

    @GetMapping("gen-daily/{date}")
    public R<Object> genDaily(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date){
        dailyTrainService.genDaily(date);
        return R.ok();
    }
}
