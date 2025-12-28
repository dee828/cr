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
public class DailyTrainStationRequest {

    private Long id;

    @NotNull(message = "日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate date;

    @NotBlank(message = "车次编号不能为空")
    @Size(max = 20)
    private String trainCode;

    @NotNull(message = "站序不能为空")
    private Integer index;

    @NotBlank(message = "站名不能为空")
    @Size(max = 20)
    private String name;

    @NotBlank(message = "站名拼音不能为空")
    @Size(max = 50)
    private String namePinyin;

    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private LocalTime inTime;

    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private LocalTime outTime;

    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private LocalTime stopTime;

    @NotNull(message = "里程 km不能为空")
    private BigDecimal km;

}
