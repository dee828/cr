package com.example.business.controller;

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
@RequestMapping("daily-train-ticket")
public class DailyTrainTicketController {
    @Autowired
    private DailyTrainTicketService dailyTrainTicketService;

    @Operation(summary = "新增/修改余票信息")
    @PostMapping("/save")
    public R<Boolean> save(@RequestBody @Valid DailyTrainTicketRequest request) {
        return R.ok(dailyTrainTicketService.saveOrUpdate(request));
    }

    @Operation(summary = "我的余票信息")
    @GetMapping("/list")
    public R<PageResponse<DailyTrainTicketResponse>> list(@Valid DailyTrainTicketListRequest request) {
        return R.ok(dailyTrainTicketService.getDailyTrainTicketPage(request));
    }

    @Operation(summary = "删除余票信息")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(dailyTrainTicketService.deleteDailyTrainTicket(id));
    }
}
