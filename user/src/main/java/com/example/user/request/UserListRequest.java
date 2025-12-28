package com.example.user.request;

import com.example.common.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserListRequest extends PageRequest {
    // 支持搜索的字段：id、email、name、mobile
    @Schema(description = "搜索关键词")
    private String keyword;
}
