package com.example.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.user.entity.Product;
import com.example.user.mapper.ProductMapper;
import com.example.user.request.ProductListRequest;
import com.example.common.response.PageResponse;
import com.example.user.response.ProductResponse;
import com.example.user.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    @Override
    public PageResponse<ProductResponse> getProductPage(ProductListRequest request) {
        // 分页
        Page<Product> productPage = Page.of(request.getPage(), request.getSize());

        // 查询
        QueryWrapper<Product> qw = null;
        // 如果用户有输入搜索关键词，拼接模糊查询的sql语句
        if(StringUtils.hasText(request.getKeyword())){
            qw = new QueryWrapper<>();
            qw.or().like("id", request.getKeyword());
            qw.or().like("name", request.getKeyword());
        }

        Page<Product> productPage1 = this.page(productPage, qw);

        List<Product> list = productPage1.getRecords();
        List<ProductResponse> productResponses = BeanUtil.copyToList(list, ProductResponse.class);

        PageResponse<ProductResponse> response = new PageResponse<>();
        response.setTotal(productPage1.getTotal());
        response.setList(productResponses);

        return response;
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = this.getOptById(id).orElseThrow();

        ProductResponse productResponse = BeanUtil.copyProperties(product, ProductResponse.class);

        return productResponse;
    }
}
