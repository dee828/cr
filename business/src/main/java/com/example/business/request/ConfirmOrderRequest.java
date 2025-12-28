package com.example.business.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
public class ConfirmOrderRequest {

    private Long id;

    @NotNull(message = "会员ID不能为空")
    private Long userId;

    @NotNull(message = "日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate date;

    @NotBlank(message = "车次编号不能为空")
    @Size(max = 20)
    private String trainCode;

    @NotBlank(message = "出发站不能为空")
    @Size(max = 20)
    private String start;

    @NotBlank(message = "到达站不能为空")
    @Size(max = 20)
    private String end;

    @NotNull(message = "余票ID不能为空")
    private Long dailyTrainTicketId;

    @NotEmpty(message = "车票不能为空")
    private List<ConfirmOrderTicketRequest> tickets;

    @NotBlank(message = "订单状态不能为空")
    private String status;

}
