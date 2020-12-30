package vn.microservice.streaming.order.service;

import vn.microservice.streaming.order.dto.OrderDTO;

/**
 * @author Tuan.Truong [Brian]
 * @version 1.0
 * Class INFO
 * @date 12/30/2020 10:44 AM
 * This class helps to work on order business logic
 */
public interface OrderService {

    public String createOrder(OrderDTO orderDTO);

}
