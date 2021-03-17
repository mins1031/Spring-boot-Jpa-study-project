package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class OrderRepository {

    private EntityManager em;

    @Autowired
    public void OrderRepository(EntityManager em){

        this.em = em;
    }

    @Transactional
    public void save(Order order){
         em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }

    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
        List<Predicate> criteria = new ArrayList<>();
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" +
                            orderSearch.getMemberName() + "%");
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대1000건
        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d", Order.class).getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset,int limit) {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<OrderSimpleQueryDto> findOrderDtos(){
        return em.createQuery(
                "select new jpabook.jpashop.repository.OrderSimpleQueryDto(o.id," +
                        "m.name,o.orderDate,o.status,d.address)"+
                " from Order o" +
                " join o.member m" +
                " join o.delivery d", OrderSimpleQueryDto.class)
        .getResultList();
    } //select절엔 엔티티나 임베디드 타입의 클래스만 단순 사용이 가능함. 위와같이 일반 클래스를
    // 사용하려면 new 명령어를 사용해야함. select절에서 원하는 데이터를 직접 선택해서 해당하는것만
    //가져오므로 네트웍 용량이 최적화되지만 큰 차이는 없다고함. 최적화면에선v4지만 api스펙에 맞춰진
    //코드가 리포지토리에 들어가는 찝찝한 단점이 있음(@JsonIgnore같은것)

    public List<Order> findAllWithItem() {
        return em.createQuery(
                " select distinct o from Order o " +
                        " join fetch o.member m " +
                        " join fetch o.delivery d" +
                        " join fetch o.orderItems oi " +
                        " join  fetch oi.item" , Order.class)
                .getResultList();
    }

}
