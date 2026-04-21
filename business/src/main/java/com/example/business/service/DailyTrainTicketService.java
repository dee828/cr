package com.example.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.business.entity.DailyTrainTicket;
import com.example.business.request.DailyTrainTicketListRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.DailyTrainTicketResponse;
import com.example.business.request.DailyTrainTicketRequest;

import java.time.LocalDate;
import java.util.List;

public interface DailyTrainTicketService extends IService<DailyTrainTicket> {
    /**
     * 保存或更新余票信息
     * 会强制关联当前登录用户
     */
    boolean saveOrUpdate(DailyTrainTicketRequest request);

    /**
     * 删除余票信息
     * 包含所有权检查
     */
    boolean deleteDailyTrainTicket(Long id);

    /**
     * 分页查询当前用户的余票信息
     */
    PageResponse<DailyTrainTicketResponse> getDailyTrainTicketPage(DailyTrainTicketListRequest request);

    /**
     * 【管理员】分页查询全量余票信息
     */
    PageResponse<DailyTrainTicketResponse> getAdminDailyTrainTicketPage(DailyTrainTicketListRequest request);

    /**
     * 【管理员】添加余票信息
     * 如果该实体表有关联的 userId 字段，管理员任意可以指定该字段的值
     */
    boolean adminAdd(DailyTrainTicketRequest request);

    /**
     * 【管理员】更新余票信息信息 (跨用户强制操作)
     */
    boolean adminUpdate(DailyTrainTicketRequest request);

    /**
     * 【管理员】逻辑删除余票信息 (无需所有权检查)
     */
    boolean adminDelete(Long id);

    /**
     * 【管理员】批量逻辑删除余票信息 (无需所有权检查)
     */
    boolean adminDeleteBatch(List<Long> ids);

    DailyTrainTicket selectByUnique(LocalDate date, String trainCode, String start, String end);
}