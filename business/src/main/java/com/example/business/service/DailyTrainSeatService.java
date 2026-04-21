package com.example.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.business.entity.DailyTrainSeat;
import com.example.business.request.DailyTrainSeatListRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.DailyTrainSeatResponse;
import com.example.business.request.DailyTrainSeatRequest;

import java.time.LocalDate;
import java.util.List;

public interface DailyTrainSeatService extends IService<DailyTrainSeat> {
    /**
     * 保存或更新每日车厢座位
     * 会强制关联当前登录用户
     */
    boolean saveOrUpdate(DailyTrainSeatRequest request);

    /**
     * 删除每日车厢座位
     * 包含所有权检查
     */
    boolean deleteDailyTrainSeat(Long id);

    /**
     * 分页查询当前用户的每日车厢座位
     */
    PageResponse<DailyTrainSeatResponse> getDailyTrainSeatPage(DailyTrainSeatListRequest request);

    /**
     * 【管理员】分页查询全量每日车厢座位
     */
    PageResponse<DailyTrainSeatResponse> getAdminDailyTrainSeatPage(DailyTrainSeatListRequest request);

    /**
     * 【管理员】添加每日车厢座位
     * 如果该实体表有关联的 userId 字段，管理员任意可以指定该字段的值
     */
    boolean adminAdd(DailyTrainSeatRequest request);

    /**
     * 【管理员】更新每日车厢座位信息 (跨用户强制操作)
     */
    boolean adminUpdate(DailyTrainSeatRequest request);

    /**
     * 【管理员】逻辑删除每日车厢座位 (无需所有权检查)
     */
    boolean adminDelete(Long id);

    /**
     * 【管理员】批量逻辑删除每日车厢座位 (无需所有权检查)
     */
    boolean adminDeleteBatch(List<Long> ids);

    int countSeat(LocalDate date, String code, String seatType);

    List<DailyTrainSeat> selectByCarriage(LocalDate date, String trainCode, Integer trainCarriageIndex);
}