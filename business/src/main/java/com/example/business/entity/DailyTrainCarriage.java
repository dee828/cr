package com.example.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Setter
@Getter
public class DailyTrainCarriage extends BaseEntity {
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
     * 厢号
     */
    @TableField("`index`")
    private Integer index;

    /**
     * 座位类型
     */
    private String seatType;

    /**
     * 座位数
     */
    private Integer seatCount;

    /**
     * 排数
     */
    private Integer rowCount;

    /**
     * 列数
     */
    private Integer colCount;

}