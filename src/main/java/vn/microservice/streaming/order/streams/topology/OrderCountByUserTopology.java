package vn.microservice.streaming.order.streams.topology;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.SessionWindows;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;
import vn.microservice.streaming.common.lib.dto.OrderStreamDTO;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author Tuan.Truong [Brian]
 * @version 1.0
 * @date 12/30/2020 11:13 AM
 * this class count order by user within window time
 * NOTE this class we are using the functional programming model (we can also use the imperative programming model that is the old fashion style with @streamListener)
 */
@EnableBinding
public class OrderCountByUserTopology {

    private final Logger log = LoggerFactory.getLogger(OrderCountByUserTopology.class);

    @Bean
    public Consumer<KStream<Byte, OrderStreamDTO>> orderCountByUserId() {
        return input -> input.peek((k, v) -> log.info("Received Order {}", v))
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
                .aggregate(() -> new OrderStreamDTO(),
                        (key, newV, aggValue) -> new OrderStreamDTO(aggValue.getUserid(), aggValue.getOrderId(), aggValue.getTicker(),
                                aggValue.getQuality() + newV.getQuality(),
                                aggValue.getAmount() + newV.getAmount(), newV.getStatus(), newV.getCreatedAt(), newV.getUpdatedAt()),
//                        (k, v, a) -> simpleMerge(v, a),
                        Materialized.<String, OrderStreamDTO, KeyValueStore<Bytes, byte[]>>as("order-verified-consumed-state-store")
                                .withKeySerde(Serdes.String()).withValueSerde(new JsonSerde<>(OrderStreamDTO.class)));

                ;
    }

    private OrderStreamDTO simpleMerge(OrderStreamDTO newValue, OrderStreamDTO aggValue) {
        return new OrderStreamDTO(aggValue.getUserid(), aggValue.getOrderId(), aggValue.getTicker(),
                aggValue.getQuality() + newValue.getQuality(),
                aggValue.getAmount() + newValue.getAmount(), aggValue.getStatus(), aggValue.getCreatedAt(), aggValue.getUpdatedAt());
    }
}
