package com.example.business.controller.admin;

import com.example.common.response.R;
import com.example.business.request.TrainStationListRequest;
import com.example.business.request.TrainStationRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.TrainStationResponse;
import com.example.business.service.TrainStationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/train-station")
public class TrainStationAdminController {
    @Autowired
    private TrainStationService trainStationService;

    @Operation(summary = "火车车站列表")
    @GetMapping("/list")
    public R<PageResponse<TrainStationResponse>> list(@Valid TrainStationListRequest request) {
        return R.ok(trainStationService.getAdminTrainStationPage(request));
    }

    @Operation(summary = "新增/修改火车车站")
    @PostMapping("/save")
    public R<Boolean> save(@RequestBody @Valid TrainStationRequest request) {
        if (request.getId() == null) {
            return R.ok(trainStationService.adminAdd(request));
        } else {
            return R.ok(trainStationService.adminUpdate(request));
        }
    }

    @Operation(summary = "删除火车车站")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(trainStationService.adminDelete(id));
    }

    @Operation(summary = "批量删除火车车站")
    @DeleteMapping
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return R.ok(trainStationService.adminDeleteBatch(ids));
    }
}
