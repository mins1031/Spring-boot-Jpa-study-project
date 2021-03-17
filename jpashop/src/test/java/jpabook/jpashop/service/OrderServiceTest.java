package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void order() throws Exception {
        //테스트 목적 : 주문이 잘 생성되는지
        //given
        Member member = createMember();

        Book book = createBook("min JPA",10000, 10);
        //when

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(),book.getId(),orderCount);
        //가상데이터를 주고 주문을 생성해 해당 주문의 id값을 받음.
        //then
        //주문이 생성되었다면 주문을 조회해봄
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("상품주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("주문한 상품 종류수 가 정확해야 한다",1,getOrder.getOrderItems().size());
        //상품을 한가지만 주문했는가? 여기서 상품가지= orderItem임.동일한 제품을 많이 사는걸 물어보는게 아닌 주문에있는 서로다른 제품들을 판별
        assertEquals("주문 가격은 가격 * 수량이다",10000 * orderCount, getOrder.getTotalPrice());
        assertEquals("주문 수량 만큼 재고가 줄어야 한다.",10 - orderCount,book.getStockQuantity());
        //assertEquals메서드(두값이 같은 지 판별)의 인자는 ("출력을 원하는 문자열",출력 기대값, 실제값)이렇게구성됨됨
        //위의 메서드로 주문이 생성되었으면 세번쨰 인자에 null이 안나오고 OrderStatus가 ORDER로 되어있어 잘 실행되겠지만 두값이 틀리거나
        // 세번쨰값이 null이면 테스트가 싫패함
        //예로 주문수량을 10 - orderCount에서 그냥 10으로 바꾸면 에러남.
    }

    @Test(expected = NotEnoughStockException.class)
    public void 상품주문_재고수량초과() throws Exception {
        //given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);

        int orderCount = 11;
        //when
        orderService.order(member.getId(), item.getId(), orderCount);

        fail("재고 수량 부족 예외가 발생해야 함");
        //이쪽은 재고수량(stockQuantity < orderCount)가 성립해야 즉 재고보다 주문수량이 많아야 테스트가 성공함.
        //왜냐면 fail라인까지 오면 테스트가 실패한 것이기에 그럼 주문수량이 많다면 69라인에서 예외가 발생하고 위의 어노테이션 설정으로
        //처리가됨.
    }

    @Test
    public void orderCancel() throws Exception {
        //given
        Member member = createMember();
        Book item = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);
        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("주문 취소시 상태는 CANCEL", OrderStatus.CANCEL,getOrder.getStatus());
        assertEquals("주문 취소시 재고수량은 복구가 되어야한다", 10,item.getStockQuantity());
        //주문시 일어나는 동작들을 반대로 적용한 것들 즉 주문 이전으로 값이 돌아와야함.
    }

    @Test
    public void quantityOver() throws Exception {

    }

    private Member createMember(){
        Member member  = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "경기","123"));
        em.persist(member);
        return member;
    }

    private Book createBook(String name, int price,int stockQuantity){

        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }
}