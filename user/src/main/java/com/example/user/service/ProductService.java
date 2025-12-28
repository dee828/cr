package com.example.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.user.entity.Product;
import com.example.user.request.ProductListRequest;
import com.example.common.response.PageResponse;
import com.example.user.response.ProductResponse;

public interface ProductService extends IService<Product> {

    PageResponse<ProductResponse> getProductPage(ProductListRequest request);

    ProductResponse getProductById(Long id);
}