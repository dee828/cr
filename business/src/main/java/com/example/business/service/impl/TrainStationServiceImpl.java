package com.example.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.business.entity.TrainStation;
import com.example.business.mapper.TrainStationMapper;
import com.example.business.request.TrainStationListRequest;
import com.example.business.request.TrainStationRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.TrainStationResponse;
import com.example.business.service.TrainStationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.example.common.exception.CustomValidationException;
import java.util.NoSuchElementException;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class TrainStationServiceImpl extends ServiceImpl<TrainStationMapper, TrainStation> implements TrainStationService {
    @Override
    @Transactional
    public boolean saveOrUpdate(TrainStationRequest request) {
        TrainStation trainStation;

        if (request.getId() != null) {
            // 更新逻辑
            trainStation = findTrainStation(request.getId());
            BeanUtil.copyProperties(request, trainStation);
        } else {
            // 新增逻辑
            trainStation = BeanUtil.copyProperties(request, TrainStation.class);
        }

        return this.saveOrUpdate(trainStation);
    }

    @Override
    @Transactional
    public boolean deleteTrainStation(Long id) {
        TrainStation trainStation = findTrainStation(id);

        return this.removeById(id);
    }

    @Override
    public PageResponse<TrainStationResponse> getTrainStationPage(TrainStationListRequest request) {
        LambdaQueryWrapper<TrainStation> qw = buildQuery(request);

        return executePageQuery(request, qw);
    }

    @Override
    public PageResponse<TrainStationResponse> getAdminTrainStationPage(TrainStationListRequest request) {
        LambdaQueryWrapper<TrainStation> qw = buildQuery(request);

        return executePageQuery(request, qw);
    }

    @Override
    @Transactional
    public boolean adminAdd(TrainStationRequest request) {

        TrainStation trainStation = BeanUtil.copyProperties(request, TrainStation.class);
        return this.save(trainStation);
    }

    @Override
    @Transactional
    public boolean adminUpdate(TrainStationRequest request) {
        TrainStation trainStation = findTrainStation(request.getId());

        // 管理员可以修改所有字段
        BeanUtil.copyProperties(request, trainStation);
        return this.updateById(trainStation);
    }

    @Override
    @Transactional
    public boolean adminDelete(Long id) {
        return this.removeById(findTrainStation(id).getId());
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
    private LambdaQueryWrapper<TrainStation> buildQuery(TrainStationListRequest request) {
        LambdaQueryWrapper<TrainStation> qw = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getKeyword())) {
            String kw = request.getKeyword();
            qw.like(TrainStation::getTrainCode, kw);
        }

        qw.orderByDesc(TrainStation::getCreatedAt);
        return qw;
    }

    /**
     * 执行分页查询并转换结果
     */
    private PageResponse<TrainStationResponse> executePageQuery(TrainStationListRequest request,
                                                             LambdaQueryWrapper<TrainStation> qw) {
        Page<TrainStation> page = this.page(Page.of(request.getPage(), request.getSize()), qw);

        List<TrainStationResponse> list = BeanUtil.copyToList(page.getRecords(), TrainStationResponse.class);

        PageResponse<TrainStationResponse> response = new PageResponse<>();
        response.setTotal(page.getTotal());
        response.setList(list);
        return response;
    }

    /**
     * 统一查找实体并抛出异常
     */
    private TrainStation findTrainStation(Long id) {
        return this.getOptById(id)
                .orElseThrow(() -> new NoSuchElementException("火车车站不存在"));
    }
}
