package com.example.business.controller.admin;

import com.example.common.response.R;
import com.example.business.request.DailyTrainTicketListRequest;
import com.example.business.request.DailyTrainTicketRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.DailyTrainTicketResponse;
import com.example.business.service.DailyTrainTicketService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/daily-train-ticket")
public class DailyTrainTicketAdminController {
    @Autowired
    private DailyTrainTicketService dailyTrainTicketService;

    @Operation(summary = "余票信息列表")
    @GetMapping("/list")
    public R<PageResponse<DailyTrainTicketResponse>> list(@Valid DailyTrainTicketListRequest request) {
        return R.ok(dailyTrainTicketService.getAdminDailyTrainTicketPage(request));
    }

    @Operation(summary = "新增/修改余票信息")
    @PostMapping("/save")
    public R<Boolean> save(@RequestBody @Valid DailyTrainTicketRequest request) {
        if (request.getId() == null) {
            return R.ok(dailyTrainTicketService.adminAdd(request));
        } else {
            return R.ok(dailyTrainTicketService.adminUpdate(request));
        }
    }

    @Operation(summary = "删除余票信息")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(dailyTrainTicketService.adminDelete(id));
    }

    @Operation(summary = "批量删除余票信息")
    @DeleteMapping
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return R.ok(dailyTrainTicketService.adminDeleteBatch(ids));
    }
}
