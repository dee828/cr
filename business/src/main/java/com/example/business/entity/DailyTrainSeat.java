package com.example.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Setter
@Getter
public class DailyTrainSeat extends BaseEntity {
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
     * 厢序
     */
    private Integer carriageIndex;

    /**
     * 排号
     */
    @TableField("`row`")
    private String row;

    /**
     * 座位类型
     */
    private String seatType;

    /**
     * 列号
     */
    private String col;

    /**
     * 同车厢座序
     */
    private Integer carriageSeatIndex;

    /**
     * 售卖情况
     */
    private String sell;

}