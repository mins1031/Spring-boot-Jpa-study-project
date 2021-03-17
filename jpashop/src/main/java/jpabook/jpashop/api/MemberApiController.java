package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> memberList(){
        return memberService.findMembers();
    }//Member엔티티를 직접사용하여 큰 문제가따름 엔티티가 변경되면 api가 죽어버림
    //피피티에 문제점 나와있음.

    @GetMapping("/api/v2/members")
    public Result memberV2(){
        List<Member> members = memberService.findMembers();
        List<MemberDTO> collect = members.stream()
                .map(m -> new MemberDTO(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect.size(), collect);
    }

    @GetMapping("/api/v2/member/{id}")
    public Member findMemberV2(
            @PathVariable("id") Long id,
            @RequestBody FindMemberRequest request){

        Member member = memberService.findOne(id);
        return member;
    }

    @Data
    @AllArgsConstructor
    static class Result<T>{
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDTO{
        private String name;
    }

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(
            @RequestBody @Valid CreateMemberRequest request){
     //결국 1강에서 하고자 한 내용은 api에 엔티티를 직접 노출시키는건 유지보수 입장에서 에러나면 골치아파짐
     //서비스,리포지토리,엔티티까지 다 봐야함. 그래서 api에 맞는 dto를따로 하나 만들어서 값을 주고받는 형태
     //로만 사용하며 개발하는게 정석이고 유지보수 측면에서 큰 메리트가있음
     //=> 엔티티와 api스팩을 분리하려는게 가장큰이유임 엔티티의 변화로 api가 영향을 받아선 안되기에
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request){
//위에선 등록용 dto를 만들고 지금은 업데이트용 응답,요청 dto(UpdateMemberReq/Res)를
// 만들었음. 왜? 따로 만듬?
// -> 등록과 수정은 요구하는 api스팩 (등록시 사용하는 내용[id 등록시 생성, 비밀번호등..])이
// 다르기때문에 별도의 dto로 구성
        memberService.update(id,request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    static class FindMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest {
        private String name;
    }

        @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
