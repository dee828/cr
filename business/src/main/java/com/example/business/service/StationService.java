package com.example.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.business.entity.Station;
import com.example.business.request.StationListRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.StationResponse;
import com.example.business.request.StationRequest;

import java.util.List;

public interface StationService extends IService<Station> {
    /**
     * 保存或更新车站
     * 会强制关联当前登录用户
     */
    boolean saveOrUpdate(StationRequest request);

    /**
     * 删除车站
     * 包含所有权检查
     */
    boolean deleteStation(Long id);

    /**
     * 分页查询当前用户的车站
     */
    PageResponse<StationResponse> getStationPage(StationListRequest request);

    /**
     * 【管理员】分页查询全量车站
     */
    PageResponse<StationResponse> getAdminStationPage(StationListRequest request);

    /**
     * 【管理员】添加车站
     * 如果该实体表有关联的 userId 字段，管理员任意可以指定该字段的值
     */
    boolean adminAdd(StationRequest request);

    /**
     * 【管理员】更新车站信息 (跨用户强制操作)
     */
    boolean adminUpdate(StationRequest request);

    /**
     * 【管理员】逻辑删除车站 (无需所有权检查)
     */
    boolean adminDelete(Long id);

    /**
     * 【管理员】批量逻辑删除车站 (无需所有权检查)
     */
    boolean adminDeleteBatch(List<Long> ids);
}