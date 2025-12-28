package com.example.user.service.impl;

import com.example.common.core.UserContext;
import com.example.common.exception.CustomForbiddenException;
import cn.hutool.core.bean.BeanUtil;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.user.entity.Passenger;
import com.example.user.mapper.PassengerMapper;
import com.example.user.request.PassengerListRequest;
import com.example.user.request.PassengerRequest;
import com.example.common.response.PageResponse;
import com.example.user.response.PassengerResponse;
import com.example.user.service.PassengerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.example.common.exception.CustomValidationException;
import java.util.NoSuchElementException;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class PassengerServiceImpl extends ServiceImpl<PassengerMapper, Passenger> implements PassengerService {
    @Override
    @Transactional
    public boolean saveOrUpdate(PassengerRequest request) {
        Long currentUserId = UserContext.get();
        Passenger passenger;

        if (request.getId() != null) {
            // 更新逻辑
            passenger = findPassenger(request.getId());
            // 校验所有权
            if (!request.getUserId().equals(currentUserId)) {
                throw new CustomForbiddenException("无权操作此乘车人/乘客信息");
            }
            BeanUtil.copyProperties(request, passenger);
        } else {
            // 新增逻辑
            passenger = BeanUtil.copyProperties(request, Passenger.class);
            // 强制绑定当前用户
            passenger.setUserId(currentUserId);
        }

        return this.saveOrUpdate(passenger);
    }

    @Override
    @Transactional
    public boolean deletePassenger(Long id) {
        Passenger passenger = findPassenger(id);

        // 安全校验：只能删除自己的乘车人/乘客
        if (!passenger.getUserId().equals(UserContext.get())) {
            throw new CustomForbiddenException("无权删除此乘车人/乘客信息");
        }

        return this.removeById(id);
    }

    @Override
    public PageResponse<PassengerResponse> getPassengerPage(PassengerListRequest request) {
        LambdaQueryWrapper<Passenger> qw = buildQuery(request);
        // 强制限定搜索范围为当前用户 (水平越权防护)
        qw.eq(Passenger::getUserId, UserContext.get());

        return executePageQuery(request, qw);
    }

    @Override
    public PageResponse<PassengerResponse> getAdminPassengerPage(PassengerListRequest request) {
        LambdaQueryWrapper<Passenger> qw = buildQuery(request);

        // 管理员可选：按用户ID筛选
        if (request.getUserId() != null) {
            qw.eq(Passenger::getUserId, request.getUserId());
        }

        return executePageQuery(request, qw);
    }

    @Override
    @Transactional
    public boolean adminAdd(PassengerRequest request) {
        if (request.getUserId() == null) {
            throw new CustomValidationException("管理员添加乘车人/乘客必须指定 userId");
        }

        Passenger passenger = BeanUtil.copyProperties(request, Passenger.class);
        // 使用请求中的 userId（管理员可以指定任意用户）
        passenger.setUserId(request.getUserId());
        return this.save(passenger);
    }

    @Override
    @Transactional
    public boolean adminUpdate(PassengerRequest request) {
        Passenger passenger = findPassenger(request.getId());

        // 管理员可以修改所有字段，包括 userId（允许调整数据归属关系）
        BeanUtil.copyProperties(request, passenger);
        return this.updateById(passenger);
    }

    @Override
    @Transactional
    public boolean adminDelete(Long id) {
        return this.removeById(findPassenger(id).getId());
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
    private LambdaQueryWrapper<Passenger> buildQuery(PassengerListRequest request) {
        LambdaQueryWrapper<Passenger> qw = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getKeyword())) {
            String kw = request.getKeyword();
            qw.like(Passenger::getName, kw);
            qw.or().like(Passenger::getMobile, kw);
        }

        qw.orderByDesc(Passenger::getCreatedAt);
        return qw;
    }

    /**
     * 执行分页查询并转换结果
     */
    private PageResponse<PassengerResponse> executePageQuery(PassengerListRequest request,
                                                             LambdaQueryWrapper<Passenger> qw) {
        Page<Passenger> page = this.page(Page.of(request.getPage(), request.getSize()), qw);

        List<PassengerResponse> list = BeanUtil.copyToList(page.getRecords(), PassengerResponse.class);

        PageResponse<PassengerResponse> response = new PageResponse<>();
        response.setTotal(page.getTotal());
        response.setList(list);
        return response;
    }

    /**
     * 统一查找实体并抛出异常
     */
    private Passenger findPassenger(Long id) {
        return this.getOptById(id)
                .orElseThrow(() -> new NoSuchElementException("乘车人/乘客不存在"));
    }
}
