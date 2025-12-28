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
public class DailyTrainStation extends BaseEntity {
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
     * 站序
     */
    @TableField("`index`")
    private Integer index;

    /**
     * 站名
     */
    private String name;

    /**
     * 站名拼音
     */
    private String namePinyin;

    /**
     * 进站时间
     */
    private LocalTime inTime;

    /**
     * 出站时间
     */
    private LocalTime outTime;

    /**
     * 停站时长
     */
    private LocalTime stopTime;

    /**
     * 里程 km
     */
    private BigDecimal km;

}