package com.example.business.service.impl;

import com.example.business.entity.DailyTrainSeat;
import com.example.business.mapper.DailyTrainSeatMapper;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AfterConfirmOrderService {
    private static final Logger log = LoggerFactory.getLogger(AfterConfirmOrderService.class);

    @Resource
    DailyTrainSeatMapper dailyTrainSeatMapper;

    /**
     * 选中座位后的事务处理：
     *     更新座位表的新售卖情况 sell 字段值
     *     真实扣减库存，更新【余票信息】表的对应余票字段值
     *     保存到【车票记录】表
     *     更新【确认订单】表的订单状态=成功
     */
    @Transactional
    public void afterConfirm(List<DailyTrainSeat> finalSeatList){
        for (DailyTrainSeat dailyTrainSeat : finalSeatList) {
            DailyTrainSeat seatForUpdate = new DailyTrainSeat();
            seatForUpdate.setId(dailyTrainSeat.getId());
            seatForUpdate.setSell(dailyTrainSeat.getSell());
            dailyTrainSeatMapper.updateById(seatForUpdate);
            log.info("更新座位表的新售卖情况 sell 字段值 - 完成");
        }
    }
}
