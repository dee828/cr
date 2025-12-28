package com.example.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.business.entity.TrainSeat;
import com.example.business.request.TrainSeatListRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.TrainSeatResponse;
import com.example.business.request.TrainSeatRequest;

import java.util.List;

public interface TrainSeatService extends IService<TrainSeat> {
    /**
     * 保存或更新车厢座位
     * 会强制关联当前登录用户
     */
    boolean saveOrUpdate(TrainSeatRequest request);

    /**
     * 删除车厢座位
     * 包含所有权检查
     */
    boolean deleteTrainSeat(Long id);

    /**
     * 分页查询当前用户的车厢座位
     */
    PageResponse<TrainSeatResponse> getTrainSeatPage(TrainSeatListRequest request);

    /**
     * 【管理员】分页查询全量车厢座位
     */
    PageResponse<TrainSeatResponse> getAdminTrainSeatPage(TrainSeatListRequest request);

    /**
     * 【管理员】添加车厢座位
     * 如果该实体表有关联的 userId 字段，管理员任意可以指定该字段的值
     */
    boolean adminAdd(TrainSeatRequest request);

    /**
     * 【管理员】更新车厢座位信息 (跨用户强制操作)
     */
    boolean adminUpdate(TrainSeatRequest request);

    /**
     * 【管理员】逻辑删除车厢座位 (无需所有权检查)
     */
    boolean adminDelete(Long id);

    /**
     * 【管理员】批量逻辑删除车厢座位 (无需所有权检查)
     */
    boolean adminDeleteBatch(List<Long> ids);
}