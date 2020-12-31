package vn.microservice.streaming.order.controller.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.microservice.streaming.order.dto.OrderDTO;
import vn.microservice.streaming.order.service.OrderService;

/**
 * @author Tuan.Truong [Brian]
 * @version 1.0
 * @date 12/30/2020 10:39 AM
 *
 *
 * Order Controller class: populate order rest API
 *
 *
 */
@RestController
@RequestMapping(path = "basket", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {
    private final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final InteractiveQueryService interactiveQueryService;

    private final OrderService orderService;

    public OrderController(InteractiveQueryService interactiveQueryService,
                           OrderService orderService) {
        this.interactiveQueryService = interactiveQueryService;
        this.orderService = orderService;
    }

    @PostMapping
    public String createOrder(@RequestBody OrderDTO orderDTO) {
        log.info("REST request to create order: {}", orderDTO);
        String str = this.orderService.createOrder(orderDTO);
        return str;
    }

}
