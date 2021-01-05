package vn.microservice.streaming.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import vn.microservice.streaming.common.lib.enumeration.Status;

import java.time.Instant;

/**
 * @author Tuan.Truong [Brian]
 * @version 1.0
 * @date 12/30/2020 10:49 AM
 * Order Data Transfer Object
 */

@Data
@ToString
@Builder
@AllArgsConstructor
public class OrderDTO {
    public OrderDTO() {}
    private String userid;
    private Long orderId;
    private String prodId;
    private Long quality;
    private Double amount;
    private Status status;
    private Instant createdAt;
    private Instant updatedAt;
}
