package com.example.user.controller;

import com.example.common.response.R;
import com.example.user.request.TicketListRequest;
import com.example.user.request.TicketRequest;
import com.example.common.response.PageResponse;
import com.example.user.response.TicketResponse;
import com.example.user.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("ticket")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @Operation(summary = "新增/修改车票记录")
    @PostMapping("/save")
    public R<Boolean> save(@RequestBody @Valid TicketRequest request) {
        return R.ok(ticketService.saveOrUpdate(request));
    }

    @Operation(summary = "我的车票记录")
    @GetMapping("/list")
    public R<PageResponse<TicketResponse>> list(@Valid TicketListRequest request) {
        return R.ok(ticketService.getTicketPage(request));
    }

    @Operation(summary = "删除车票记录")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(ticketService.deleteTicket(id));
    }
}
