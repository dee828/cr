package com.example.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class TrainSeat extends BaseEntity {
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

}