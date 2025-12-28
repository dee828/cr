package com.example.business.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.math.BigDecimal;

@Getter
@Setter
public class TrainStationResponse {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String trainCode;

    private Integer index;

    private String name;

    private String namePinyin;

    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private LocalTime inTime;

    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private LocalTime outTime;

    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private LocalTime stopTime;

    private BigDecimal km;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

}
