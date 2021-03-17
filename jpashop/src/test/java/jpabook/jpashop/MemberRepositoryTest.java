package jpabook.jpashop;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberRepositoryTest {

    /*@Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional//스프링에선 Transactional 어노테이션이 테스트에 있으면 테스트가 끝나고 롤백을 해버림 그래서 롤백 설정도 같이 해줘야함
    @Rollback(false) //롤백안하고 커밋을 해줌
    public void test() throws Exception{
        Member member = new Member();
        member.setName("memberA");
        Long savedId = memberRepository.save(member);
        Member findMember = memberRepository.find(savedId);
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());

        Assertions.assertThat(findMember.getName()).isEqualTo(member.getName());
        // 테이블생성과 데이터 삽입이 잘 이루어졌으면 jpa가 잘 동작한다는걸 확인가능
        Assertions.assertThat(findMember).isEqualTo(member);
        //웨에서 생성한 member와 find한 멤버가 같은지 확인 => true여야 함 영속성 컨테스트에 id가 동일한 객체가 있으면 jpa는
        //같다고 판단함.
    }*/
}