package vn.microservice.streaming.order.streams.topology;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;
import vn.microservice.streaming.common.lib.dto.OrderStreamDTO;
import vn.microservice.streaming.common.lib.enumeration.Status;

import java.time.Instant;
import java.util.function.Consumer;

/**
 * @author Tuan.Truong [Brian]
 * @version 1.0
 * This class helps to save the order to microservice-order-state-store
 * @date 1/1/21 12:26
 */
@EnableBinding
public class OrderStoreProcessorTopology {

    private final Logger log = LoggerFactory.getLogger(OrderStoreProcessorTopology.class);

    private static final String MICROSERIVCE_ORDER_STATE_STORE = "microservice-order-state-store";

    /**
     * This method helps to save order request to local state store and backed up to kafka broker with initialize status PENDING
     * @return
     */
    @Bean
    public Consumer<KStream<Byte, OrderStreamDTO>> orderStoreProcess() {
        return input -> input.peek((k, v) -> log.info("receive order {} then save to local state store", v))
                .map((k, v) -> new KeyValue<>(v.getOrderId(), v)).groupByKey(Grouped.with(Serdes.Long(), new JsonSerde<>(OrderStreamDTO.class)))
                .aggregate(() -> new OrderStreamDTO(),
                        (k, nV, aggV) -> updateOrderStatus(nV),
                        Materialized.<Long, OrderStreamDTO, KeyValueStore<Bytes, byte[]>>as(MICROSERIVCE_ORDER_STATE_STORE).withKeySerde(Serdes.Long()).withValueSerde(new JsonSerde<>(OrderStreamDTO.class)));
    }

    /**
     * should use mapper lib instead
     * @param orderStreamDTO
     * @return
     */
    private OrderStreamDTO updateOrderStatus(OrderStreamDTO orderStreamDTO) {
        return new OrderStreamDTO(orderStreamDTO.getUserid(), orderStreamDTO.getOrderId(), orderStreamDTO.getProdId(), orderStreamDTO.getQuality(), orderStreamDTO.getAmount(), Status.PENDING, Instant.now(), Instant.now());
    }
}
