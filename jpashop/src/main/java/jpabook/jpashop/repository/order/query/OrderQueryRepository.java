package jpabook.jpashop.repository.order.query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos(){
        List<OrderQueryDto> result = findOrders();
        //order가 10면 -> 1쿼리  10order
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        }); //10order 이므로 10개의 order에 맞는 item들을 넣어줌. => 10쿼리 실행
        return result;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order." +
                        "query.OrderItemQueryDto(oi.order.id, i.name, " +
                        "oi.orderPrice, oi.count) " +
                        "from OrderItem oi " +
                        " join oi.item i" +
                        " where oi.order.id = :orderId",OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    public List<OrderQueryDto> findOrders(){
       return em.createQuery(
                "select new jpabook.jpashop.repository.order.query." +
                        "OrderQueryDto(o.id,m.name,o.orderDate,o.status,d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d ", OrderQueryDto.class)
                .getResultList();
    }

    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> result = findOrders();
        //오더를 다 가져오고
        List<Long> orderIds = result.stream().map(o -> o.getOrderId())
                .collect(Collectors.toList());
        //Long타입의 주문번호를 다 가져와 리스트로 만듬
        List<OrderItemQueryDto> orderItems = em.createQuery(
                "select new jpabook.jpashop.repository.order." +
                        "query.OrderItemQueryDto(oi.order.id, i.name, " +
                        "oi.orderPrice, oi.count) " +
                        "from OrderItem oi " +
                        " join oi.item i" +
                        " where oi.order.id in :orderIds ",OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();
        //한주문서의 아이템 내용을 orderItems 리스트로 받게됨.

        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream().
                collect(Collectors.groupingBy(orderItemQueryDto ->
                        orderItemQueryDto.getOrderId()));
        //key = orderId , value = OrderItemQueryDto 인 Map형태로 리스트를 바꾸어줌
        result.forEach(o ->
                o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;

    }

    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery("select new" +
                " jpabook.jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate,o.status, d.address, i.name, oi.orderPrice, oi.count) " +
                " from Order o " +
                " join o.member m " +
                " join o.delivery d " +
                " join o.orderItems oi " +
                " join oi.item i", OrderFlatDto.class)
                .getResultList();
    }
}
//이처럼 레파지토리 성격을 일반 레파지토리와 쿼리로 나눠놓는 이유는 핵심 비즈니스 레퍼지토리와
//api에 최적화된= 화면에 대한 쿼리를 관리하는 레퍼지토리를 나눠놔야 관리하기가 편함.