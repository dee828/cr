package com.example.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Setter
@Getter
public class ConfirmOrder extends BaseEntity {
    /**
     * 会员ID
     */
    private Long userId;

    /**
     * 日期
     */
    @TableField("`date`")
    private LocalDate date;

    /**
     * 车次编号
     */
    private String trainCode;

    /**
     * 出发站
     */
    private String start;

    /**
     * 到达站
     */
    @TableField("`end`")
    private String end;

    /**
     * 余票ID
     */
    private Long dailyTrainTicketId;

    /**
     * 车票
     */
    private String tickets;

    /**
     * 订单状态
     */
    @TableField("`status`")
    private String status;

}