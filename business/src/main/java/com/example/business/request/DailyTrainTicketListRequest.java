package com.example.business.request;

import com.example.common.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class DailyTrainTicketListRequest extends PageRequest {
    @Schema(description = "搜索关键词", example = "\"\"")
    private String keyword;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DailyTrainTicketListRequest that)) return false;
        return Objects.equals(getKeyword(), that.getKeyword()) && Objects.equals(getPage(), that.getPage()) && Objects.equals(getSize(), that.getSize());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKeyword(), getPage(), getSize());
    }
}
