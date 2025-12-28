package com.example.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.business.entity.DailyTrain;
import com.example.business.entity.Train;
import com.example.business.request.DailyTrainListRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.DailyTrainResponse;
import com.example.business.request.DailyTrainRequest;

import java.time.LocalDate;
import java.util.List;

public interface DailyTrainService extends IService<DailyTrain> {
    /**
     * 保存或更新每日车次
     * 会强制关联当前登录用户
     */
    boolean saveOrUpdate(DailyTrainRequest request);

    /**
     * 删除每日车次
     * 包含所有权检查
     */
    boolean deleteDailyTrain(Long id);

    /**
     * 分页查询当前用户的每日车次
     */
    PageResponse<DailyTrainResponse> getDailyTrainPage(DailyTrainListRequest request);

    /**
     * 【管理员】分页查询全量每日车次
     */
    PageResponse<DailyTrainResponse> getAdminDailyTrainPage(DailyTrainListRequest request);

    /**
     * 【管理员】添加每日车次
     * 如果该实体表有关联的 userId 字段，管理员任意可以指定该字段的值
     */
    boolean adminAdd(DailyTrainRequest request);

    /**
     * 【管理员】更新每日车次信息 (跨用户强制操作)
     */
    boolean adminUpdate(DailyTrainRequest request);

    /**
     * 【管理员】逻辑删除每日车次 (无需所有权检查)
     */
    boolean adminDelete(Long id);

    /**
     * 【管理员】批量逻辑删除每日车次 (无需所有权检查)
     */
    boolean adminDeleteBatch(List<Long> ids);

    /**
     * 生成每日数据
     * 包含：每日车次、每日火车车站、每日火车车厢、每日车厢座位
     * @param date 指定日期
     */
    void genDaily(LocalDate date);
}