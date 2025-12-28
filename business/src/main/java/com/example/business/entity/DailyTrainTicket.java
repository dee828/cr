package com.example.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.math.BigDecimal;

@Setter
@Getter
public class DailyTrainTicket extends BaseEntity {
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
     * 出发站拼音
     */
    private String startPinyin;

    /**
     * 出发时间
     */
    private LocalTime startTime;

    /**
     * 出发站序
     */
    private Integer startIndex;

    /**
     * 到达站
     */
    @TableField("`end`")
    private String end;

    /**
     * 到达站拼音
     */
    private String endPinyin;

    /**
     * 到站时间
     */
    private LocalTime endTime;

    /**
     * 到站站序
     */
    private Integer endIndex;

    /**
     * 一等座余票
     */
    private Integer ydz;

    /**
     * 一等座票价
     */
    private BigDecimal ydzPrice;

    /**
     * 二等座余票
     */
    private Integer edz;

    /**
     * 二等座票价
     */
    private BigDecimal edzPrice;

}