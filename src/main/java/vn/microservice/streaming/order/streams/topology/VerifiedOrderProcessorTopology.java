package vn.microservice.streaming.order.streams.topology;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.SessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;
import vn.microservice.streaming.common.lib.dto.OrderValidationPer3MinStreamDTO;
import vn.microservice.streaming.common.lib.dto.VerifiedOrderStreamDTO;

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

    /**
     * send completed signal message to topic microservice-completed-orders for sending email
     * @return
     */
    @Bean
    public Function<KStream<Long, VerifiedOrderStreamDTO>, KStream<?, OrderValidationPer3MinStreamDTO>> verifiedOrderProcess() {
        return input -> input.peek((k, v) -> log.info("+++ order verified receive order validation with KEY {} value {}", k, v))
                // no need to re-map the key, because payment, inventory and shipping services produce key by orderid and value VerifiedOrderStreamDTO
//                .map((k, v) -> new KeyValue<>(v.getOrderId(), v))
                .groupByKey()
//                .groupByKey(Grouped.with(Serdes.String(), new JsonSerde<>(VerifiedOrder.class)))
                .windowedBy(SessionWindows.with(Duration.ofMinutes(3)))
                .aggregate(() -> new OrderValidationPer3MinStreamDTO(),
                        (k, nValue, aggValue) ->   {
                            aggValue.addVerifiedOrder(nValue);
                            return aggValue;
                        },
                        (k, nValue, aggValue) -> aggValue,
                        Materialized.<Long, OrderValidationPer3MinStreamDTO, SessionStore<Bytes, byte[]>>as("agg-verified-order-3-min").withKeySerde(Serdes.Long()).withValueSerde(new JsonSerde<>(OrderValidationPer3MinStreamDTO.class)))
                .filter((k, v) -> v.getCount() >=3 || v == null)
                //solution 2 go with function programming
                .toStream((k, v) -> k.key()).map((k, v) -> new KeyValue<>(null, v))
                .peek((k, v) -> log.info("===== RESOLVE COMPLETED ORDER  ====== KEY {} === VALUE {}", k, v));
                //solution 1 go with consumer, need to create kafka topic ORDER_COMPLETE_TOPIC in advance
//                .toStream((k, v) -> k.key())
//                .peek((k, v) -> log.info("??? verifiedOrderProcess out to topic ORDER COMPLETE {}", v));
//                .map((k, v) -> new KeyValue<>(k, v));
//                .to(ORDER_COMPLETE_TOPIC, Produced.with(Serdes.Long(), new JsonSerde<>(OrderValidationPer3MinDTO.class)));
    }

}
