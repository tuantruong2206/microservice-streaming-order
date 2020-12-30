package vn.microservice.streaming.order.controller.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    public OrderController(InteractiveQueryService interactiveQueryService) {
        this.interactiveQueryService = interactiveQueryService;
    }
}
