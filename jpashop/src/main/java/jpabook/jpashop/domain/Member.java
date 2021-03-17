package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
    //연관관계의 주인을 정해서 하면 멤버에서는 orders에 대한 내용의 조회밖에 불가
    //하고 order에서 조회수정삭제등이 가능한데. 이렇게 해놓지 않으면 사용시 굉징히
    //헷갈림 멤버를 건드렸는데 오더의 값이 디비에서 변경되면 헷갈려짐. 그렇기에
    //멤버에서 값을 변경,삭제하면 그 ㅔ이블의 값이 변경되는 것이기에 덜 헷갈림
    //그래서 주인을 설정해 권한을 제한해 놓는것임.
}
