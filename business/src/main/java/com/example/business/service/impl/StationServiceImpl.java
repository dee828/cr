package com.example.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.business.entity.Station;
import com.example.business.mapper.StationMapper;
import com.example.business.request.StationListRequest;
import com.example.business.request.StationRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.StationResponse;
import com.example.business.service.StationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.example.common.exception.CustomValidationException;
import java.util.NoSuchElementException;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class StationServiceImpl extends ServiceImpl<StationMapper, Station> implements StationService {
    @Override
    @Transactional
    public boolean saveOrUpdate(StationRequest request) {
        Station station;

        if (request.getId() != null) {
            // 更新逻辑
            station = findStation(request.getId());
            BeanUtil.copyProperties(request, station);
        } else {
            // 新增逻辑
            station = BeanUtil.copyProperties(request, Station.class);
        }

        return this.saveOrUpdate(station);
    }

    @Override
    @Transactional
    public boolean deleteStation(Long id) {
        Station station = findStation(id);

        return this.removeById(id);
    }

    @Override
    public PageResponse<StationResponse> getStationPage(StationListRequest request) {
        LambdaQueryWrapper<Station> qw = buildQuery(request);

        return executePageQuery(request, qw);
    }

    @Override
    public PageResponse<StationResponse> getAdminStationPage(StationListRequest request) {
        LambdaQueryWrapper<Station> qw = buildQuery(request);

        return executePageQuery(request, qw);
    }

    @Override
    @Transactional
    public boolean adminAdd(StationRequest request) {

        Station station = BeanUtil.copyProperties(request, Station.class);
        return this.save(station);
    }

    @Override
    @Transactional
    public boolean adminUpdate(StationRequest request) {
        Station station = findStation(request.getId());

        // 管理员可以修改所有字段
        BeanUtil.copyProperties(request, station);
        return this.updateById(station);
    }

    @Override
    @Transactional
    public boolean adminDelete(Long id) {
        return this.removeById(findStation(id).getId());
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
    private LambdaQueryWrapper<Station> buildQuery(StationListRequest request) {
        LambdaQueryWrapper<Station> qw = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getKeyword())) {
            String kw = request.getKeyword();
            qw.like(Station::getName, kw);
            qw.or().like(Station::getNamePinyin, kw);
            qw.or().like(Station::getNamePy, kw);
        }

        qw.orderByDesc(Station::getCreatedAt);
        return qw;
    }

    /**
     * 执行分页查询并转换结果
     */
    private PageResponse<StationResponse> executePageQuery(StationListRequest request,
                                                             LambdaQueryWrapper<Station> qw) {
        Page<Station> page = this.page(Page.of(request.getPage(), request.getSize()), qw);

        List<StationResponse> list = BeanUtil.copyToList(page.getRecords(), StationResponse.class);

        PageResponse<StationResponse> response = new PageResponse<>();
        response.setTotal(page.getTotal());
        response.setList(list);
        return response;
    }

    /**
     * 统一查找实体并抛出异常
     */
    private Station findStation(Long id) {
        return this.getOptById(id)
                .orElseThrow(() -> new NoSuchElementException("车站不存在"));
    }
}
