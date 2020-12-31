package vn.microservice.streaming.order.streams.topology;

import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import vn.microservice.streaming.common.lib.dto.OrderVerified;

import java.util.function.Function;

/**
 * @author Tuan.Truong [Brian]
 * @version 1.0
 * Class INFO
 * @date 12/31/2020 5:09 PM
 */
@EnableBinding
public class VerifiedOrderProcessorTopology {

    private final Logger log = LoggerFactory.getLogger(VerifiedOrderProcessorTopology.class);

    public Function<KStream<Object, OrderVerified>, KStream<?, OrderVerified>> verifiedOrderProcess() {
        return ;
    }

}
