package common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {
    private String orderId;
    private String userName;
    private Long createTime;
    private Integer amount;
}
