package com.example.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.business.entity.DailyTrainTicket;
import com.example.business.mapper.DailyTrainTicketMapper;
import com.example.business.request.DailyTrainTicketListRequest;
import com.example.business.request.DailyTrainTicketRequest;
import com.example.common.response.PageResponse;
import com.example.business.response.DailyTrainTicketResponse;
import com.example.business.service.DailyTrainTicketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.example.common.exception.CustomValidationException;
import java.util.NoSuchElementException;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class DailyTrainTicketServiceImpl extends ServiceImpl<DailyTrainTicketMapper, DailyTrainTicket> implements DailyTrainTicketService {
    @Override
    @Transactional
    public boolean saveOrUpdate(DailyTrainTicketRequest request) {
        DailyTrainTicket dailyTrainTicket;

        if (request.getId() != null) {
            // 更新逻辑
            dailyTrainTicket = findDailyTrainTicket(request.getId());
            BeanUtil.copyProperties(request, dailyTrainTicket);
        } else {
            // 新增逻辑
            dailyTrainTicket = BeanUtil.copyProperties(request, DailyTrainTicket.class);
        }

        return this.saveOrUpdate(dailyTrainTicket);
    }

    @Override
    @Transactional
    public boolean deleteDailyTrainTicket(Long id) {
        DailyTrainTicket dailyTrainTicket = findDailyTrainTicket(id);

        return this.removeById(id);
    }

    @Override
    public PageResponse<DailyTrainTicketResponse> getDailyTrainTicketPage(DailyTrainTicketListRequest request) {
        LambdaQueryWrapper<DailyTrainTicket> qw = buildQuery(request);

        return executePageQuery(request, qw);
    }

    @Override
    public PageResponse<DailyTrainTicketResponse> getAdminDailyTrainTicketPage(DailyTrainTicketListRequest request) {
        LambdaQueryWrapper<DailyTrainTicket> qw = buildQuery(request);

        return executePageQuery(request, qw);
    }

    @Override
    @Transactional
    public boolean adminAdd(DailyTrainTicketRequest request) {

        DailyTrainTicket dailyTrainTicket = BeanUtil.copyProperties(request, DailyTrainTicket.class);
        return this.save(dailyTrainTicket);
    }

    @Override
    @Transactional
    public boolean adminUpdate(DailyTrainTicketRequest request) {
        DailyTrainTicket dailyTrainTicket = findDailyTrainTicket(request.getId());

        // 管理员可以修改所有字段
        BeanUtil.copyProperties(request, dailyTrainTicket);
        return this.updateById(dailyTrainTicket);
    }

    @Override
    @Transactional
    public boolean adminDelete(Long id) {
        return this.removeById(findDailyTrainTicket(id).getId());
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
    private LambdaQueryWrapper<DailyTrainTicket> buildQuery(DailyTrainTicketListRequest request) {
        LambdaQueryWrapper<DailyTrainTicket> qw = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getKeyword())) {
            String kw = request.getKeyword();
            qw.like(DailyTrainTicket::getTrainCode, kw);
            qw.or().like(DailyTrainTicket::getStart, kw);
            qw.or().like(DailyTrainTicket::getStartPinyin, kw);
            qw.or().like(DailyTrainTicket::getEnd, kw);
            qw.or().like(DailyTrainTicket::getEndPinyin, kw);
        }

        qw.orderByDesc(DailyTrainTicket::getCreatedAt);
        return qw;
    }

    /**
     * 执行分页查询并转换结果
     */
    private PageResponse<DailyTrainTicketResponse> executePageQuery(DailyTrainTicketListRequest request,
                                                             LambdaQueryWrapper<DailyTrainTicket> qw) {
        Page<DailyTrainTicket> page = this.page(Page.of(request.getPage(), request.getSize()), qw);

        List<DailyTrainTicketResponse> list = BeanUtil.copyToList(page.getRecords(), DailyTrainTicketResponse.class);

        PageResponse<DailyTrainTicketResponse> response = new PageResponse<>();
        response.setTotal(page.getTotal());
        response.setList(list);
        return response;
    }

    /**
     * 统一查找实体并抛出异常
     */
    private DailyTrainTicket findDailyTrainTicket(Long id) {
        return this.getOptById(id)
                .orElseThrow(() -> new NoSuchElementException("余票信息不存在"));
    }
}
