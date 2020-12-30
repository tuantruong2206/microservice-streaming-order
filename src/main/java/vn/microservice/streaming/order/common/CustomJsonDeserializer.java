package vn.microservice.streaming.order.common;

import org.springframework.kafka.support.serializer.JsonDeserializer;

/**
 * @author Tuan.Truong [Brian]
 * @version 1.0
 * @date 12/30/2020 10:23 AM
 */
public class CustomJsonDeserializer extends JsonDeserializer {
    public CustomJsonDeserializer() {
// defaults from superclass
        super();
// * to trust all
        this.addTrustedPackages("*");
    }
}
