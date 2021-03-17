package jpabook.jpashop.service;

import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.domain.Member;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.plaf.metal.MetalMenuBarUI;
import java.util.List;

@Service
@Transactional(readOnly = true)
//jpa는 기본적으로 모든 데이터 변경은 트랜잭션 안에서 이루어져야하기에
//@Transactional 어노테이션 필요
//jpa에선 @Transactional(readOnly=true)사용하면조회기능에선최적화를해줌
//그래서 조회기능 많은 클래스에선 위처럼 하면 모든 메서드에 readonly가 적용됨
public class MemberService {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    //회원 가입
    @Transactional
    //그리고 readOnly기능을 하면 안되는 메서드에는 @Transactional만 적용하면
    //그대로 사용됨
    public Long join(Member member) {

        validateDuplicateMember(member);
        memberRepository.save(member);//1
        return member.getId();//2
        //왜 1후에 2를 적용하는가? 이건 1을 적용하면 member를 영속화 함 다만
        //디비에는 값이 안들어가지만GeneratedValue를 통해 영컨에서 pk가 생성
        //되어 저장됨. 그렇기에 1후에 2에서 id를 뽑아올수 있음. 영컨은 키벨류형태기에
        //미리 잡아줌.
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //회원 전체 조회
    public List<Member> findMembers(){

        return memberRepository.findAll();
    }

    public Member findOne(Long id){
        return memberRepository.findOne(id);
    }

    @Transactional
    public void update(Long id , String name){
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }
}
