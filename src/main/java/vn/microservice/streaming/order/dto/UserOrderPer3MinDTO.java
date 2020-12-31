package vn.microservice.streaming.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import vn.microservice.streaming.common.lib.dto.OrderStreamDTO;

import java.util.Date;
import java.util.List;

/**
 * @author Tuan.Truong [Brian]
 * @version 1.0
 * @date 12/31/2020 2:21 PM
 *
 */
@Data
@ToString
@Builder
@AllArgsConstructor
public class UserOrderPer3MinDTO {

    public UserOrderPer3MinDTO() {}
    private String user;
    private List<OrderStreamDTO> orderHistory;
    private Date start;
    private Date end;

    public void addOrder(OrderStreamDTO order, String user) {
        orderHistory.add(order);
        this.user = user;
    }


}
