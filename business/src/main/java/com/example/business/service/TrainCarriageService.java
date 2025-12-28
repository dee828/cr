package com.example.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.business.entity.TrainCarriage;
import com.example.business.request.TrainCarriageListRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.TrainCarriageResponse;
import com.example.business.request.TrainCarriageRequest;

import java.util.List;

public interface TrainCarriageService extends IService<TrainCarriage> {
    /**
     * 保存或更新火车车厢
     * 会强制关联当前登录用户
     */
    boolean saveOrUpdate(TrainCarriageRequest request);

    /**
     * 删除火车车厢
     * 包含所有权检查
     */
    boolean deleteTrainCarriage(Long id);

    /**
     * 分页查询当前用户的火车车厢
     */
    PageResponse<TrainCarriageResponse> getTrainCarriagePage(TrainCarriageListRequest request);

    /**
     * 【管理员】分页查询全量火车车厢
     */
    PageResponse<TrainCarriageResponse> getAdminTrainCarriagePage(TrainCarriageListRequest request);

    /**
     * 【管理员】添加火车车厢
     * 如果该实体表有关联的 userId 字段，管理员任意可以指定该字段的值
     */
    boolean adminAdd(TrainCarriageRequest request);

    /**
     * 【管理员】更新火车车厢信息 (跨用户强制操作)
     */
    boolean adminUpdate(TrainCarriageRequest request);

    /**
     * 【管理员】逻辑删除火车车厢 (无需所有权检查)
     */
    boolean adminDelete(Long id);

    /**
     * 【管理员】批量逻辑删除火车车厢 (无需所有权检查)
     */
    boolean adminDeleteBatch(List<Long> ids);
}