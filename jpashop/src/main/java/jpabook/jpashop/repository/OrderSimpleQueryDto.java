package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderSimpleQueryDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public OrderSimpleQueryDto(Long orderId, String name, LocalDateTime orderDate,
                                   OrderStatus orderStatus, Address address){
            this.orderId= orderId;
            this.name = name;
            this.orderDate = orderDate;
            this.orderStatus = orderStatus;
            this.address = address;
        }
        //dto가 엔티티를 파라미터로 받는건 크게 문제 되지 않음. 별로 중요하지 않은 곳이기에
        //즉 외부에 노출되지 않으면 = api와 연관이 되지 않으면 됨.

}
