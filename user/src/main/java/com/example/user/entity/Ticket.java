package com.example.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;

@Setter
@Getter
public class Ticket extends BaseEntity {
    /**
     * 会员ID
     */
    private Long userId;

    /**
     * 乘客ID
     */
    private Long passengerId;

    /**
     * 乘客姓名
     */
    private String passengerName;

    /**
     * 日期
     */
    private LocalDate trainDate;

    /**
     * 车次编号
     */
    private String trainCode;

    /**
     * 厢序
     */
    private Integer carriageIndex;

    /**
     * 排号
     */
    private String seatRow;

    /**
     * 列号
     */
    private String seatCol;

    /**
     * 出发站
     */
    private String startStation;

    /**
     * 出发时间
     */
    private LocalTime startTime;

    /**
     * 到达站
     */
    private String endStation;

    /**
     * 到站时间
     */
    private LocalTime endTime;

    /**
     * 座位类型
     */
    private String seatType;

}