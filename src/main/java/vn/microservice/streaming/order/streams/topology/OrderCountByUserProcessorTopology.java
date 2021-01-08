package vn.microservice.streaming.order.streams.topology;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.SessionWindows;
import org.apache.kafka.streams.state.SessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;
import vn.microservice.streaming.common.lib.dto.OrderStreamDTO;
import vn.microservice.streaming.order.dto.UserOrderPer3MinDTO;
import java.time.Duration;
import java.util.function.Consumer;

/**
 * @author Tuan.Truong [Brian]
 * @version 1.0
 * @date 12/30/2020 11:13 AM
 * this class count order by user within window time
 * NOTE this class we are using the functional programming model (we can also use the imperative programming model that is the old fashion style with @streamListener)
 */
@EnableBinding
public class OrderCountByUserProcessorTopology {

    private final Logger log = LoggerFactory.getLogger(OrderCountByUserProcessorTopology.class);

    public static final String MICROSERVICE_STATE_STORE_USER_ORDER_3_MIN = "state-store-user-order-3-min";

    /**
     * This method helps to count and store order by user with 3 min to local state store and backed up to kafka broker
     * @return
     */
    @Bean
    public Consumer<KStream<Byte, OrderStreamDTO>> orderCountByUserIdProcess() {
        return input -> input.peek((k, v) -> log.info("Received Order for 3MIN {}", v))
                .map((k, v) -> new KeyValue<>(v.getUserid(), v))
                .groupByKey(Grouped.with(Serdes.String(), new JsonSerde<>(OrderStreamDTO.class)))
                //WHY do we have serdes here?
                /*
                 * every operator that does not use default Serdes
                 * from StreamsConfig need to set the correct Serdes.
                 *
                 * */
                //https://stackoverflow.com/questions/47569359/getting-class-cast-exception-in-kafka-stream-api
                .windowedBy(SessionWindows.with(Duration.ofMinutes(3)))
                .aggregate(() -> new UserOrderPer3MinDTO(),
                        /**
                         * newValue OrderStreamDTO
                         * aggValue UserOrderPer3MinDTO
                         */
                        (key, newValue, aggValue) -> {
                            aggValue.addOrder(newValue, key);
                            return aggValue;
                        },
                        (key, newValue, aggValue) -> {
                            return aggValue;
                        },
                        Materialized.<String, UserOrderPer3MinDTO, SessionStore<Bytes, byte[]>>as(MICROSERVICE_STATE_STORE_USER_ORDER_3_MIN)
                                .withKeySerde(Serdes.String()).withValueSerde(new JsonSerde<>(UserOrderPer3MinDTO.class)));
    }

    private OrderStreamDTO simpleMerge(OrderStreamDTO newValue, OrderStreamDTO aggValue) {
        return new OrderStreamDTO(aggValue.getUserid(), aggValue.getOrderId(), aggValue.getProdId(),
                aggValue.getQuality() + newValue.getQuality(),
                aggValue.getAmount() + newValue.getAmount(), aggValue.getStatus(), aggValue.getCreatedAt(), aggValue.getUpdatedAt());
    }
}
