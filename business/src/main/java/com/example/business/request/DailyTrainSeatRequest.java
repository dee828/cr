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
public class DailyTrainSeatRequest {

    private Long id;

    @NotNull(message = "日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate date;

    @NotBlank(message = "车次编号不能为空")
    @Size(max = 20)
    private String trainCode;

    @NotNull(message = "厢序不能为空")
    private Integer carriageIndex;

    @NotBlank(message = "排号不能为空")
    private String row;

    @NotBlank(message = "座位类型不能为空")
    private String seatType;

    @NotBlank(message = "列号不能为空")
    private String col;

    @NotNull(message = "同车厢座序不能为空")
    private Integer carriageSeatIndex;

    @NotBlank(message = "售卖情况不能为空")
    @Size(max = 90)
    private String sell;

}
