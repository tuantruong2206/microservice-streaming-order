package vn.microservice.streaming.order.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeTypeUtils;
import vn.microservice.streaming.common.lib.dto.OrderStreamDTO;
import vn.microservice.streaming.order.dto.OrderDTO;
import vn.microservice.streaming.order.dto.UserOrderPer3MinDTO;
import vn.microservice.streaming.order.service.OrderService;
import vn.microservice.streaming.order.streams.OrderStream;

/**
 * @author Tuan.Truong [Brian]
 * @version 1.0
 * @date 12/30/2020 10:45 AM
 */
public class OrderServiceImpl implements OrderService {

    private final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderStream orderStream;

    public OrderServiceImpl(OrderStream orderStream) {
        this.orderStream = orderStream;
    }

    @Override
    public String createOrder(OrderDTO orderDTO) {
        log.info("Create order DTO {}", orderDTO);
        //TODO need to use mapper for better look of coding
        OrderStreamDTO orderStreamDTO = new OrderStreamDTO(orderDTO.getUserid(), orderDTO.getOrderId(), orderDTO.getTicker(), orderDTO.getQuality(), orderDTO.getAmount(), orderDTO.getStatus(), orderDTO.getCreatedAt(), orderDTO.getUpdatedAt());
        MessageChannel messageChannel = this.orderStream.outboundOrder();
        messageChannel.send(MessageBuilder.withPayload(orderStreamDTO).setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE).build());
        return "Success";
    }
}
