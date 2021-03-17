package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DTYPE")
@Getter @Setter
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categoryList = new ArrayList<>();

    //비즈니스 로직

    //stock(재고) 증가
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    //stock(재고) 감소
    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0 ){
            throw new NotEnoughStockException("need more stock");
        }//수량이 감소될떄 0이하면 안되기에 로직 걸어놈
        this.stockQuantity = restStock;
    }

    //비즈니스 로직 끝.
    //보통 mybatis로 사용할땐 이렇게 못했지만 객체지향적으로 생각했을때 해당 값을 가지고 있는 클래스에서 동작을 미리 준비하는게
    //더 이상적이어서 비즈니스 로직을 구성해놓았다고함
}
