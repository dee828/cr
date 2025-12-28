package com.example.business.controller.admin;

import com.example.common.response.R;
import com.example.business.request.TrainSeatListRequest;
import com.example.business.request.TrainSeatRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.TrainSeatResponse;
import com.example.business.service.TrainSeatService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/train-seat")
public class TrainSeatAdminController {
    @Autowired
    private TrainSeatService trainSeatService;

    @Operation(summary = "车厢座位列表")
    @GetMapping("/list")
    public R<PageResponse<TrainSeatResponse>> list(@Valid TrainSeatListRequest request) {
        return R.ok(trainSeatService.getAdminTrainSeatPage(request));
    }

    @Operation(summary = "新增/修改车厢座位")
    @PostMapping("/save")
    public R<Boolean> save(@RequestBody @Valid TrainSeatRequest request) {
        if (request.getId() == null) {
            return R.ok(trainSeatService.adminAdd(request));
        } else {
            return R.ok(trainSeatService.adminUpdate(request));
        }
    }

    @Operation(summary = "删除车厢座位")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(trainSeatService.adminDelete(id));
    }

    @Operation(summary = "批量删除车厢座位")
    @DeleteMapping
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return R.ok(trainSeatService.adminDeleteBatch(ids));
    }
}
