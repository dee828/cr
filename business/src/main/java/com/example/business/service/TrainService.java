package com.example.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.business.entity.Train;
import com.example.business.request.TrainListRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.TrainResponse;
import com.example.business.request.TrainRequest;

import java.util.List;

public interface TrainService extends IService<Train> {
    /**
     * 保存或更新车次
     * 会强制关联当前登录用户
     */
    boolean saveOrUpdate(TrainRequest request);

    /**
     * 删除车次
     * 包含所有权检查
     */
    boolean deleteTrain(Long id);

    /**
     * 分页查询当前用户的车次
     */
    PageResponse<TrainResponse> getTrainPage(TrainListRequest request);

    /**
     * 【管理员】分页查询全量车次
     */
    PageResponse<TrainResponse> getAdminTrainPage(TrainListRequest request);

    /**
     * 【管理员】添加车次
     * 如果该实体表有关联的 userId 字段，管理员任意可以指定该字段的值
     */
    boolean adminAdd(TrainRequest request);

    /**
     * 【管理员】更新车次信息 (跨用户强制操作)
     */
    boolean adminUpdate(TrainRequest request);

    /**
     * 【管理员】逻辑删除车次 (无需所有权检查)
     */
    boolean adminDelete(Long id);

    /**
     * 【管理员】批量逻辑删除车次 (无需所有权检查)
     */
    boolean adminDeleteBatch(List<Long> ids);

    /**
     * 根据车次编号自动生成座位
     * @param trainCode 车次编号
     */
    public void genSeat(String trainCode);
}