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

@Getter
@Setter
public class DailyTrainRequest {

    private Long id;

    @NotNull(message = "日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate date;

    @NotBlank(message = "车次编号不能为空")
    @Size(max = 20)
    private String code;

    @NotBlank(message = "车次类型不能为空")
    private String type;

    @NotBlank(message = "始发站不能为空")
    @Size(max = 20)
    private String start;

    @NotBlank(message = "始发站拼音不能为空")
    @Size(max = 50)
    private String startPinyin;

    @NotNull(message = "出发时间不能为空")
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private LocalTime startTime;

    @NotBlank(message = "终点站不能为空")
    @Size(max = 20)
    private String end;

    @NotBlank(message = "终点站拼音不能为空")
    @Size(max = 50)
    private String endPinyin;

    @NotNull(message = "到站时间不能为空")
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private LocalTime endTime;

}
