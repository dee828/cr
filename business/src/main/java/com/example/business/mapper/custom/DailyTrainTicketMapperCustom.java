package com.example.business.mapper.custom;

import java.time.LocalDate;

public interface DailyTrainTicketMapperCustom {
    void updateCountBySell(LocalDate date, String trainCode, String seatTypeCode, Integer minStartIndex, Integer maxStartIndex, Integer minEndIndex, Integer maxEndIndex);
}
