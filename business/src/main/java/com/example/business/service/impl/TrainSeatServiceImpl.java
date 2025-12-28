package com.example.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.business.entity.TrainSeat;
import com.example.business.mapper.TrainSeatMapper;
import com.example.business.request.TrainSeatListRequest;
import com.example.business.request.TrainSeatRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.TrainSeatResponse;
import com.example.business.service.TrainSeatService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.example.common.exception.CustomValidationException;
import java.util.NoSuchElementException;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class TrainSeatServiceImpl extends ServiceImpl<TrainSeatMapper, TrainSeat> implements TrainSeatService {
    @Override
    @Transactional
    public boolean saveOrUpdate(TrainSeatRequest request) {
        TrainSeat trainSeat;

        if (request.getId() != null) {
            // 更新逻辑
            trainSeat = findTrainSeat(request.getId());
            BeanUtil.copyProperties(request, trainSeat);
        } else {
            // 新增逻辑
            trainSeat = BeanUtil.copyProperties(request, TrainSeat.class);
        }

        return this.saveOrUpdate(trainSeat);
    }

    @Override
    @Transactional
    public boolean deleteTrainSeat(Long id) {
        TrainSeat trainSeat = findTrainSeat(id);

        return this.removeById(id);
    }

    @Override
    public PageResponse<TrainSeatResponse> getTrainSeatPage(TrainSeatListRequest request) {
        LambdaQueryWrapper<TrainSeat> qw = buildQuery(request);

        return executePageQuery(request, qw);
    }

    @Override
    public PageResponse<TrainSeatResponse> getAdminTrainSeatPage(TrainSeatListRequest request) {
        LambdaQueryWrapper<TrainSeat> qw = buildQuery(request);

        return executePageQuery(request, qw);
    }

    @Override
    @Transactional
    public boolean adminAdd(TrainSeatRequest request) {

        TrainSeat trainSeat = BeanUtil.copyProperties(request, TrainSeat.class);
        return this.save(trainSeat);
    }

    @Override
    @Transactional
    public boolean adminUpdate(TrainSeatRequest request) {
        TrainSeat trainSeat = findTrainSeat(request.getId());

        // 管理员可以修改所有字段
        BeanUtil.copyProperties(request, trainSeat);
        return this.updateById(trainSeat);
    }

    @Override
    @Transactional
    public boolean adminDelete(Long id) {
        return this.removeById(findTrainSeat(id).getId());
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
    private LambdaQueryWrapper<TrainSeat> buildQuery(TrainSeatListRequest request) {
        LambdaQueryWrapper<TrainSeat> qw = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getKeyword())) {
            String kw = request.getKeyword();
            qw.like(TrainSeat::getTrainCode, kw);
        }

        qw.orderByDesc(TrainSeat::getCreatedAt);
        return qw;
    }

    /**
     * 执行分页查询并转换结果
     */
    private PageResponse<TrainSeatResponse> executePageQuery(TrainSeatListRequest request,
                                                             LambdaQueryWrapper<TrainSeat> qw) {
        Page<TrainSeat> page = this.page(Page.of(request.getPage(), request.getSize()), qw);

        List<TrainSeatResponse> list = BeanUtil.copyToList(page.getRecords(), TrainSeatResponse.class);

        PageResponse<TrainSeatResponse> response = new PageResponse<>();
        response.setTotal(page.getTotal());
        response.setList(list);
        return response;
    }

    /**
     * 统一查找实体并抛出异常
     */
    private TrainSeat findTrainSeat(Long id) {
        return this.getOptById(id)
                .orElseThrow(() -> new NoSuchElementException("车厢座位不存在"));
    }
}
