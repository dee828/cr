package com.example.business.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.example.business.entity.DailyTrainCarriage;
import com.example.business.entity.DailyTrainSeat;
import com.example.business.entity.DailyTrainTicket;
import com.example.business.enums.ConfirmOrderStatus;
import com.example.business.enums.SeatCol;
import com.example.business.enums.SeatType;
import com.example.business.service.DailyTrainCarriageService;
import com.example.business.service.DailyTrainSeatService;
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
import java.util.ArrayList;
import java.util.NoSuchElementException;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class ConfirmOrderServiceImpl extends ServiceImpl<ConfirmOrderMapper, ConfirmOrder> implements ConfirmOrderService {
    private static final Logger log = LoggerFactory.getLogger(ConfirmOrderServiceImpl.class);

    @Autowired
    DailyTrainTicketService dailyTrainTicketService;

    @Autowired
    DailyTrainCarriageService dailyTrainCarriageService;

    @Autowired
    DailyTrainSeatService dailyTrainSeatService;

    @Autowired
    AfterConfirmOrderService afterConfirmOrderService;

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

    public synchronized void confirm(@Valid ConfirmOrderRequest request) {
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
        // 最终的选座结果
        List<DailyTrainSeat> finalSeatList = new ArrayList<>();
          // 区分是否主动选座
        ConfirmOrderTicketRequest firstTicketRequest = request.getTickets().get(0);
        if (StrUtil.isNotBlank(firstTicketRequest.getSeat())) {
            // 有主动选座
            log.info("本次用户购票【有】主动选座");

            // 辅助：计算座位的相对偏移值，提高多个座位的选座效率
            // 查出用户所选座位类型（总共包含哪些列）比如: 二等座 - A B C D F
            String seatTypeCode = firstTicketRequest.getSeatTypeCode();
            List<SeatCol> colEnumList = SeatCol.getColsByType(seatTypeCode);
            log.info("本次选座的座位类型={}，包含的列={}", SeatType.getEnumByCode(seatTypeCode), colEnumList);
            // 组成和前端一样的两排初始座位列表，用作参照的两排座位
            List<String> referSeatList = new ArrayList<>();
            for (int i = 1; i <= 2; i++) {
                for (SeatCol seatCol : colEnumList) {
                    referSeatList.add(seatCol.getCode() + i);
                }
            }
            log.info("用作参照的两排座位={}", referSeatList);
            // 绝对偏移值
            List<Integer> aboluteOffsetList = new ArrayList<>();
            List<Integer> offsetList = new ArrayList<>();
            for (ConfirmOrderTicketRequest ticketReq : request.getTickets()) {
                int index = referSeatList.indexOf(ticketReq.getSeat());
                aboluteOffsetList.add(index);
            }
            log.info("计算得到所选座位的绝对偏移值={}", aboluteOffsetList);
            // 真正需要的偏移值（相对于第一个座位的偏移值）
            for (Integer index : aboluteOffsetList) {
                int offset = index - aboluteOffsetList.get(0);
                offsetList.add(offset);
            }
            log.info("计算得到所选座位相对第一个座位的偏移值={}", offsetList);
            // 挑座位 - 按需
            getSeat(finalSeatList, date, trainCode, seatTypeCode, firstTicketRequest.getSeat().split("")[0], offsetList, dailyTrainTicket.getStartIndex(), dailyTrainTicket.getEndIndex());
        } else {
            // 没有主动选座
            log.info("本次用户购票【无】主动选座");
            // 挑座位 - 随机
            for (ConfirmOrderTicketRequest ticket : request.getTickets()) {
                getSeat(finalSeatList, date, trainCode, ticket.getSeatTypeCode(), null, null, dailyTrainTicket.getStartIndex(), dailyTrainTicket.getEndIndex());
            }
        }
        log.info("最终的选座结果={}", finalSeatList);

        // 选中座位后的事务处理：
        //     更新座位表的新售卖情况 sell 字段值
        //     真实扣减库存，更新【余票信息】表的对应余票字段值
        //     保存到【车票记录】表
        //     更新【确认订单】表的订单状态=成功
        afterConfirmOrderService.afterConfirm(finalSeatList, dailyTrainTicket, request.getTickets(), confirmOrder);
    }

    /**
     * 挑座位
     *   如果有选座：根据之前计算好的【选座偏移值】可以一次性挑完，无需循环
     *   如果无选座：一个一个挑，遇到未售的就选中
     *
     * @param finalSeatList 最终选座结果
     * @param date          日期
     * @param trainCode     车次编号
     * @param seatType      座位类型
     * @param column        用户选座的座位列
     * @param offsetList    座位偏移值
     * @param startIndex    出发站序
     * @param endIndex      到站站序
     */
    private void getSeat(List<DailyTrainSeat> finalSeatList, LocalDate date, String trainCode, String seatType, String column, List<Integer> offsetList, Integer startIndex, Integer endIndex) {
        List<DailyTrainSeat> getSeatList = new ArrayList<>();

        List<DailyTrainCarriage> carriageList = dailyTrainCarriageService.selectBySeatType(date, trainCode, seatType);
        log.info("查到符合条件(seatType={})的车厢数量={}", seatType, carriageList.size());
        // 一个车厢一个车厢的获取数据
        for (DailyTrainCarriage dailyTrainCarriage : carriageList) {
            // 换车厢时，需要清空【临时/不完整】的选座信息 getSeatList
            getSeatList = new ArrayList<>();

            Integer trainCarriageIndex = dailyTrainCarriage.getIndex();
            log.info("开始从序号={}的车厢选座", trainCarriageIndex);
            List<DailyTrainSeat> seatList = dailyTrainSeatService.selectByCarriage(date, trainCode, trainCarriageIndex);
            log.info("车厢序号={}，座位数={}", trainCarriageIndex, seatList.size());

            for (int i = 0; i < seatList.size(); i++) {
                DailyTrainSeat dailyTrainSeat = seatList.get(i);
                Integer seatIndex = dailyTrainSeat.getCarriageSeatIndex();
                String col = dailyTrainSeat.getCol();

                //判断当前的座位是否被选中过
                boolean alreadyChooseFlag = false;
                for (DailyTrainSeat finalSeat : finalSeatList) {
                    if (finalSeat.getId().equals(dailyTrainSeat.getId())) {
                        alreadyChooseFlag = true;
                        break;
                    }
                }
                if (alreadyChooseFlag) {
                    log.info("座位={}在当前选座过程中已被选中过，不能重复选择，继续判断下一个座位", seatIndex);
                    continue;
                }

                // 判断列号 column，有值的情况下要对比列号，也就是【有选座】的情况
                if (StrUtil.isBlank(column)){
                    log.info("无选座");
                } else {
                    if (!column.equals(col)){
                        log.info("座位={}列值不对，继续判断下一个座位，当前列值={}，目标列值={}", seatIndex, col, column);
                        continue;
                    }
                }

                boolean isChoose = calSell(dailyTrainSeat, startIndex, endIndex);
                if (isChoose) {
                    log.info("选中座位");
                    getSeatList.add(dailyTrainSeat);
                } else {
                    //log.info("未选中座位");
                    continue;
                }

                // 根据偏移值 offset 选择剩下的座位
                boolean isGetAllOffsetSeat = true;
                if (CollUtil.isNotEmpty(offsetList)) {
                    log.info("有偏移值={}，根据偏移值校验座位是否可选", offsetList);
                    // 从索引 1 开始，索引 0 就是当前已选中的票
                    for (int j = 1; j < offsetList.size(); j++) {
                        Integer offset = offsetList.get(j);
                        int nextIndex = i + offset;

                        // 额外判断：【有选座】的情况下，一定要在同一个车箱
                        if (nextIndex >= seatList.size()) {
                            log.info("座位={}不可选，偏移后的索引超出了当前车厢的座位数={}", nextIndex, seatList.size());
                            isGetAllOffsetSeat = false;
                            break;
                        }

                        DailyTrainSeat nextDailyTrainSeat = seatList.get(nextIndex);
                        boolean isChooseNext = calSell(nextDailyTrainSeat, startIndex, endIndex);
                        if (isChooseNext) {
                            log.info("座位={}被选中", nextDailyTrainSeat.getCarriageSeatIndex());
                            getSeatList.add(nextDailyTrainSeat);
                        } else {
                            log.info("座位={}不可选", nextDailyTrainSeat.getCarriageSeatIndex());
                            isGetAllOffsetSeat = false;
                            break;
                        }
                    }
                }
                if (!isGetAllOffsetSeat) {
                    // 未完全选中所有座位时，需要清空【临时/不完整】的选座信息
                    getSeatList = new ArrayList<>();
                    continue;
                }

                // 保存最终的选座结果
                finalSeatList.addAll(getSeatList);
                return;
            }
        }
    }

    /**
     * 计算某座位在区间内是否可卖
     * 例：sell=10001
     *   - 第一位1表示0~1（起始站到第二个站），第二位0代表1~2（第2个站到第3个站） ...
     *   - 这个值表示第 0(起始站)~1 卖出去了，第 4~5 卖出去了
     *   - 假如本次购买区间是 1~4，这区间的对应值是 000
     * 全部是 0，表示这个区间可买；只要有 1，就表示区间内已售过票
     * <p>
     * 选中后，要计算购票后的 sell，比如原来是 10001，本次购买区间 1~4
     * 方案：构造本次购票完成之后的新 sell=01110，和原来的 sell=10001【按位或】最终得到 11111
     */
    private boolean calSell(DailyTrainSeat dailyTrainSeat, Integer startIndex, Integer endIndex) {
        // 拿到座位的当前售卖情况
        String sell = dailyTrainSeat.getSell();
        // 拿到它的一小部分
        String sellPart = sell.substring(startIndex, endIndex);
        if (Integer.parseInt(sellPart) > 0){
            log.info("座位={}在本次车站区间={}~{}已出售，不可选", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);
            return false;
        } else {
            log.info("座位={}在本次车站区间={}~{}未出售，可选", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);
            //  111,   111
            String curSell = sellPart.replace('0', '1');
            // 0111,  0111
            curSell = StrUtil.fillBefore(curSell, '0', endIndex);
            // 01110, 01110
            curSell = StrUtil.fillAfter(curSell, '0', sell.length());

            // 当前区间售票信息 curSell=01110 与数据库中已售信息 sell=00001 按位或，即可得到该座位卖出此票后的新售票详情
            // 15(01111), 14(01110 = 01110|00000)
            int newSellInt = NumberUtil.binaryToInt(curSell) | NumberUtil.binaryToInt(sell);
            //  1111,  1110
            String newSell = NumberUtil.getBinaryStr(newSellInt);
            // 01111, 01110
            newSell = StrUtil.fillBefore(newSell, '0', sell.length());
            log.info("座位={}被选中，原sell={}，车站区间={}~{}，即：{}，本次购票完成之后的新sell={}"
                    , dailyTrainSeat.getCarriageSeatIndex(), sell, startIndex, endIndex, curSell, newSell);
            // 更新 sell 值（修改了内存中 dailyTrainSeat 对象的 sell 值；还未更新到数据库）
            dailyTrainSeat.setSell(newSell);
            return true;
        }
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
