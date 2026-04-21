package com.example.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.business.entity.DailyTrainCarriage;
import com.example.business.mapper.DailyTrainCarriageMapper;
import com.example.business.request.DailyTrainCarriageListRequest;
import com.example.business.request.DailyTrainCarriageRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.DailyTrainCarriageResponse;
import com.example.business.service.DailyTrainCarriageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.example.common.exception.CustomValidationException;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class DailyTrainCarriageServiceImpl extends ServiceImpl<DailyTrainCarriageMapper, DailyTrainCarriage> implements DailyTrainCarriageService {
    @Override
    @Transactional
    public boolean saveOrUpdate(DailyTrainCarriageRequest request) {
        DailyTrainCarriage dailyTrainCarriage;

        if (request.getId() != null) {
            // 更新逻辑
            dailyTrainCarriage = findDailyTrainCarriage(request.getId());
            BeanUtil.copyProperties(request, dailyTrainCarriage);
        } else {
            // 新增逻辑
            dailyTrainCarriage = BeanUtil.copyProperties(request, DailyTrainCarriage.class);
        }

        return this.saveOrUpdate(dailyTrainCarriage);
    }

    @Override
    @Transactional
    public boolean deleteDailyTrainCarriage(Long id) {
        DailyTrainCarriage dailyTrainCarriage = findDailyTrainCarriage(id);

        return this.removeById(id);
    }

    @Override
    public PageResponse<DailyTrainCarriageResponse> getDailyTrainCarriagePage(DailyTrainCarriageListRequest request) {
        LambdaQueryWrapper<DailyTrainCarriage> qw = buildQuery(request);

        return executePageQuery(request, qw);
    }

    @Override
    public PageResponse<DailyTrainCarriageResponse> getAdminDailyTrainCarriagePage(DailyTrainCarriageListRequest request) {
        LambdaQueryWrapper<DailyTrainCarriage> qw = buildQuery(request);

        return executePageQuery(request, qw);
    }

    @Override
    @Transactional
    public boolean adminAdd(DailyTrainCarriageRequest request) {

        DailyTrainCarriage dailyTrainCarriage = BeanUtil.copyProperties(request, DailyTrainCarriage.class);
        return this.save(dailyTrainCarriage);
    }

    @Override
    @Transactional
    public boolean adminUpdate(DailyTrainCarriageRequest request) {
        DailyTrainCarriage dailyTrainCarriage = findDailyTrainCarriage(request.getId());

        // 管理员可以修改所有字段
        BeanUtil.copyProperties(request, dailyTrainCarriage);
        return this.updateById(dailyTrainCarriage);
    }

    @Override
    @Transactional
    public boolean adminDelete(Long id) {
        return this.removeById(findDailyTrainCarriage(id).getId());
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

    @Override
    public List<DailyTrainCarriage> selectBySeatType(LocalDate date, String trainCode, String seatType) {
        LambdaQueryWrapper<DailyTrainCarriage> qw = new LambdaQueryWrapper<>();
        qw.eq(DailyTrainCarriage::getDate, date);
        qw.eq(DailyTrainCarriage::getTrainCode, trainCode);
        qw.eq(DailyTrainCarriage::getSeatType, seatType);
        return this.list(qw);
    }

    // ==================== 逻辑抽取辅助方法 ====================

    /**
     * 构建基础查询对象 (处理通用过滤如关键词)
     */
    private LambdaQueryWrapper<DailyTrainCarriage> buildQuery(DailyTrainCarriageListRequest request) {
        LambdaQueryWrapper<DailyTrainCarriage> qw = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getKeyword())) {
            String kw = request.getKeyword();
            qw.like(DailyTrainCarriage::getTrainCode, kw);
        }

        qw.orderByDesc(DailyTrainCarriage::getCreatedAt);
        return qw;
    }

    /**
     * 执行分页查询并转换结果
     */
    private PageResponse<DailyTrainCarriageResponse> executePageQuery(DailyTrainCarriageListRequest request,
                                                             LambdaQueryWrapper<DailyTrainCarriage> qw) {
        Page<DailyTrainCarriage> page = this.page(Page.of(request.getPage(), request.getSize()), qw);

        List<DailyTrainCarriageResponse> list = BeanUtil.copyToList(page.getRecords(), DailyTrainCarriageResponse.class);

        PageResponse<DailyTrainCarriageResponse> response = new PageResponse<>();
        response.setTotal(page.getTotal());
        response.setList(list);
        return response;
    }

    /**
     * 统一查找实体并抛出异常
     */
    private DailyTrainCarriage findDailyTrainCarriage(Long id) {
        return this.getOptById(id)
                .orElseThrow(() -> new NoSuchElementException("每日火车车厢不存在"));
    }
}
