package com.example.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.business.entity.ConfirmOrder;
import com.example.business.request.ConfirmOrderListRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.ConfirmOrderResponse;
import com.example.business.request.ConfirmOrderRequest;
import jakarta.validation.Valid;

import java.util.List;

public interface ConfirmOrderService extends IService<ConfirmOrder> {
    /**
     * 保存或更新确认订单
     * 会强制关联当前登录用户
     */
    boolean saveOrUpdate(ConfirmOrderRequest request);

    /**
     * 删除确认订单
     * 包含所有权检查
     */
    boolean deleteConfirmOrder(Long id);

    /**
     * 分页查询当前用户的确认订单
     */
    PageResponse<ConfirmOrderResponse> getConfirmOrderPage(ConfirmOrderListRequest request);

    /**
     * 【管理员】分页查询全量确认订单
     */
    PageResponse<ConfirmOrderResponse> getAdminConfirmOrderPage(ConfirmOrderListRequest request);

    /**
     * 【管理员】添加确认订单
     * 如果该实体表有关联的 userId 字段，管理员任意可以指定该字段的值
     */
    boolean adminAdd(ConfirmOrderRequest request);

    /**
     * 【管理员】更新确认订单信息 (跨用户强制操作)
     */
    boolean adminUpdate(ConfirmOrderRequest request);

    /**
     * 【管理员】逻辑删除确认订单 (无需所有权检查)
     */
    boolean adminDelete(Long id);

    /**
     * 【管理员】批量逻辑删除确认订单 (无需所有权检查)
     */
    boolean adminDeleteBatch(List<Long> ids);

    void confirm(@Valid ConfirmOrderRequest request);
}