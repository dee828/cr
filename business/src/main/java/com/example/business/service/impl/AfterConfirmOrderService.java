package com.example.business.service.impl;

import com.example.business.entity.ConfirmOrder;
import com.example.business.entity.DailyTrainSeat;
import com.example.business.entity.DailyTrainTicket;
import com.example.business.enums.ConfirmOrderStatus;
import com.example.business.feign.UserFeign;
import com.example.business.mapper.ConfirmOrderMapper;
import com.example.business.mapper.DailyTrainSeatMapper;
import com.example.business.mapper.custom.DailyTrainTicketMapperCustom;
import com.example.business.request.ConfirmOrderTicketRequest;
import com.example.common.core.UserContext;
import com.example.common.request.TicketRequest;
import com.example.common.response.R;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AfterConfirmOrderService {
    private static final Logger log = LoggerFactory.getLogger(AfterConfirmOrderService.class);

    @Resource
    DailyTrainSeatMapper dailyTrainSeatMapper;

    @Autowired
    DailyTrainTicketMapperCustom dailyTrainTicketMapperCustom;

    @Resource
    private UserFeign userFeign;

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    /**
     * 选中座位后的事务处理：
     *     更新座位表的新售卖情况 sell 字段值
     *     真实扣减库存，更新【余票信息】表的对应余票字段值
     *     保存到【车票记录】表
     *     更新【确认订单】表的订单状态=成功
     */
    @Transactional
    public void afterConfirm(List<DailyTrainSeat> finalSeatList, DailyTrainTicket dailyTrainTicket, List<ConfirmOrderTicketRequest> tickets, ConfirmOrder confirmOrder){
        for (int j = 0; j < finalSeatList.size(); j++) {
            DailyTrainSeat dailyTrainSeat = finalSeatList.get(j);
            DailyTrainSeat seatForUpdate = new DailyTrainSeat();
            seatForUpdate.setId(dailyTrainSeat.getId());
            seatForUpdate.setSell(dailyTrainSeat.getSell());
            dailyTrainSeatMapper.updateById(seatForUpdate);
            log.info("更新座位表的新售卖情况 sell 字段值 - 完成");

            Integer startIndex = dailyTrainTicket.getStartIndex();
            Integer endIndex = dailyTrainTicket.getEndIndex();
            char[] chars = seatForUpdate.getSell().toCharArray();
            Integer maxStartIndex = endIndex - 1;
            Integer minEndIndex = startIndex + 1;
            int minStartIndex = 0;
            for (int i = startIndex - 1; i >= 0; i--) {
                char aChar = chars[i];
                if (aChar == '1') {
                    minStartIndex = i + 1;
                    break;
                }
            }
            log.info("影响出发站区间：最小出发站index={} ~ 最大出发站index={}", minStartIndex, maxStartIndex);

            int maxEndIndex = seatForUpdate.getSell().length();
            for (int i = endIndex; i < seatForUpdate.getSell().length(); i++) {
                char aChar = chars[i];
                if (aChar == '1') {
                    maxEndIndex = i;
                    break;
                }
            }
            log.info("影响到达站区间：最小到达站index={} ~ 最大到达站index={}", minEndIndex, maxEndIndex);

            dailyTrainTicketMapperCustom.updateCountBySell(
                    dailyTrainSeat.getDate(),
                    dailyTrainSeat.getTrainCode(),
                    dailyTrainSeat.getSeatType(),
                    minStartIndex,
                    maxStartIndex,
                    minEndIndex,
                    maxEndIndex);
            log.info("真实扣减库存，更新【余票信息】表的对应余票字段值 - 完成");

            // 调用会员服务接口，为会员增加车票购买记录
            TicketRequest userTicketRequest = new TicketRequest();
            userTicketRequest.setUserId(UserContext.get());
            userTicketRequest.setPassengerId(tickets.get(j).getPassengerId());
            userTicketRequest.setPassengerName(tickets.get(j).getPassengerName());
            userTicketRequest.setTrainDate(dailyTrainTicket.getDate());
            userTicketRequest.setTrainCode(dailyTrainTicket.getTrainCode());
            userTicketRequest.setCarriageIndex(dailyTrainSeat.getCarriageIndex());
            userTicketRequest.setSeatRow(dailyTrainSeat.getRow());
            userTicketRequest.setSeatCol(dailyTrainSeat.getCol());
            userTicketRequest.setStartStation(dailyTrainTicket.getStart());
            userTicketRequest.setStartTime(dailyTrainTicket.getStartTime());
            userTicketRequest.setEndStation(dailyTrainTicket.getEnd());
            userTicketRequest.setEndTime(dailyTrainTicket.getEndTime());
            userTicketRequest.setSeatType(dailyTrainSeat.getSeatType());
            R<Boolean> responseFromUserModule = userFeign.saveByFeign(userTicketRequest);
            log.info("跨服务调用 user 模块 saveByFeign 接口，返回={} - 完成", responseFromUserModule);

            // 更新【确认订单】表的订单状态=成功
            ConfirmOrder confirmOrderForUpdate = new ConfirmOrder();
            confirmOrderForUpdate.setId(confirmOrder.getId());
            confirmOrderForUpdate.setStatus(ConfirmOrderStatus.SUCCESS.getCode());
            confirmOrderMapper.updateById(confirmOrderForUpdate);
            log.info("更新【确认订单】表的订单状态=成功 - 完成");
        }
    }
}
