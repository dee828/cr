package com.example.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.business.entity.Train;
import com.example.business.entity.TrainCarriage;
import com.example.business.entity.TrainSeat;
import com.example.business.enums.SeatCol;
import com.example.business.mapper.TrainMapper;
import com.example.business.mapper.TrainSeatMapper;
import com.example.business.request.TrainListRequest;
import com.example.business.request.TrainRequest;
import com.example.business.service.TrainCarriageService;
import com.example.common.response.PageResponse;
import com.example.business.response.TrainResponse;
import com.example.business.service.TrainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.example.common.exception.CustomValidationException;
import java.util.NoSuchElementException;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class TrainServiceImpl extends ServiceImpl<TrainMapper, Train> implements TrainService {
    @Override
    @Transactional
    public boolean saveOrUpdate(TrainRequest request) {
        Train train;

        if (request.getId() != null) {
            // 更新逻辑
            train = findTrain(request.getId());
            BeanUtil.copyProperties(request, train);
        } else {
            // 新增逻辑
            train = BeanUtil.copyProperties(request, Train.class);
        }

        return this.saveOrUpdate(train);
    }

    @Override
    @Transactional
    public boolean deleteTrain(Long id) {
        Train train = findTrain(id);

        return this.removeById(id);
    }

    @Override
    public PageResponse<TrainResponse> getTrainPage(TrainListRequest request) {
        LambdaQueryWrapper<Train> qw = buildQuery(request);

        return executePageQuery(request, qw);
    }

    @Override
    public PageResponse<TrainResponse> getAdminTrainPage(TrainListRequest request) {
        LambdaQueryWrapper<Train> qw = buildQuery(request);

        return executePageQuery(request, qw);
    }

    @Override
    @Transactional
    public boolean adminAdd(TrainRequest request) {

        Train train = BeanUtil.copyProperties(request, Train.class);
        return this.save(train);
    }

    @Override
    @Transactional
    public boolean adminUpdate(TrainRequest request) {
        Train train = findTrain(request.getId());

        // 管理员可以修改所有字段
        BeanUtil.copyProperties(request, train);
        return this.updateById(train);
    }

    @Override
    @Transactional
    public boolean adminDelete(Long id) {
        return this.removeById(findTrain(id).getId());
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
    private LambdaQueryWrapper<Train> buildQuery(TrainListRequest request) {
        LambdaQueryWrapper<Train> qw = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getKeyword())) {
            String kw = request.getKeyword();
            qw.like(Train::getCode, kw);
            qw.or().like(Train::getStart, kw);
            qw.or().like(Train::getStartPinyin, kw);
            qw.or().like(Train::getEnd, kw);
            qw.or().like(Train::getEndPinyin, kw);
        }

        qw.orderByDesc(Train::getCreatedAt);
        return qw;
    }

    /**
     * 执行分页查询并转换结果
     */
    private PageResponse<TrainResponse> executePageQuery(TrainListRequest request,
                                                             LambdaQueryWrapper<Train> qw) {
        Page<Train> page = this.page(Page.of(request.getPage(), request.getSize()), qw);

        List<TrainResponse> list = BeanUtil.copyToList(page.getRecords(), TrainResponse.class);

        PageResponse<TrainResponse> response = new PageResponse<>();
        response.setTotal(page.getTotal());
        response.setList(list);
        return response;
    }

    /**
     * 统一查找实体并抛出异常
     */
    private Train findTrain(Long id) {
        return this.getOptById(id)
                .orElseThrow(() -> new NoSuchElementException("车次不存在"));
    }


    private static final Logger log = LoggerFactory.getLogger(TrainServiceImpl.class);
    @Autowired
    TrainCarriageService trainCarriageService;
    @Autowired
    TrainSeatMapper trainSeatMapper;

    /**
     * 根据车次编号自动生成座位
     * @param trainCode 车次编号
     */
    public void genSeat(String trainCode) {
        // 1. 车次：[前置准备] 清空当前车次下的所有座位数据
        LambdaQueryWrapper<TrainSeat> qw = new LambdaQueryWrapper<>();
        qw.eq(TrainSeat::getTrainCode, trainCode);
        trainSeatMapper.delete(qw);
        log.debug("车次[{}]：[前置准备] 清空当前车次下的所有座位数据", trainCode);

        // 2. 车厢：查找当前车次下的所有车厢
        LambdaQueryWrapper<TrainCarriage> tcQw = new LambdaQueryWrapper<>();
        tcQw.eq(TrainCarriage::getTrainCode, trainCode);
        List<TrainCarriage> trainCarriages = trainCarriageService.list(tcQw);
        log.debug("车厢：查找当前车次下的所有车厢，共 {} 节", trainCarriages.size());

        // 3. 座位：循环生成每个车厢的座位
        for (TrainCarriage trainCarriage : trainCarriages) {
            // 3.1 获取车厢的详情：行数、座位类型(根据座位类型可以自动算出列数)
            Integer rowCount = trainCarriage.getRowCount();
            String seatType = trainCarriage.getSeatType();
            log.debug("获取车厢的详情：行数{}、座位类型{}", rowCount, seatType);
            int seatIndex = 1;

            // 3.2 根据座位类型，获取所有的列。比如车厢类型是一等座，则可以获取到列 A、C、D、F
            List<SeatCol> seatCols = SeatCol.getColsByType(seatType);
            log.debug("根据座位类型[{}]，获取所有的列[{}]", seatType, seatCols);

            // 3.3 行数：循环
            for (int row = 1; row <= rowCount; row++) {
                log.debug("开始生成车次{}，车厢{}，排数{}，具体座位{}", trainCode, trainCarriage.getIndex(), row, seatCols);
                // 3.4 列数：循环
                for (SeatCol seatCol: seatCols) {
                    // 3.5 自动构造出座位数据并入库
                    TrainSeat trainSeat = new TrainSeat();
                    trainSeat.setId(IdUtil.getSnowflakeNextId());
                    trainSeat.setTrainCode(trainCode);
                    trainSeat.setCarriageIndex(trainCarriage.getIndex());
                    trainSeat.setRow(StrUtil.fillBefore(String.valueOf(row), '0', 2));
                    trainSeat.setCol(seatCol.getCode());
                    trainSeat.setSeatType(seatType);
                    trainSeat.setCarriageSeatIndex(seatIndex++);
                    trainSeatMapper.insert(trainSeat);
                }
            }
        }
    }
}
