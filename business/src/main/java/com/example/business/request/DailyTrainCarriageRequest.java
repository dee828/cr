package com.example.business.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Getter
@Setter
public class DailyTrainCarriageRequest {

    private Long id;

    @NotNull(message = "日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate date;

    @NotBlank(message = "车次编号不能为空")
    @Size(max = 20)
    private String trainCode;

    @NotNull(message = "厢号不能为空")
    private Integer index;

    @NotBlank(message = "座位类型不能为空")
    private String seatType;

    @NotNull(message = "座位数不能为空")
    private Integer seatCount;

    @NotNull(message = "排数不能为空")
    private Integer rowCount;

    @NotNull(message = "列数不能为空")
    private Integer colCount;

}
