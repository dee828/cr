package com.example.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.business.entity.*;
import com.example.business.enums.SeatType;
import com.example.business.enums.TrainType;
import com.example.business.mapper.DailyTrainMapper;
import com.example.business.request.DailyTrainListRequest;
import com.example.business.request.DailyTrainRequest;
import com.example.business.service.*;
import com.example.common.response.PageResponse;
import com.example.business.response.DailyTrainResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.example.common.exception.CustomValidationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.NoSuchElementException;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class DailyTrainServiceImpl extends ServiceImpl<DailyTrainMapper, DailyTrain> implements DailyTrainService {
    private static final Logger log = LoggerFactory.getLogger(DailyTrainServiceImpl.class);

    @Override
    @Transactional
    public boolean saveOrUpdate(DailyTrainRequest request) {
        DailyTrain dailyTrain;

        if (request.getId() != null) {
            // 更新逻辑
            dailyTrain = findDailyTrain(request.getId());
            BeanUtil.copyProperties(request, dailyTrain);
        } else {
            // 新增逻辑
            dailyTrain = BeanUtil.copyProperties(request, DailyTrain.class);
        }

        return this.saveOrUpdate(dailyTrain);
    }

    @Override
    @Transactional
    public boolean deleteDailyTrain(Long id) {
        DailyTrain dailyTrain = findDailyTrain(id);

        return this.removeById(id);
    }

    @Override
    public PageResponse<DailyTrainResponse> getDailyTrainPage(DailyTrainListRequest request) {
        LambdaQueryWrapper<DailyTrain> qw = buildQuery(request);

        return executePageQuery(request, qw);
    }

    @Override
    public PageResponse<DailyTrainResponse> getAdminDailyTrainPage(DailyTrainListRequest request) {
        LambdaQueryWrapper<DailyTrain> qw = buildQuery(request);

        return executePageQuery(request, qw);
    }

    @Override
    @Transactional
    public boolean adminAdd(DailyTrainRequest request) {

        DailyTrain dailyTrain = BeanUtil.copyProperties(request, DailyTrain.class);
        return this.save(dailyTrain);
    }

    @Override
    @Transactional
    public boolean adminUpdate(DailyTrainRequest request) {
        DailyTrain dailyTrain = findDailyTrain(request.getId());

        // 管理员可以修改所有字段
        BeanUtil.copyProperties(request, dailyTrain);
        return this.updateById(dailyTrain);
    }

    @Override
    @Transactional
    public boolean adminDelete(Long id) {
        return this.removeById(findDailyTrain(id).getId());
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
    private LambdaQueryWrapper<DailyTrain> buildQuery(DailyTrainListRequest request) {
        LambdaQueryWrapper<DailyTrain> qw = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getKeyword())) {
            String kw = request.getKeyword();
            qw.like(DailyTrain::getCode, kw);
            qw.or().like(DailyTrain::getStart, kw);
            qw.or().like(DailyTrain::getStartPinyin, kw);
            qw.or().like(DailyTrain::getEnd, kw);
            qw.or().like(DailyTrain::getEndPinyin, kw);
        }

        qw.orderByDesc(DailyTrain::getCreatedAt);
        return qw;
    }

    /**
     * 执行分页查询并转换结果
     */
    private PageResponse<DailyTrainResponse> executePageQuery(DailyTrainListRequest request,
                                                             LambdaQueryWrapper<DailyTrain> qw) {
        Page<DailyTrain> page = this.page(Page.of(request.getPage(), request.getSize()), qw);

        List<DailyTrainResponse> list = BeanUtil.copyToList(page.getRecords(), DailyTrainResponse.class);

        PageResponse<DailyTrainResponse> response = new PageResponse<>();
        response.setTotal(page.getTotal());
        response.setList(list);
        return response;
    }

    /**
     * 统一查找实体并抛出异常
     */
    private DailyTrain findDailyTrain(Long id) {
        return this.getOptById(id)
                .orElseThrow(() -> new NoSuchElementException("每日车次不存在"));
    }

    @Autowired
    TrainService trainService;

    /**
     * 生成每日数据
     * 包含：每日车次、每日火车车站、每日火车车厢、每日车厢座位、余票信息
     * @param date 指定日期
     */
    public void genDaily(LocalDate date){
        // 查出所有的车次
        List<Train> trainList = trainService.list();
        //   循环所有的车次
        for (Train train : trainList) {
            genDailyTrain(date, train);
            genDailyTrainStation(date, train);
            genDailyTrainCarriage(date, train);
            genDailyTrainSeat(date, train);
            genDailyTrainTicket(date, train);
        }
    }

    private void genDailyTrain(LocalDate date, Train train){
        log.info("生成日期 = {}，车次编号 = {} 的【每日车次】数据 - 开始", date, train.getCode());
        // 先删除指定日期、指定车次的已有数据
        LambdaQueryWrapper<DailyTrain> qw = new LambdaQueryWrapper<>();
        qw.eq(DailyTrain::getCode, train.getCode());
        qw.eq(DailyTrain::getDate, date);
        this.remove(qw);

        // 生成指定日期的每日车次
        DailyTrain dailyTrain = BeanUtil.copyProperties(train, DailyTrain.class);
        dailyTrain.setDate(date);
        dailyTrain.setId(IdUtil.getSnowflakeNextId());
        this.save(dailyTrain);
        log.info("生成日期 = {}，车次编号 = {} 的【每日车次】数据 - 完成", date, train.getCode());
    }

    @Autowired
    TrainStationService trainStationService;
    @Autowired
    DailyTrainStationService dailyTrainStationService;

    private void genDailyTrainStation(LocalDate date, Train train){
        log.info("生成日期 = {}，车次编号 = {} 的【每日火车车站】数据 - 开始", date, train.getCode());
        // 先删除指定日期、指定车次的已有数据
        LambdaQueryWrapper<DailyTrainStation> qw = new LambdaQueryWrapper<>();
        qw.eq(DailyTrainStation::getTrainCode, train.getCode());
        qw.eq(DailyTrainStation::getDate, date);
        dailyTrainStationService.remove(qw);

        // 查出基础数据之车站基础数据
        LambdaQueryWrapper<TrainStation> qwByTrainCode = new LambdaQueryWrapper<>();
        qwByTrainCode.eq(TrainStation::getTrainCode, train.getCode());
        List<TrainStation> list = trainStationService.list(qwByTrainCode);
        for (TrainStation trainStation : list) {
            // 在查到的基础数据上增加每日数据需要的日期字段和其他相关字段
            DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(trainStation, DailyTrainStation.class);
            dailyTrainStation.setDate(date);
            dailyTrainStation.setId(IdUtil.getSnowflakeNextId());
            dailyTrainStationService.save(dailyTrainStation);
        }
        log.info("正在生成日期 = {}，车次编号 = {} 的【每日火车车站】数据 - 完成", date, train.getCode());
    }

    @Autowired
    TrainCarriageService trainCarriageService;
    @Autowired
    DailyTrainCarriageService dailyTrainCarriageService;

    private void genDailyTrainCarriage(LocalDate date, Train train){
        log.info("生成日期 = {}，车次编号 = {} 的【每日火车车厢】数据 - 开始", date, train.getCode());
        // 先删除指定日期、指定车次的已有数据
        LambdaQueryWrapper<DailyTrainCarriage> qw = new LambdaQueryWrapper<>();
        qw.eq(DailyTrainCarriage::getTrainCode, train.getCode());
        qw.eq(DailyTrainCarriage::getDate, date);
        dailyTrainCarriageService.remove(qw);

        // 查出基础数据之车站基础数据
        LambdaQueryWrapper<TrainCarriage> qwByTrainCode = new LambdaQueryWrapper<>();
        qwByTrainCode.eq(TrainCarriage::getTrainCode, train.getCode());
        List<TrainCarriage> list = trainCarriageService.list(qwByTrainCode);
        for (TrainCarriage trainCarriage : list) {
            // 在查到的基础数据上增加每日数据需要的日期字段和其他相关字段
            DailyTrainCarriage dailyTrainCarriage = BeanUtil.copyProperties(trainCarriage, DailyTrainCarriage.class);
            dailyTrainCarriage.setDate(date);
            dailyTrainCarriage.setId(IdUtil.getSnowflakeNextId());
            dailyTrainCarriageService.save(dailyTrainCarriage);
        }
        log.info("生成日期 = {}，车次编号 = {} 的【每日火车车厢】数据 - 完成", date, train.getCode());
    }

    @Autowired
    TrainSeatService trainSeatService;
    @Autowired
    DailyTrainSeatService dailyTrainSeatService;

    private void genDailyTrainSeat(LocalDate date, Train train){
        log.info("生成日期 = {}，车次编号 = {} 的【每日车厢座位】数据 - 开始", date, train.getCode());
        // 先删除指定日期、指定车次的已有数据
        LambdaQueryWrapper<DailyTrainSeat> qw = new LambdaQueryWrapper<>();
        qw.eq(DailyTrainSeat::getTrainCode, train.getCode());
        qw.eq(DailyTrainSeat::getDate, date);
        dailyTrainSeatService.remove(qw);

        // 查出基础数据之车站基础数据
        LambdaQueryWrapper<TrainSeat> qwByTrainCode = new LambdaQueryWrapper<>();
        qwByTrainCode.eq(TrainSeat::getTrainCode, train.getCode());
        List<TrainSeat> list = trainSeatService.list(qwByTrainCode);

        // --- --- ---
        // 查出当前车次经过几个车站
        LambdaQueryWrapper<TrainStation> qwByTrainCodeForSell = new LambdaQueryWrapper<>();
        qwByTrainCodeForSell.eq(TrainStation::getTrainCode, train.getCode());
        List<TrainStation> trainStationList = trainStationService.list(qwByTrainCodeForSell);
        // sell =  (车站个数 - 1) 个 0
        String sell = StrUtil.fillBefore("", '0', trainStationList.size() - 1);
        // --- --- ---

        for (TrainSeat trainSeat : list) {
            // 在查到的基础数据上增加每日数据需要的日期字段和其他相关字段
            DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(trainSeat, DailyTrainSeat.class);
            dailyTrainSeat.setDate(date);
            dailyTrainSeat.setSell(sell);
            dailyTrainSeat.setId(IdUtil.getSnowflakeNextId());
            dailyTrainSeatService.save(dailyTrainSeat);
        }
        log.info("生成日期 = {}，车次编号 = {} 的【每日车厢座位】数据 - 完成", date, train.getCode());
    }

    @Autowired
    DailyTrainTicketService dailyTrainTicketService;

    private void genDailyTrainTicket(LocalDate date, Train train){
        log.info("生成日期 = {}，车次编号 = {} 的【余票信息】数据 - 开始", date, train.getCode());
        // 先删除指定日期、指定车次的已有数据
        LambdaQueryWrapper<DailyTrainTicket> qw = new LambdaQueryWrapper<>();
        qw.eq(DailyTrainTicket::getTrainCode, train.getCode());
        qw.eq(DailyTrainTicket::getDate, date);
        dailyTrainTicketService.remove(qw);

        // 查出当前车次经过几个车站
        LambdaQueryWrapper<TrainStation> qwByTrainCodeForSell = new LambdaQueryWrapper<>();
        qwByTrainCodeForSell.eq(TrainStation::getTrainCode, train.getCode());
        List<TrainStation> trainStationList = trainStationService.list(qwByTrainCodeForSell);
        log.debug("当前车次【{}】经过的车站总数={}", train.getCode(), trainStationList.size());
        for (int i = 0; i < trainStationList.size(); i++) {
            // 第一层循环 得到出发站
            TrainStation trainStationStart = trainStationList.get(i);
            BigDecimal kmSum = BigDecimal.ZERO;
            for (int j = (i + 1); j < trainStationList.size(); j++) {
                // 第二层循环 得到到达站
                TrainStation trainStationEnd = trainStationList.get(j);
                kmSum = kmSum.add(trainStationEnd.getKm());

                // 构造余票信息数据
                DailyTrainTicket dailyTrainTicket = new DailyTrainTicket();

                dailyTrainTicket.setId(IdUtil.getSnowflakeNextId());
                dailyTrainTicket.setDate(date);
                dailyTrainTicket.setTrainCode(train.getCode());

                dailyTrainTicket.setStart(trainStationStart.getName());
                dailyTrainTicket.setStartPinyin(trainStationStart.getNamePinyin());
                dailyTrainTicket.setStartTime(trainStationStart.getOutTime());
                dailyTrainTicket.setStartIndex(trainStationStart.getIndex());

                dailyTrainTicket.setEnd(trainStationEnd.getName());
                dailyTrainTicket.setEndPinyin(trainStationEnd.getNamePinyin());
                dailyTrainTicket.setEndTime(trainStationEnd.getInTime());
                dailyTrainTicket.setEndIndex(trainStationEnd.getIndex());

                int ydzCount = dailyTrainSeatService.countSeat(date, train.getCode(), SeatType.YDZ.getCode());
                int edzCount = dailyTrainSeatService.countSeat(date, train.getCode(), SeatType.EDZ.getCode());

                // 系数一：火车类型
                BigDecimal priceRate = EnumUtil.getFieldBy(TrainType::getPriceRate, TrainType::getCode, train.getType());
                // 系数二：座位类型（座位单价）
                BigDecimal ydzPriceBySeatType = SeatType.YDZ.getPrice();
                // 计算票价 = 里程(公里) * 座位单价(元/公里) * 火车类型(系数)
                BigDecimal ydzPrice = kmSum.multiply(priceRate).multiply(ydzPriceBySeatType).setScale(2, RoundingMode.HALF_UP);
                BigDecimal edzPrice = kmSum.multiply(priceRate).multiply(SeatType.EDZ.getPrice()).setScale(2, RoundingMode.HALF_UP);

                dailyTrainTicket.setYdz(ydzCount);
                dailyTrainTicket.setYdzPrice(ydzPrice);
                dailyTrainTicket.setEdz(edzCount);
                dailyTrainTicket.setEdzPrice(edzPrice);

                dailyTrainTicketService.save(dailyTrainTicket);
            }
        }
        log.info("生成日期 = {}，车次编号 = {} 的【余票信息】数据 - 完成", date, train.getCode());
    }
}
