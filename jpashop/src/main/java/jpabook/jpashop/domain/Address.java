package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    protected Address() {
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

    //임베디드 타입은 기본편에서도 배웠지만 세터를 사용하지 않고 생성자를 통해서 값을 넣는게 이상적임
    //또한 jpa스펙상 기본 생성자는 꼭 필요함.리플렉션이나 프록시같은 기술을 지원하기 위해서라고함
}
