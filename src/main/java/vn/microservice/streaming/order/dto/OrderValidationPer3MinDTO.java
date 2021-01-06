package vn.microservice.streaming.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import vn.microservice.streaming.common.lib.dto.VerifiedOrderStreamDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tuan.Truong Brian
 * @version 1.0
 * Class INFO
 * @date 1/1/21 14:46
 */
@Data
@ToString
@Builder
@AllArgsConstructor
public class OrderValidationPer3MinDTO {
    private Long count;
    List<VerifiedOrderStreamDTO> verifiedOrders;
    List<Long> orderIds;
    List<String> userIds;

    public OrderValidationPer3MinDTO() {
        verifiedOrders = new ArrayList<>();
        orderIds = new ArrayList<>();
        userIds = new ArrayList<>();
        count = new Long(0);
    }

    public void addVerifiedOrder(VerifiedOrderStreamDTO verifiedOrder) {
        this.verifiedOrders.add(verifiedOrder);
        orderIds.add(verifiedOrder.getOrderId());
        userIds.add(verifiedOrder.getUserId());
        count ++;
    }
}
