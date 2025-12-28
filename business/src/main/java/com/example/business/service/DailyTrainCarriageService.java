package com.example.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.business.entity.DailyTrainCarriage;
import com.example.business.request.DailyTrainCarriageListRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.DailyTrainCarriageResponse;
import com.example.business.request.DailyTrainCarriageRequest;

import java.util.List;

public interface DailyTrainCarriageService extends IService<DailyTrainCarriage> {
    /**
     * 保存或更新每日火车车厢
     * 会强制关联当前登录用户
     */
    boolean saveOrUpdate(DailyTrainCarriageRequest request);

    /**
     * 删除每日火车车厢
     * 包含所有权检查
     */
    boolean deleteDailyTrainCarriage(Long id);

    /**
     * 分页查询当前用户的每日火车车厢
     */
    PageResponse<DailyTrainCarriageResponse> getDailyTrainCarriagePage(DailyTrainCarriageListRequest request);

    /**
     * 【管理员】分页查询全量每日火车车厢
     */
    PageResponse<DailyTrainCarriageResponse> getAdminDailyTrainCarriagePage(DailyTrainCarriageListRequest request);

    /**
     * 【管理员】添加每日火车车厢
     * 如果该实体表有关联的 userId 字段，管理员任意可以指定该字段的值
     */
    boolean adminAdd(DailyTrainCarriageRequest request);

    /**
     * 【管理员】更新每日火车车厢信息 (跨用户强制操作)
     */
    boolean adminUpdate(DailyTrainCarriageRequest request);

    /**
     * 【管理员】逻辑删除每日火车车厢 (无需所有权检查)
     */
    boolean adminDelete(Long id);

    /**
     * 【管理员】批量逻辑删除每日火车车厢 (无需所有权检查)
     */
    boolean adminDeleteBatch(List<Long> ids);
}