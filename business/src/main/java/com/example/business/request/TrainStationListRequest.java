package com.example.business.request;

import com.example.common.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainStationListRequest extends PageRequest {
    @Schema(description = "搜索关键词", example = "\"\"")
    private String keyword;
}
