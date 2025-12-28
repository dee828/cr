package com.example.business.controller.admin;

import com.example.common.response.R;
import com.example.business.request.TrainListRequest;
import com.example.business.request.TrainRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.TrainResponse;
import com.example.business.service.TrainService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/train")
public class TrainAdminController {
    @Autowired
    private TrainService trainService;

    @Operation(summary = "车次列表")
    @GetMapping("/list")
    public R<PageResponse<TrainResponse>> list(@Valid TrainListRequest request) {
        return R.ok(trainService.getAdminTrainPage(request));
    }

    @Operation(summary = "新增/修改车次")
    @PostMapping("/save")
    public R<Boolean> save(@RequestBody @Valid TrainRequest request) {
        if (request.getId() == null) {
            return R.ok(trainService.adminAdd(request));
        } else {
            return R.ok(trainService.adminUpdate(request));
        }
    }

    @Operation(summary = "删除车次")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(trainService.adminDelete(id));
    }

    @Operation(summary = "批量删除车次")
    @DeleteMapping
    public R<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return R.ok(trainService.adminDeleteBatch(ids));
    }

    @Operation(summary = "自动生成座位")
    @GetMapping("gen-seat/{trainCode}")
    public R<Object> genSeat(@PathVariable String trainCode){
        trainService.genSeat(trainCode);
        return R.ok();
    }
}
