package com.example.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.user.entity.Passenger;
import com.example.user.request.PassengerListRequest;
import com.example.common.response.PageResponse;
import com.example.user.response.PassengerResponse;
import com.example.user.request.PassengerRequest;

import java.util.List;

public interface PassengerService extends IService<Passenger> {
    /**
     * 保存或更新乘车人/乘客
     * 会强制关联当前登录用户
     */
    boolean saveOrUpdate(PassengerRequest request);

    /**
     * 删除乘车人/乘客
     * 包含所有权检查
     */
    boolean deletePassenger(Long id);

    /**
     * 分页查询当前用户的乘车人/乘客
     */
    PageResponse<PassengerResponse> getPassengerPage(PassengerListRequest request);

    /**
     * 【管理员】分页查询全量乘车人/乘客
     */
    PageResponse<PassengerResponse> getAdminPassengerPage(PassengerListRequest request);

    /**
     * 【管理员】添加乘车人/乘客
     * 如果该实体表有关联的 userId 字段，管理员任意可以指定该字段的值
     */
    boolean adminAdd(PassengerRequest request);

    /**
     * 【管理员】更新乘车人/乘客信息 (跨用户强制操作)
     */
    boolean adminUpdate(PassengerRequest request);

    /**
     * 【管理员】逻辑删除乘车人/乘客 (无需所有权检查)
     */
    boolean adminDelete(Long id);

    /**
     * 【管理员】批量逻辑删除乘车人/乘客 (无需所有权检查)
     */
    boolean adminDeleteBatch(List<Long> ids);
}