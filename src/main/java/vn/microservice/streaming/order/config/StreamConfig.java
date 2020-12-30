package vn.microservice.streaming.order.config;

import org.springframework.cloud.stream.annotation.EnableBinding;
import vn.microservice.streaming.order.streams.OrderStream;

/**
 * @author Tuan.Truong [Brian]
 * @version 1.0
 * @date 12/30/2020 11:05 AM
 * Enable binder order streaming which is configured in yml file, we can use StreamBridge instead to simplify and dynamic binder
 * If so, we don't need this file any more
 */
@EnableBinding(OrderStream.class)
public class StreamConfig {
}
