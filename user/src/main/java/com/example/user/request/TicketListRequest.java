package com.example.user.request;

import com.example.common.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketListRequest extends PageRequest {
    @Schema(description = "用户ID (仅管理员端有效)")
    private Long userId;

    @Schema(description = "搜索关键词", example = "\"\"")
    private String keyword;
}
