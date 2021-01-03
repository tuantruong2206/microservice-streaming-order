package vn.microservice.streaming.order.streams.topology;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.SessionWindows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;
import vn.microservice.streaming.common.lib.dto.VerifiedOrderStreamDTO;
import vn.microservice.streaming.order.dto.OrderValidationPer3MinDTO;

import java.time.Duration;
import java.util.function.Function;

/**
 * @author Tuan.Truong [Brian]
 * @version 1.0
 * This class aggregate order validation from payment and shipping services to create completed order
 * @date 12/31/2020 5:09 PM
 */
@EnableBinding
public class VerifiedOrderProcessorTopology {

    private final static Logger log = LoggerFactory.getLogger(VerifiedOrderProcessorTopology.class);
    private final static String ORDER_COMPLETE_TOPIC = "microservice-order-complete";

    @Bean
    public Function<KStream<Object, VerifiedOrderStreamDTO>, KStream<?, OrderValidationPer3MinDTO>> verifiedOrderProcess() {
        return input -> input.peek((k, v) -> log.info("+++ order verified receive order validation {}", v))
                .map((k, v) -> new KeyValue<>(v.getOrderId(), v))
                .groupByKey()
//                .groupByKey(Grouped.with(Serdes.String(), new JsonSerde<>(VerifiedOrder.class)))
                .windowedBy(SessionWindows.with(Duration.ofMinutes(3)))
                .aggregate(() -> new OrderValidationPer3MinDTO(),
                        (k, nValue, aggValue) ->   {
                            aggValue.addVerifiedOrder(nValue);
                            return aggValue;
                        },
                        (k, nValue, aggValue) -> aggValue)
                .filter((k, v) -> v.getCount() >=3)
                .toStream((k, v) -> k.key())
                .peek((k, v) -> log.info("??? verifiedOrderProcess out to topic ORDER COMPLETE {}", v));
//                .map((k, v) -> new KeyValue<>(k, v));
//                .to(ORDER_COMPLETE_TOPIC, Produced.with(Serdes.Long(), new JsonSerde<>(OrderValidationPer3MinDTO.class)));
    }

}
