package com.example.user.controller.admin;

import com.example.common.response.R;
import com.example.user.request.ProductListRequest;
import com.example.common.response.PageResponse;
import com.example.user.response.ProductResponse;
import com.example.user.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin/product")
public class ProductAdminController {
    @Autowired
    private ProductService productService;

    @GetMapping("list")
    public R<PageResponse<ProductResponse>> list(@Valid ProductListRequest request){
        return R.ok(productService.getProductPage(request));
    }

    @GetMapping("{id}")
    public R<ProductResponse> show(@PathVariable Long id){
        return R.ok(productService.getProductById(id));
        }
}
