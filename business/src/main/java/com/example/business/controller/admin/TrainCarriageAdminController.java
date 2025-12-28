package com.example.business.controller.admin;

import com.example.common.response.R;
import com.example.business.request.TrainCarriageListRequest;
import com.example.business.request.TrainCarriageRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.TrainCarriageResponse;
import com.example.business.service.TrainCarriageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/train-carriage")
public class TrainCarriageAdminController {
    @Autowired
    private TrainCarriageService trainCarriageService;

    @Operation(summary = "火车车厢列表")
    @GetMapping("/list")
    public R<PageResponse<TrainCarriageResponse>> list(@Valid TrainCarriageListRequest request) {
        return R.ok(trainCarriageService.getAdminTrainCarriagePage(request));
    }

    @Operation(summary = "新增/修改火车车厢")
    @PostMapping("/save")
    public R<Boolean> save(@RequestBody @Valid TrainCarriageRequest request) {
        if (request.getId() == null) {
            return R.ok(trainCarriageService.adminAdd(request));
        } else {
            return R.ok(trainCarriageService.adminUpdate(request));
        }
    }

    @Operation(summary = "删除火车车厢")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(trainCarriageService.adminDelete(id));
    }

    @Operation(summary = "批量删除火车车厢")
    @DeleteMapping
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return R.ok(trainCarriageService.adminDeleteBatch(ids));
    }
}
