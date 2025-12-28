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
import java.time.LocalTime;
import java.time.LocalDate;
import java.math.BigDecimal;

@Getter
@Setter
public class DailyTrainTicketRequest {

    private Long id;

    @NotNull(message = "日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate date;

    @NotBlank(message = "车次编号不能为空")
    @Size(max = 20)
    private String trainCode;

    @NotBlank(message = "出发站不能为空")
    @Size(max = 20)
    private String start;

    @NotBlank(message = "出发站拼音不能为空")
    @Size(max = 50)
    private String startPinyin;

    @NotNull(message = "出发时间不能为空")
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private LocalTime startTime;

    @NotNull(message = "出发站序不能为空")
    private Integer startIndex;

    @NotBlank(message = "到达站不能为空")
    @Size(max = 20)
    private String end;

    @NotBlank(message = "到达站拼音不能为空")
    @Size(max = 50)
    private String endPinyin;

    @NotNull(message = "到站时间不能为空")
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private LocalTime endTime;

    @NotNull(message = "到站站序不能为空")
    private Integer endIndex;

    @NotNull(message = "一等座余票不能为空")
    private Integer ydz;

    @NotNull(message = "一等座票价不能为空")
    private BigDecimal ydzPrice;

    @NotNull(message = "二等座余票不能为空")
    private Integer edz;

    @NotNull(message = "二等座票价不能为空")
    private BigDecimal edzPrice;

}
