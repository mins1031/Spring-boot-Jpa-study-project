package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;
    
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1(){

        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o->o.getItem().getName());
        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<OrderDto> result = orders.stream().map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    } //주문조회 v2강에서 보여주려는 최종목표는 위의 로직대로 구성해 엔티티 데이터를 조호하면 쿼리가
    //많이 나감. order 리스트 조회시 order 2개 , order -> dto로 바꾸는 과정에서
    // 각 오더마다 member,delevery, 그리고 orderItem 2개 까지 해서 10개 가까운 쿼리가 나가게됨
    // 전 단원과 과정이 비슷하지만 전 단원은 @*ToOne에 대한 즉 객체 하나가 있는 엔티티의 최적화 강의였고
    //이번 단원은 컬랙션이 포함된 엔티티의 조회성능 최적화에 대한 이야기임.

    @GetMapping("/api/v3/orders")
    public List<OrderDto> orderV3(){
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> result = orders.stream().map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }
    //orderV2 와 orderV3의 코드는 동일함. 근데 두 메서드의 쿼리횟수 차이는 1+n과 1임
    //레포지토리 단에서 패치조인으로 조회쿼리 성능튜닝을 했냐 안했냐로 성능차이가 확 나버림

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> orderV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit)
    {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset,limit);
        List<OrderDto> result = orders.stream().map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> orderV4(){
        return orderQueryRepository.findOrderQueryDtos();
    }

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> orderV5(){
        return orderQueryRepository.findAllByDto_optimization();
    }

    @GetMapping("/api/v6/orders")
    public List<OrderFlatDto> orderV6(){
        return orderQueryRepository.findAllByDto_flat();
    }

    @Data
    static class OrderDto{

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
            .map(orderItem -> new OrderItemDto(orderItem))
            .collect(Collectors.toList());
        }
    }//api에 맞춰 엔티티를 dto로 변환해야 하기에 dto정의해주고 생성자역시 정의하는데
     //많은 개발자들이 간과하는부분이 dto구성시에도 엔티티가 포함이 되선안됨 위에 만약
    //List<OrderItem>이 된다면 엔티티가 그대로 List에 구성되고 dto에 포함되어 화면으로 출력되게됨
    //엔티티와 화면을 분리한다는 말은 아예 접점없이 엔티티가 화면으로 나가면 안되는것임 단순히
    //컨트롤러 메서드 리턴값을 dto로 한다고 되는것이 아님. 간과하지 말것!
    //컨트롤러에서 항상 주의해서 엔티티가 출력되는지 확인할것 모든 엔티티는 dto로 랩핑되어 나가야함

    @Data
    static class OrderItemDto {

        private String itemName;
        private int orderPrice;
        private int count;
        //왜 OrderItem 엔티티의 컬럼들이 다 없냐면 api에서 원하는 값이 이름,주문가격,수량만
        //있다고 가정하고 수업진행하셔서 그럼. api에서 요구하지 않는 데이터는 불필요하게 분담할
        //필요가 없ㅇ,ㅁ
        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
 }
