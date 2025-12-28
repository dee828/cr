package com.example.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.business.entity.DailyTrainStation;
import com.example.business.request.DailyTrainStationListRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.DailyTrainStationResponse;
import com.example.business.request.DailyTrainStationRequest;

import java.util.List;

public interface DailyTrainStationService extends IService<DailyTrainStation> {
    /**
     * 保存或更新每日火车车站
     * 会强制关联当前登录用户
     */
    boolean saveOrUpdate(DailyTrainStationRequest request);

    /**
     * 删除每日火车车站
     * 包含所有权检查
     */
    boolean deleteDailyTrainStation(Long id);

    /**
     * 分页查询当前用户的每日火车车站
     */
    PageResponse<DailyTrainStationResponse> getDailyTrainStationPage(DailyTrainStationListRequest request);

    /**
     * 【管理员】分页查询全量每日火车车站
     */
    PageResponse<DailyTrainStationResponse> getAdminDailyTrainStationPage(DailyTrainStationListRequest request);

    /**
     * 【管理员】添加每日火车车站
     * 如果该实体表有关联的 userId 字段，管理员任意可以指定该字段的值
     */
    boolean adminAdd(DailyTrainStationRequest request);

    /**
     * 【管理员】更新每日火车车站信息 (跨用户强制操作)
     */
    boolean adminUpdate(DailyTrainStationRequest request);

    /**
     * 【管理员】逻辑删除每日火车车站 (无需所有权检查)
     */
    boolean adminDelete(Long id);

    /**
     * 【管理员】批量逻辑删除每日火车车站 (无需所有权检查)
     */
    boolean adminDeleteBatch(List<Long> ids);
}