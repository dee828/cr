package com.example.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.business.entity.DailyTrainStation;
import com.example.business.mapper.DailyTrainStationMapper;
import com.example.business.request.DailyTrainStationListRequest;
import com.example.business.request.DailyTrainStationRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.DailyTrainStationResponse;
import com.example.business.service.DailyTrainStationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.example.common.exception.CustomValidationException;
import java.util.NoSuchElementException;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class DailyTrainStationServiceImpl extends ServiceImpl<DailyTrainStationMapper, DailyTrainStation> implements DailyTrainStationService {
    @Override
    @Transactional
    public boolean saveOrUpdate(DailyTrainStationRequest request) {
        DailyTrainStation dailyTrainStation;

        if (request.getId() != null) {
            // 更新逻辑
            dailyTrainStation = findDailyTrainStation(request.getId());
            BeanUtil.copyProperties(request, dailyTrainStation);
        } else {
            // 新增逻辑
            dailyTrainStation = BeanUtil.copyProperties(request, DailyTrainStation.class);
        }

        return this.saveOrUpdate(dailyTrainStation);
    }

    @Override
    @Transactional
    public boolean deleteDailyTrainStation(Long id) {
        DailyTrainStation dailyTrainStation = findDailyTrainStation(id);

        return this.removeById(id);
    }

    @Override
    public PageResponse<DailyTrainStationResponse> getDailyTrainStationPage(DailyTrainStationListRequest request) {
        LambdaQueryWrapper<DailyTrainStation> qw = buildQuery(request);

        return executePageQuery(request, qw);
    }

    @Override
    public PageResponse<DailyTrainStationResponse> getAdminDailyTrainStationPage(DailyTrainStationListRequest request) {
        LambdaQueryWrapper<DailyTrainStation> qw = buildQuery(request);

        return executePageQuery(request, qw);
    }

    @Override
    @Transactional
    public boolean adminAdd(DailyTrainStationRequest request) {

        DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(request, DailyTrainStation.class);
        return this.save(dailyTrainStation);
    }

    @Override
    @Transactional
    public boolean adminUpdate(DailyTrainStationRequest request) {
        DailyTrainStation dailyTrainStation = findDailyTrainStation(request.getId());

        // 管理员可以修改所有字段
        BeanUtil.copyProperties(request, dailyTrainStation);
        return this.updateById(dailyTrainStation);
    }

    @Override
    @Transactional
    public boolean adminDelete(Long id) {
        return this.removeById(findDailyTrainStation(id).getId());
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
    private LambdaQueryWrapper<DailyTrainStation> buildQuery(DailyTrainStationListRequest request) {
        LambdaQueryWrapper<DailyTrainStation> qw = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getKeyword())) {
            String kw = request.getKeyword();
            qw.like(DailyTrainStation::getTrainCode, kw);
        }

        qw.orderByDesc(DailyTrainStation::getCreatedAt);
        return qw;
    }

    /**
     * 执行分页查询并转换结果
     */
    private PageResponse<DailyTrainStationResponse> executePageQuery(DailyTrainStationListRequest request,
                                                             LambdaQueryWrapper<DailyTrainStation> qw) {
        Page<DailyTrainStation> page = this.page(Page.of(request.getPage(), request.getSize()), qw);

        List<DailyTrainStationResponse> list = BeanUtil.copyToList(page.getRecords(), DailyTrainStationResponse.class);

        PageResponse<DailyTrainStationResponse> response = new PageResponse<>();
        response.setTotal(page.getTotal());
        response.setList(list);
        return response;
    }

    /**
     * 统一查找实体并抛出异常
     */
    private DailyTrainStation findDailyTrainStation(Long id) {
        return this.getOptById(id)
                .orElseThrow(() -> new NoSuchElementException("每日火车车站不存在"));
    }
}
