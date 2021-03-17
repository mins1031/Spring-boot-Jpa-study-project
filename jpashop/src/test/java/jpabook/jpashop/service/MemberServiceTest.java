package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
//JUnit쓸때 spring이랑 엮어서 사용한다는 어노테이션
@SpringBootTest
//spring 컨테이너 안에서 해당 테스트를 돌린다는 의미
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void join() throws Exception{

        //given = 이ㅣ런게 주어졌을때
        Member mem = new Member();
        mem.setName("kim");

        //when = 이렇게 하면
        Long saveId = memberService.join(mem);

        //then = 이렇게 된다 라는 문맥?스타일 임
        assertEquals(mem, memberRepository.findOne(saveId));
    }

    @Test(expected = IllegalStateException.class)
//테스트 코드에서 IllegalStateException이 뜨면 try catch를 자동으로 적용해줌
    public void duplicateJoin() throws Exception {

        //given = 이런게 주어졌을때
        Member mem1 = new Member();
        mem1.setName("kim1");
        Member mem2 = new Member();
        mem2.setName("kim1");

        //when = 이렇게 하면
        memberService.join(mem1);
        memberService.join(mem2);//예외가 터져야함
        //then = 이렇게 된다 라는 문맥?스타일 임
        Assert.fail("예외가 발생해야 한다");
        //해당 코드라인으로 오면 무조건 에러를 발생시킴
    }
}