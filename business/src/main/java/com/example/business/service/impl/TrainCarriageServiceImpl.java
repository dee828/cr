package com.example.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.business.entity.TrainCarriage;
import com.example.business.mapper.TrainCarriageMapper;
import com.example.business.request.TrainCarriageListRequest;
import com.example.business.request.TrainCarriageRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.TrainCarriageResponse;
import com.example.business.service.TrainCarriageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.example.common.exception.CustomValidationException;
import java.util.NoSuchElementException;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class TrainCarriageServiceImpl extends ServiceImpl<TrainCarriageMapper, TrainCarriage> implements TrainCarriageService {
    @Override
    @Transactional
    public boolean saveOrUpdate(TrainCarriageRequest request) {
        TrainCarriage trainCarriage;

        if (request.getId() != null) {
            // 更新逻辑
            trainCarriage = findTrainCarriage(request.getId());
            BeanUtil.copyProperties(request, trainCarriage);
        } else {
            // 新增逻辑
            trainCarriage = BeanUtil.copyProperties(request, TrainCarriage.class);
        }

        return this.saveOrUpdate(trainCarriage);
    }

    @Override
    @Transactional
    public boolean deleteTrainCarriage(Long id) {
        TrainCarriage trainCarriage = findTrainCarriage(id);

        return this.removeById(id);
    }

    @Override
    public PageResponse<TrainCarriageResponse> getTrainCarriagePage(TrainCarriageListRequest request) {
        LambdaQueryWrapper<TrainCarriage> qw = buildQuery(request);

        return executePageQuery(request, qw);
    }

    @Override
    public PageResponse<TrainCarriageResponse> getAdminTrainCarriagePage(TrainCarriageListRequest request) {
        LambdaQueryWrapper<TrainCarriage> qw = buildQuery(request);

        return executePageQuery(request, qw);
    }

    @Override
    @Transactional
    public boolean adminAdd(TrainCarriageRequest request) {

        TrainCarriage trainCarriage = BeanUtil.copyProperties(request, TrainCarriage.class);
        return this.save(trainCarriage);
    }

    @Override
    @Transactional
    public boolean adminUpdate(TrainCarriageRequest request) {
        TrainCarriage trainCarriage = findTrainCarriage(request.getId());

        // 管理员可以修改所有字段
        BeanUtil.copyProperties(request, trainCarriage);
        return this.updateById(trainCarriage);
    }

    @Override
    @Transactional
    public boolean adminDelete(Long id) {
        return this.removeById(findTrainCarriage(id).getId());
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
    private LambdaQueryWrapper<TrainCarriage> buildQuery(TrainCarriageListRequest request) {
        LambdaQueryWrapper<TrainCarriage> qw = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getKeyword())) {
            String kw = request.getKeyword();
            qw.like(TrainCarriage::getTrainCode, kw);
        }

        qw.orderByDesc(TrainCarriage::getCreatedAt);
        return qw;
    }

    /**
     * 执行分页查询并转换结果
     */
    private PageResponse<TrainCarriageResponse> executePageQuery(TrainCarriageListRequest request,
                                                             LambdaQueryWrapper<TrainCarriage> qw) {
        Page<TrainCarriage> page = this.page(Page.of(request.getPage(), request.getSize()), qw);

        List<TrainCarriageResponse> list = BeanUtil.copyToList(page.getRecords(), TrainCarriageResponse.class);

        PageResponse<TrainCarriageResponse> response = new PageResponse<>();
        response.setTotal(page.getTotal());
        response.setList(list);
        return response;
    }

    /**
     * 统一查找实体并抛出异常
     */
    private TrainCarriage findTrainCarriage(Long id) {
        return this.getOptById(id)
                .orElseThrow(() -> new NoSuchElementException("火车车厢不存在"));
    }
}
