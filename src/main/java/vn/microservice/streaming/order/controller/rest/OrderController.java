package vn.microservice.streaming.order.controller.rest;

import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import vn.microservice.streaming.common.lib.dto.OrderStreamDTO;
import vn.microservice.streaming.order.dto.OrderDTO;
import vn.microservice.streaming.order.service.OrderService;
import vn.microservice.streaming.order.streams.topology.OrderStoreProcessorTopology;

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
@RequestMapping(path = "order", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping("/{orderid}")
    public OrderStreamDTO getOrderbyId(@PathVariable("orderid") Long orderId) {
        final ReadOnlyKeyValueStore<Long, OrderStreamDTO> keyValueStore;
        OrderStreamDTO orderStreamDTO = null;

        final HostInfo host = interactiveQueryService.getHostInfo(OrderStoreProcessorTopology.MICROSERIVCE_ORDER_STATE_STORE, orderId, new LongSerializer());
        log.info("**** Query orderid {} stay on the host {}", orderId, host);

        if (interactiveQueryService.getCurrentHostInfo().equals(host)) {
            log.info("send request served from the same host:");
            keyValueStore = interactiveQueryService.getQueryableStore(OrderStoreProcessorTopology.MICROSERIVCE_ORDER_STATE_STORE, QueryableStoreTypes.<Long, OrderStreamDTO>keyValueStore());
            orderStreamDTO = keyValueStore.get(orderId);
        } else {
            log.info("send request served from different host: {}", host);
            RestTemplate restTemplate = new RestTemplate();
            orderStreamDTO = restTemplate.getForObject(String.format("http://%s:%d/%s", host.host(), host.port(), "/order/"+ orderId), OrderStreamDTO.class);
        }

        log.info("****||||*** QUERY result : {}", orderStreamDTO);
        return orderStreamDTO;
    }

}
