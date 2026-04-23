package com.example.common.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;

@Getter
@Setter
public class TicketRequest {

    private Long id;

    @NotNull(message = "会员ID不能为空")
    private Long userId;

    @NotNull(message = "乘客ID不能为空")
    private Long passengerId;

    private String passengerName;

    @NotNull(message = "日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate trainDate;

    @NotBlank(message = "车次编号不能为空")
    @Size(max = 20)
    private String trainCode;

    @NotNull(message = "厢序不能为空")
    private Integer carriageIndex;

    @NotBlank(message = "排号不能为空")
    private String seatRow;

    @NotBlank(message = "列号不能为空")
    private String seatCol;

    @NotBlank(message = "出发站不能为空")
    @Size(max = 20)
    private String startStation;

    @NotNull(message = "出发时间不能为空")
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private LocalTime startTime;

    @NotBlank(message = "到达站不能为空")
    @Size(max = 20)
    private String endStation;

    @NotNull(message = "到站时间不能为空")
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private LocalTime endTime;

    @NotBlank(message = "座位类型不能为空")
    private String seatType;

}
