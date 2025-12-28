package com.example.business.enums;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum SeatCol {
    YDZ_A("A", "A", "1"),
    YDZ_C("C", "C", "1"),
    YDZ_D("D", "D", "1"),
    YDZ_F("F", "F", "1"),
    EDZ_A("A", "A", "2"),
    EDZ_B("B", "B", "2"),
    EDZ_C("C", "C", "2"),
    EDZ_D("D", "D", "2"),
    EDZ_F("F", "F", "2");

    private String code;

    private String desc;

    /**
     * 对应 SeatType 的 code 值
     */
    private String type;

    SeatCol(String code, String desc, String type) {
        this.code = code;
        this.desc = desc;
        this.type = type;
    }

    /**
     * 根据车厢座位类型，筛选出所有的列
     * 例子：车厢类型是一等座，则筛选出 columnList = {ACDF}
     */
    public static List<SeatCol> getColsByType(String seatType) {
        List<SeatCol> colList = new ArrayList<>();
        EnumSet<SeatCol> seatColEnums = EnumSet.allOf(SeatCol.class);
        for (SeatCol anEnum : seatColEnums) {
            if (seatType.equals(anEnum.getType())) {
                colList.add(anEnum);
            }
        }
        return colList;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public String getType() {
        return type;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setType(String type) {
        this.type = type;
    }
}