package com.example.user.controller.admin;

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
@RequestMapping("admin/ticket")
public class TicketAdminController {
    @Autowired
    private TicketService ticketService;

    @Operation(summary = "车票记录列表")
    @GetMapping("/list")
    public R<PageResponse<TicketResponse>> list(@Valid TicketListRequest request) {
        return R.ok(ticketService.getAdminTicketPage(request));
    }

    @Operation(summary = "新增/修改车票记录")
    @PostMapping("/save")
    public R<Boolean> save(@RequestBody @Valid TicketRequest request) {
        if (request.getId() == null) {
            return R.ok(ticketService.adminAdd(request));
        } else {
            return R.ok(ticketService.adminUpdate(request));
        }
    }

    @Operation(summary = "删除车票记录")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(ticketService.adminDelete(id));
    }

    @Operation(summary = "批量删除车票记录")
    @DeleteMapping
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return R.ok(ticketService.adminDeleteBatch(ids));
    }
}
