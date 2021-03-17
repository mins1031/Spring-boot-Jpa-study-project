package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    //order메서드에서 멤버의 정보를 가져와야 하기때문에 의존성 받아옴.
    //주문
    @Transactional
    public Long order(Long memberId, Long itemId, int count){

        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송 정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문 생성
        Order order = Order.createOrder(member, delivery , orderItem);

        //주문 저장
        orderRepository.save(order);

        return order.getId();
    }
    //주문에 필요한 정보들을 가공해 주문을 생성해줌. 주문하다라는 동작을 로직으로
    //구현했다고 생각하면됨. 위의 부분들은 엔티티에서 가진 정보들만으론 해결할수
    //없는 부분들이기 때문에 서비스에 비즈니스 로지 구현함.

    //취소
    @Transactional
    public void cancelOrder(Long orderId){
        //해당하는 주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        //주문 취소
        order.cancel();
    }
    //검색
    public List<Order> findOrders(OrderSearch orderSearch)
    {
        return orderRepository.findAllByCriteria(orderSearch);
    }
}
