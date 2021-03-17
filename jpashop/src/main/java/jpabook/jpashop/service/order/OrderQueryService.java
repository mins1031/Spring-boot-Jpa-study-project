package jpabook.jpashop.service.order;

import jpabook.jpashop.api.OrderApiController;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {

    /*public List<OrderDto> orderV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderApiController.OrderDto> result = orders.stream().map(o -> new OrderApiController.OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }*/
}
//OSIV 설정을 false로 두게 되면 영속성 컨택스트가 서비스 영역까지만 살아있게되
//컨트롤러에서 사용을 못함. = 그렇기에 서비스 단에 쿼리용 서비스를 하나 만들어 사용하는방식.
//무튼 핵심 비즈니스 로직 클래스들과 단순 api용 로직 클래스 들을 패키지별로 나눠놓는것이
//명시적으로 좋음