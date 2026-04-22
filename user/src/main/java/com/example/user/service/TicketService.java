package com.example.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.user.entity.Ticket;
import com.example.user.request.TicketListRequest;
import com.example.common.response.PageResponse;
import com.example.user.response.TicketResponse;
import com.example.user.request.TicketRequest;

import java.util.List;

public interface TicketService extends IService<Ticket> {
    /**
     * 保存或更新车票记录
     * 会强制关联当前登录用户
     */
    boolean saveOrUpdate(TicketRequest request);

    /**
     * 删除车票记录
     * 包含所有权检查
     */
    boolean deleteTicket(Long id);

    /**
     * 分页查询当前用户的车票记录
     */
    PageResponse<TicketResponse> getTicketPage(TicketListRequest request);

    /**
     * 【管理员】分页查询全量车票记录
     */
    PageResponse<TicketResponse> getAdminTicketPage(TicketListRequest request);

    /**
     * 【管理员】添加车票记录
     * 如果该实体表有关联的 userId 字段，管理员任意可以指定该字段的值
     */
    boolean adminAdd(TicketRequest request);

    /**
     * 【管理员】更新车票记录信息 (跨用户强制操作)
     */
    boolean adminUpdate(TicketRequest request);

    /**
     * 【管理员】逻辑删除车票记录 (无需所有权检查)
     */
    boolean adminDelete(Long id);

    /**
     * 【管理员】批量逻辑删除车票记录 (无需所有权检查)
     */
    boolean adminDeleteBatch(List<Long> ids);
}