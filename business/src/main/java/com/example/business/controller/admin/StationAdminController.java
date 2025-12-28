package com.example.business.controller.admin;

import com.example.common.response.R;
import com.example.business.request.StationListRequest;
import com.example.business.request.StationRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.StationResponse;
import com.example.business.service.StationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/station")
public class StationAdminController {
    @Autowired
    private StationService stationService;

    @Operation(summary = "车站列表")
    @GetMapping("/list")
    public R<PageResponse<StationResponse>> list(@Valid StationListRequest request) {
        return R.ok(stationService.getAdminStationPage(request));
    }

    @Operation(summary = "新增/修改车站")
    @PostMapping("/save")
    public R<Boolean> save(@RequestBody @Valid StationRequest request) {
        if (request.getId() == null) {
            return R.ok(stationService.adminAdd(request));
        } else {
            return R.ok(stationService.adminUpdate(request));
        }
    }

    @Operation(summary = "删除车站")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(stationService.adminDelete(id));
    }

    @Operation(summary = "批量删除车站")
    @DeleteMapping
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return R.ok(stationService.adminDeleteBatch(ids));
    }
}
