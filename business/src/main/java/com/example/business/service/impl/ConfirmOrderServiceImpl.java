package com.example.business.service.impl;

import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.example.business.entity.DailyTrainTicket;
import com.example.business.enums.ConfirmOrderStatus;
import com.example.business.enums.SeatType;
import com.example.common.exception.CustomBusinessException;
import com.example.business.request.ConfirmOrderTicketRequest;
import com.example.business.service.DailyTrainTicketService;
import com.example.common.core.UserContext;
import com.example.common.exception.CustomForbiddenException;
import cn.hutool.core.bean.BeanUtil;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.business.entity.ConfirmOrder;
import com.example.business.mapper.ConfirmOrderMapper;
import com.example.business.request.ConfirmOrderListRequest;
import com.example.business.request.ConfirmOrderRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.ConfirmOrderResponse;
import com.example.business.service.ConfirmOrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.example.common.exception.CustomValidationException;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class ConfirmOrderServiceImpl extends ServiceImpl<ConfirmOrderMapper, ConfirmOrder> implements ConfirmOrderService {
    private static final Logger log = LoggerFactory.getLogger(ConfirmOrderServiceImpl.class);

    @Autowired
    DailyTrainTicketService dailyTrainTicketService;

    @Override
    @Transactional
    public boolean saveOrUpdate(ConfirmOrderRequest request) {
        Long currentUserId = UserContext.get();
        ConfirmOrder confirmOrder;

        if (request.getId() != null) {
            // 更新逻辑
            confirmOrder = findConfirmOrder(request.getId());
            // 校验所有权
            if (!request.getUserId().equals(currentUserId)) {
                throw new CustomForbiddenException("无权操作此确认订单信息");
            }
            BeanUtil.copyProperties(request, confirmOrder);
        } else {
            // 新增逻辑
            confirmOrder = BeanUtil.copyProperties(request, ConfirmOrder.class);
            // 强制绑定当前用户
            confirmOrder.setUserId(currentUserId);
        }

        return this.saveOrUpdate(confirmOrder);
    }

    @Override
    @Transactional
    public boolean deleteConfirmOrder(Long id) {
        ConfirmOrder confirmOrder = findConfirmOrder(id);

        // 安全校验：只能删除自己的确认订单
        if (!confirmOrder.getUserId().equals(UserContext.get())) {
            throw new CustomForbiddenException("无权删除此确认订单信息");
        }

        return this.removeById(id);
    }

    @Override
    public PageResponse<ConfirmOrderResponse> getConfirmOrderPage(ConfirmOrderListRequest request) {
        LambdaQueryWrapper<ConfirmOrder> qw = buildQuery(request);
        // 强制限定搜索范围为当前用户 (水平越权防护)
        qw.eq(ConfirmOrder::getUserId, UserContext.get());

        return executePageQuery(request, qw);
    }

    @Override
    public PageResponse<ConfirmOrderResponse> getAdminConfirmOrderPage(ConfirmOrderListRequest request) {
        LambdaQueryWrapper<ConfirmOrder> qw = buildQuery(request);

        // 管理员可选：按用户ID筛选
        if (request.getUserId() != null) {
            qw.eq(ConfirmOrder::getUserId, request.getUserId());
        }

        return executePageQuery(request, qw);
    }

    @Override
    @Transactional
    public boolean adminAdd(ConfirmOrderRequest request) {
        if (request.getUserId() == null) {
            throw new CustomValidationException("管理员添加确认订单必须指定 userId");
        }

        ConfirmOrder confirmOrder = BeanUtil.copyProperties(request, ConfirmOrder.class);
        // 使用请求中的 userId（管理员可以指定任意用户）
        confirmOrder.setUserId(request.getUserId());
        return this.save(confirmOrder);
    }

    @Override
    @Transactional
    public boolean adminUpdate(ConfirmOrderRequest request) {
        ConfirmOrder confirmOrder = findConfirmOrder(request.getId());

        // 管理员可以修改所有字段，包括 userId（允许调整数据归属关系）
        BeanUtil.copyProperties(request, confirmOrder);
        return this.updateById(confirmOrder);
    }

    @Override
    @Transactional
    public boolean adminDelete(Long id) {
        return this.removeById(findConfirmOrder(id).getId());
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
    private LambdaQueryWrapper<ConfirmOrder> buildQuery(ConfirmOrderListRequest request) {
        LambdaQueryWrapper<ConfirmOrder> qw = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getKeyword())) {
            String kw = request.getKeyword();
            qw.like(ConfirmOrder::getTrainCode, kw);
            qw.or().like(ConfirmOrder::getStart, kw);
            qw.or().like(ConfirmOrder::getEnd, kw);
        }

        qw.orderByDesc(ConfirmOrder::getCreatedAt);
        return qw;
    }

    /**
     * 执行分页查询并转换结果
     */
    private PageResponse<ConfirmOrderResponse> executePageQuery(ConfirmOrderListRequest request,
                                                             LambdaQueryWrapper<ConfirmOrder> qw) {
        Page<ConfirmOrder> page = this.page(Page.of(request.getPage(), request.getSize()), qw);

        List<ConfirmOrderResponse> list = BeanUtil.copyToList(page.getRecords(), ConfirmOrderResponse.class);

        PageResponse<ConfirmOrderResponse> response = new PageResponse<>();
        response.setTotal(page.getTotal());
        response.setList(list);
        return response;
    }

    /**
     * 统一查找实体并抛出异常
     */
    private ConfirmOrder findConfirmOrder(Long id) {
        return this.getOptById(id)
                .orElseThrow(() -> new NoSuchElementException("确认订单不存在"));
    }

    public void confirm(@Valid ConfirmOrderRequest request) {
        System.out.println(request);
        // 额外的业务数据校验【暂略】如：车次是否存在；同乘客同车次的票是否已经买过了；等等...；当前先聚焦以下核心功能

        LocalDate date = request.getDate();
        String trainCode = request.getTrainCode();
        String start = request.getStart();
        String end = request.getEnd();
        // 保存到【确认订单】表，订单状态赋初始值
        ConfirmOrder confirmOrder = new ConfirmOrder();
        confirmOrder.setId(IdUtil.getSnowflakeNextId());
        confirmOrder.setUserId(UserContext.get());
        confirmOrder.setDate(date);
        confirmOrder.setTrainCode(trainCode);
        confirmOrder.setStart(start);
        confirmOrder.setEnd(end);
        confirmOrder.setDailyTrainTicketId(request.getDailyTrainTicketId());
        confirmOrder.setStatus(ConfirmOrderStatus.INIT.getCode());
        confirmOrder.setTickets(JSONUtil.toJsonStr(request.getTickets()));
        this.save(confirmOrder);

        // 查询实时【余票信息】（用户在前端界面看到有票，等到真正下单时有可能被抢完了）
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
        log.debug("预扣减余票之前：{}", dailyTrainTicket);

        // 预扣减余票数量，并判断余票是否足够
        preReduceTicketCount(request, dailyTrainTicket);
        log.debug("预扣减余票之后：{}", dailyTrainTicket);

        // 选座
        // 遍历车厢，获取每个车厢的座位数据
        // 选座：挑选符合条件的座位，如果这个车厢不满足，则进入下个车厢（隐藏条件：多个选座必须在同一个车厢）

        // 选中座位后的事务处理：
        // 座位表修改售卖情况 sell 字段
        // 真实扣减库存，更新【余票信息】的余票
        // 记录会员的购票记录
        // 更新【确认订单】表的订单状态=成功
    }

    private void preReduceTicketCount(ConfirmOrderRequest request, DailyTrainTicket dailyTrainTicket) {
        // 从用户请求中获取用户要购买的票数
        List<ConfirmOrderTicketRequest> tickets = request.getTickets();
        // 循环操作
        for (ConfirmOrderTicketRequest ticketRequest : tickets) {
            String seatTypeCode = ticketRequest.getSeatTypeCode();
            SeatType seatType = EnumUtil.getBy(SeatType::getCode, seatTypeCode);
            switch(seatType){
                case YDZ -> {
                    int countAfterReduce = dailyTrainTicket.getYdz() - 1;
                    if (countAfterReduce < 0){
                        throw new CustomBusinessException("余票不足");
                    }
                    dailyTrainTicket.setYdz(countAfterReduce);
                }
                case EDZ -> {
                    int countAfterReduce = dailyTrainTicket.getEdz() - 1;
                    if (countAfterReduce < 0){
                        throw new CustomBusinessException("余票不足");
                    }
                    dailyTrainTicket.setEdz(countAfterReduce);
                }
            }
        }
    }
}
