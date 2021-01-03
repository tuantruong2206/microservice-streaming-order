package vn.microservice.streaming.order.streams;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author Tuan.Truong [Brian]
 * @version 1.0
 * @date 12/30/2020 10:55 AM
 * This class create order stream binder to connect to Message broker, we can also use StreamBridge instead to simplify and dynamic binder
 * @Autowired
 * private StreamBridge streamBridge;
 */
public interface OrderStream {
    final String INPUT = "rest-order-in";

    final String OUTPUT = "rest-order-out";
    @Input(INPUT)
    SubscribableChannel inboundOrder();

    @Output(OUTPUT)
    MessageChannel outboundOrder();
}
