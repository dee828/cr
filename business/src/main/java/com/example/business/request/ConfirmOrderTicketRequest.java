package com.example.business.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ConfirmOrderTicketRequest {
    @NotNull(message = "【乘客ID】不能为空")
    private Long passengerId;

    @NotBlank(message = "【乘客类型】不能为空")
    private String passengerType;

    @NotBlank(message = "【乘客名称】不能为空")
    private String passengerName;

    @NotBlank(message = "【乘客身份证】不能为空")
    private String passengerIdCard;

    @NotBlank(message = "【座位类型code】不能为空")
    private String seatTypeCode;

    /**
     * 选座。允许空值，值示例：A1
     */
    private String seat;
}
