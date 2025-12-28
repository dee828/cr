package com.example.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.business.entity.TrainStation;
import com.example.business.request.TrainStationListRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.TrainStationResponse;
import com.example.business.request.TrainStationRequest;

import java.util.List;

public interface TrainStationService extends IService<TrainStation> {
    /**
     * 保存或更新火车车站
     * 会强制关联当前登录用户
     */
    boolean saveOrUpdate(TrainStationRequest request);

    /**
     * 删除火车车站
     * 包含所有权检查
     */
    boolean deleteTrainStation(Long id);

    /**
     * 分页查询当前用户的火车车站
     */
    PageResponse<TrainStationResponse> getTrainStationPage(TrainStationListRequest request);

    /**
     * 【管理员】分页查询全量火车车站
     */
    PageResponse<TrainStationResponse> getAdminTrainStationPage(TrainStationListRequest request);

    /**
     * 【管理员】添加火车车站
     * 如果该实体表有关联的 userId 字段，管理员任意可以指定该字段的值
     */
    boolean adminAdd(TrainStationRequest request);

    /**
     * 【管理员】更新火车车站信息 (跨用户强制操作)
     */
    boolean adminUpdate(TrainStationRequest request);

    /**
     * 【管理员】逻辑删除火车车站 (无需所有权检查)
     */
    boolean adminDelete(Long id);

    /**
     * 【管理员】批量逻辑删除火车车站 (无需所有权检查)
     */
    boolean adminDeleteBatch(List<Long> ids);
}