package com.example.user.service.impl;

import com.example.common.core.UserContext;
import com.example.common.exception.CustomForbiddenException;
import cn.hutool.core.bean.BeanUtil;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.user.entity.Ticket;
import com.example.user.mapper.TicketMapper;
import com.example.user.request.TicketListRequest;
import com.example.user.request.TicketRequest;
import com.example.common.response.PageResponse;
import com.example.user.response.TicketResponse;
import com.example.user.service.TicketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.example.common.exception.CustomValidationException;
import java.util.NoSuchElementException;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class TicketServiceImpl extends ServiceImpl<TicketMapper, Ticket> implements TicketService {
    @Override
    @Transactional
    public boolean saveOrUpdate(TicketRequest request) {
        Long currentUserId = UserContext.get();
        Ticket ticket;

        if (request.getId() != null) {
            // 更新逻辑
            ticket = findTicket(request.getId());
            // 校验所有权
            if (!request.getUserId().equals(currentUserId)) {
                throw new CustomForbiddenException("无权操作此车票记录信息");
            }
            BeanUtil.copyProperties(request, ticket);
        } else {
            // 新增逻辑
            ticket = BeanUtil.copyProperties(request, Ticket.class);
            // 强制绑定当前用户
            ticket.setUserId(currentUserId);
        }

        return this.saveOrUpdate(ticket);
    }

    @Override
    @Transactional
    public boolean save(TicketRequest request) {
        Ticket ticket = BeanUtil.copyProperties(request, Ticket.class);
        // 强制绑定当前用户
        ticket.setUserId(UserContext.get());

        return this.save(ticket);
    }

    @Override
    @Transactional
    public boolean deleteTicket(Long id) {
        Ticket ticket = findTicket(id);

        // 安全校验：只能删除自己的车票记录
        if (!ticket.getUserId().equals(UserContext.get())) {
            throw new CustomForbiddenException("无权删除此车票记录信息");
        }

        return this.removeById(id);
    }

    @Override
    public PageResponse<TicketResponse> getTicketPage(TicketListRequest request) {
        LambdaQueryWrapper<Ticket> qw = buildQuery(request);
        // 强制限定搜索范围为当前用户 (水平越权防护)
        qw.eq(Ticket::getUserId, UserContext.get());

        return executePageQuery(request, qw);
    }

    @Override
    public PageResponse<TicketResponse> getAdminTicketPage(TicketListRequest request) {
        LambdaQueryWrapper<Ticket> qw = buildQuery(request);

        // 管理员可选：按用户ID筛选
        if (request.getUserId() != null) {
            qw.eq(Ticket::getUserId, request.getUserId());
        }

        return executePageQuery(request, qw);
    }

    @Override
    @Transactional
    public boolean adminAdd(TicketRequest request) {
        if (request.getUserId() == null) {
            throw new CustomValidationException("管理员添加车票记录必须指定 userId");
        }

        Ticket ticket = BeanUtil.copyProperties(request, Ticket.class);
        // 使用请求中的 userId（管理员可以指定任意用户）
        ticket.setUserId(request.getUserId());
        return this.save(ticket);
    }

    @Override
    @Transactional
    public boolean adminUpdate(TicketRequest request) {
        Ticket ticket = findTicket(request.getId());

        // 管理员可以修改所有字段，包括 userId（允许调整数据归属关系）
        BeanUtil.copyProperties(request, ticket);
        return this.updateById(ticket);
    }

    @Override
    @Transactional
    public boolean adminDelete(Long id) {
        return this.removeById(findTicket(id).getId());
    }

    @Override
    @Transactional
    public boolean adminDeleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        // 批量删除，MyBatis-Plus 的 removeByIds 会自动处理逻辑删除
        return this.removeByIds(ids);
    }

    // ==================== 逻辑抽取辅助方法 ====================

    /**
     * 构建基础查询对象 (处理通用过滤如关键词)
     */
    private LambdaQueryWrapper<Ticket> buildQuery(TicketListRequest request) {
        LambdaQueryWrapper<Ticket> qw = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getKeyword())) {
            String kw = request.getKeyword();
            qw.like(Ticket::getPassengerName, kw);
        }

        qw.orderByDesc(Ticket::getCreatedAt);
        return qw;
    }

    /**
     * 执行分页查询并转换结果
     */
    private PageResponse<TicketResponse> executePageQuery(TicketListRequest request,
                                                             LambdaQueryWrapper<Ticket> qw) {
        Page<Ticket> page = this.page(Page.of(request.getPage(), request.getSize()), qw);

        List<TicketResponse> list = BeanUtil.copyToList(page.getRecords(), TicketResponse.class);

        PageResponse<TicketResponse> response = new PageResponse<>();
        response.setTotal(page.getTotal());
        response.setList(list);
        return response;
    }

    /**
     * 统一查找实体并抛出异常
     */
    private Ticket findTicket(Long id) {
        return this.getOptById(id)
                .orElseThrow(() -> new NoSuchElementException("车票记录不存在"));
    }
}
