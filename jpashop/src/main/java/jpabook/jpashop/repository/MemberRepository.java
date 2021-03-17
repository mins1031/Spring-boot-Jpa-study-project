package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
//reposiory를 스프링 빈에 등록해주는 어노테이션
public class MemberRepository {

    //@PersistenceContext//jpa를 사용하기 때문에 사용. 스프링이 EM을 만들어 주입해줌
    private EntityManager em;

    @Autowired
    //스프링jpadata 에선 @Autowired써줘도 @PersistenceContext처럼 동작함
    //이걸로 @RequiredArgsConstructor를 사용해도 되지만 가시성을 고려해 하지않음.
    public MemberRepository(EntityManager em){
        this.em = em;
    }

    public void save(Member member){
        em.persist(member);
    }

    public Member findOne(Long id){
        return em.find(Member.class, id);
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name = :name",Member.class)
                .setParameter("name",name)
                .getResultList();
    }

}
