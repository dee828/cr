package com.example.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;

@Setter
@Getter
public class DailyTrain extends BaseEntity {
    /**
     * 日期
     */
    @TableField("`date`")
    private LocalDate date;

    /**
     * 车次编号
     */
    private String code;

    /**
     * 车次类型
     */
    @TableField("`type`")
    private String type;

    /**
     * 始发站
     */
    private String start;

    /**
     * 始发站拼音
     */
    private String startPinyin;

    /**
     * 出发时间
     */
    private LocalTime startTime;

    /**
     * 终点站
     */
    @TableField("`end`")
    private String end;

    /**
     * 终点站拼音
     */
    private String endPinyin;

    /**
     * 到站时间
     */
    private LocalTime endTime;

}