package com.example.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.business.entity.DailyTrainSeat;
import com.example.business.mapper.DailyTrainSeatMapper;
import com.example.business.request.DailyTrainSeatListRequest;
import com.example.business.request.DailyTrainSeatRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.DailyTrainSeatResponse;
import com.example.business.service.DailyTrainSeatService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.example.common.exception.CustomValidationException;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class DailyTrainSeatServiceImpl extends ServiceImpl<DailyTrainSeatMapper, DailyTrainSeat> implements DailyTrainSeatService {
    @Override
    @Transactional
    public boolean saveOrUpdate(DailyTrainSeatRequest request) {
        DailyTrainSeat dailyTrainSeat;

        if (request.getId() != null) {
            // 更新逻辑
            dailyTrainSeat = findDailyTrainSeat(request.getId());
            BeanUtil.copyProperties(request, dailyTrainSeat);
        } else {
            // 新增逻辑
            dailyTrainSeat = BeanUtil.copyProperties(request, DailyTrainSeat.class);
        }

        return this.saveOrUpdate(dailyTrainSeat);
    }

    @Override
    @Transactional
    public boolean deleteDailyTrainSeat(Long id) {
        DailyTrainSeat dailyTrainSeat = findDailyTrainSeat(id);

        return this.removeById(id);
    }

    @Override
    public PageResponse<DailyTrainSeatResponse> getDailyTrainSeatPage(DailyTrainSeatListRequest request) {
        LambdaQueryWrapper<DailyTrainSeat> qw = buildQuery(request);

        return executePageQuery(request, qw);
    }

    @Override
    public PageResponse<DailyTrainSeatResponse> getAdminDailyTrainSeatPage(DailyTrainSeatListRequest request) {
        LambdaQueryWrapper<DailyTrainSeat> qw = buildQuery(request);

        return executePageQuery(request, qw);
    }

    @Override
    @Transactional
    public boolean adminAdd(DailyTrainSeatRequest request) {

        DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(request, DailyTrainSeat.class);
        return this.save(dailyTrainSeat);
    }

    @Override
    @Transactional
    public boolean adminUpdate(DailyTrainSeatRequest request) {
        DailyTrainSeat dailyTrainSeat = findDailyTrainSeat(request.getId());

        // 管理员可以修改所有字段
        BeanUtil.copyProperties(request, dailyTrainSeat);
        return this.updateById(dailyTrainSeat);
    }

    @Override
    @Transactional
    public boolean adminDelete(Long id) {
        return this.removeById(findDailyTrainSeat(id).getId());
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
    private LambdaQueryWrapper<DailyTrainSeat> buildQuery(DailyTrainSeatListRequest request) {
        LambdaQueryWrapper<DailyTrainSeat> qw = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getKeyword())) {
            String kw = request.getKeyword();
            qw.like(DailyTrainSeat::getTrainCode, kw);
        }

        qw.orderByDesc(DailyTrainSeat::getCreatedAt);
        return qw;
    }

    /**
     * 执行分页查询并转换结果
     */
    private PageResponse<DailyTrainSeatResponse> executePageQuery(DailyTrainSeatListRequest request,
                                                             LambdaQueryWrapper<DailyTrainSeat> qw) {
        Page<DailyTrainSeat> page = this.page(Page.of(request.getPage(), request.getSize()), qw);

        List<DailyTrainSeatResponse> list = BeanUtil.copyToList(page.getRecords(), DailyTrainSeatResponse.class);

        PageResponse<DailyTrainSeatResponse> response = new PageResponse<>();
        response.setTotal(page.getTotal());
        response.setList(list);
        return response;
    }

    /**
     * 统一查找实体并抛出异常
     */
    private DailyTrainSeat findDailyTrainSeat(Long id) {
        return this.getOptById(id)
                .orElseThrow(() -> new NoSuchElementException("每日车厢座位不存在"));
    }

    @Override
    public int countSeat(LocalDate date, String code, String seatType) {
        LambdaQueryWrapper<DailyTrainSeat> qw = new LambdaQueryWrapper<>();
        qw.eq(DailyTrainSeat::getDate, date);
        qw.eq(DailyTrainSeat::getTrainCode, code);
        qw.eq(DailyTrainSeat::getSeatType, seatType);
        long seatCount = this.count(qw);
        if (seatCount == 0L){
            seatCount = -1;
        }
        return (int) seatCount;
    }
}
